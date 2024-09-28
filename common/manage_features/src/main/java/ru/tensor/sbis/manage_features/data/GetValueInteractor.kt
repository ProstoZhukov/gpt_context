package ru.tensor.sbis.manage_features.data

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Интерактор для взаимодействия с источником данных [DataSource]
 * @property dataSource источник данных, к которому происходит обращение
 */
internal class GetValueInteractor @Inject constructor(private val dataSource: DataSource) {

    /**
     * Получить значение доступности функционала, с предварительной проверкой доступности запроса значения
     * @param featureName название функционала (захардкожено в облаке)
     * @param userID идентификатор авторизованного пользователя
     * @param clientID идентификатор клиента (компании пользователя)
     */
    fun getValueWithCheck(featureName: String, userID: Int, clientID: Int): Single<String> {
        return checkEnabled(featureName, userID, clientID)
            .flatMap {
                if (!it) throw IllegalAccessException("Hasn't access")
                getValue(featureName, userID, clientID)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Проверить доступенн ли запрос кода функционала
     * @param featureName название функционала (захардкожено в облаке)
     * @param userID идентификатор авторизованного пользователя
     * @param clientID идентификатор клиента (компании пользователя)
     */
    private fun checkEnabled(featureName: String, userID: Int, clientID: Int) =
        Single.fromCallable { dataSource.isManageFeaturesEnabled(featureName, userID, clientID) }

    /**
     * Получить значение доступности функционала
     * @param featureName название функционала (захардкожено в облаке)
     * @param userID идентификатор авторизованного пользователя
     * @param clientID идентификатор клиента (компании пользователя)
     */
    private fun getValue(featureName: String, userID: Int, clientID: Int) =
        Single.fromCallable { dataSource.getManageFeature(featureName, userID, clientID).orEmpty() }
            .map {
                if (it.isEmpty()) throw IllegalAccessException("Hasn't access")
                it
            }
}