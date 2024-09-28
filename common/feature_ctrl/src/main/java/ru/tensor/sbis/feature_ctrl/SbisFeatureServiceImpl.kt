package ru.tensor.sbis.feature_ctrl

import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import ru.tensor.sbis.common.util.runSafely
import ru.tensor.sbis.feature_service.generated.Feature
import ru.tensor.sbis.feature_service.generated.FeatureOnSyncCallback
import ru.tensor.sbis.feature_service.generated.FeatureUpdateEvent
import ru.tensor.sbis.feature_service.generated.Specification
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber

/**
 * Класс для работы с сервисом Управление функционалом feature-ctrl.
 *
 * @author mb.kruglova
 */
internal class SbisFeatureServiceImpl(
    private val loginInterface: LoginInterface,
    private val controller: Lazy<FeatureUpdateEvent> = lazy { FeatureUpdateEvent.instance() }
) : SbisFeatureService {

    /**
     * Метод, который возвращает статус включенности фичи по её наименованию.
     *
     * @param featureName: Имя соответствующей фичи.
     * @return состояние включенности фичи.
     */
    override fun isActive(featureName: String): Boolean {
        val (user, client) = getAccountPair()
        return runSafely(
            "Cannot check feature availability of $featureName for userId $user, clientId $client"
        ) {
            Feature.hasOn(featureName, client, user)
        } ?: false
    }

    /**
     * Метод, который возвращает значение фичи по её наименованию.
     *
     * @param featureName: Наименование фичи.
     * @return значение фичи. Может вернуть пустое значение, если у фичи оно отсутствует.
     */
    override fun getValue(featureName: String): String? {
        val (user, client) = getAccountPair()
        return runSafely("Cannot check feature value of $featureName for userId $user, clientId $client") {
            Feature.getValue(featureName, client, user)
        }
    }

    /**
     * Optional метод для подписки на получение информации о фичах.
     *
     * @param featureList: Список наименований фич, информацию о которых хотим получить.
     * @return [Observable] для подписки на получение информации о фичах.
     */
    override fun getFeatureInfoObservable(featureList: List<String>): Observable<SbisFeatureInfo> =
        Observable.create { emitter ->
            featureList.forEach { feature ->
                val callback = object : FeatureOnSyncCallback(feature) {
                    override fun onEvent(spec: Specification) {
                        val newInfo = SbisFeatureMapper().map(spec)
                        if (!emitter.isDisposed) emitter.onNext(newInfo)
                    }
                }

                val subscription = controller.value.featureOnSync(feature).subscribe(callback)

                emitter.setCancellable { subscription.disable() }

                subscription.enable()
            }
        }

    /**
     * Optional метод получения Flow, в котором публикуется информация о фичах.
     *
     * @param featureList: Список наименований фич, информацию о которых хотим получить.
     * @return [Flow], в котором публикуются результаты загрузки информации о фичах.
     */
    override fun getFeatureInfoFlow(featureList: List<String>): Flow<SbisFeatureInfo> =
        callbackFlow {
            featureList.forEach { feature ->
                val callback = object : FeatureOnSyncCallback(feature) {
                    override fun onEvent(spec: Specification) {
                        val newInfo = SbisFeatureMapper().map(spec)
                        channel.trySendBlocking(newInfo)
                            .onFailure { exception ->
                                Timber.e(exception, "Unable to get info about toggle features")
                            }
                    }
                }
                val subscription = controller.value.featureOnSync(feature).subscribe(callback)

                awaitClose { subscription.disable() }
            }
        }.flowOn(Dispatchers.IO)

    /**
     * Функция получения данных об аккаунте пользователя (userId и clientId).
     */
    private fun getAccountPair(): Pair<Long?, Long?> {
        val userAccount = loginInterface.getCurrentAccount()
        val user = userAccount?.userId?.toLong()
        val client = userAccount?.clientId?.toLong()
        return Pair(user, client)
    }
}