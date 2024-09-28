package ru.tensor.sbis.business.common.domain.interactor.impl

import androidx.annotation.VisibleForTesting
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.internal.schedulers.SingleScheduler
import ru.tensor.sbis.business.common.data.base.BaseCrudRepository
import ru.tensor.sbis.business.common.domain.filter.Filter
import ru.tensor.sbis.business.common.domain.filter.HashFilter
import ru.tensor.sbis.business.common.domain.filter.SearchListFilter
import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallback
import ru.tensor.sbis.business.common.domain.interactor.RequestInteractor
import ru.tensor.sbis.business.common.domain.interactor.SearchInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadResult
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.viewmodel.UpdateCause
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import javax.inject.Inject

/**
 * Абстрактная реализация интерактора получения данных через CRUD фасад.
 * Используется для получения данных на методах с синхронизатором через [DataRefreshCallback].
 *
 * @property CPP_DATA тип данных контроллера
 * @property DATA тип получаемых данных java/kotlin
 * @property CPP_FILTER тип фильтра контроллера
 * @property FILTER тип фильтра интерактора
 *
 * @param filter фильтр [FILTER] для преобразования в фильтр контроллера [CPP_FILTER]
 * @param repository репозиторий CRUD фасада [REPO]
 * @param mapper списочный маппер моделей контроллера [CPP_DATA] в UI модели [DATA]
 * @property networkUtils утилиты для работы с сетью
 * @property hasPermissionError true, если во время последней синхронизации была получена ошибку доступа
 */
abstract class AbstractRequestInteractor<REPO : BaseCrudRepository<*, *>, CPP_DATA : Any, DATA : PayloadResult, CPP_FILTER : Any, FILTER>(
    protected val filter: FILTER,
    protected val repository: REPO,
    protected val mapper: Function<CPP_DATA, DATA>
) : RequestInteractor<DATA, FILTER>,
    BaseInteractor() where FILTER : HashFilter, FILTER : Filter<CPP_FILTER> {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Inject
    internal open lateinit var networkUtils: NetworkUtils

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var hasPermissionError = false
        private set

    protected val workScheduler = SingleScheduler()
    private var emitter: Emitter<DATA>? = null
    private val isConnected: Boolean get() = networkUtils.isConnected

    override fun requestData(cause: UpdateCause): Observable<Result<DATA>> =
        Observable.create {
            val subscription = subscribe()
            emitter = it
            it.setCancellable {
                subscription.disable()
                if (it == emitter) {
                    emitter = null
                }
            }
            emitData(
                if (cause.isRequestFromCache) {
                    SyncStage.COMPLETE
                } else {
                    SyncStage.INIT
                }
            )
        }
            .map(Result.Companion::success)
            .onErrorReturn(Result.Companion::failure)
            .subscribeOn(workScheduler)
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Применить/передать специальные условия обработки для маппера в зависимости от
     * состояния [FILTER] используемого для получения данных.
     */
    protected open fun setMapperConditions(filter: FILTER) = Unit

    /**
     * Подтверждение ответа контроллера [cppResult], true если ответ валиден для использования. Необходим для определения в каких случаях
     * ответ невалиден и лучше его обработать как null, т.е. исключением [Error.NoDataReceivedError] для чтения после
     * синхронизации ИЛИ запуск синхронизации при чтение из кэша.
     */
    protected open fun matchUsableCondition(cppResult: CPP_DATA?): Boolean = cppResult != null

    /**
     * Пост-обработчик данных до трансформации.
     *
     * @param onRefreshCallback  true если получение данных инициировано по callback о завершении синхронизации
     */
    protected open fun onFetchProcessor(
        filter: FILTER,
        cppResult: CPP_DATA?,
        onRefreshCallback: Boolean = false
    ) = Unit

    /**
     * Запросить данные у контроллера по фильтру фасада контроллера [cppFilter].
     *
     * @param sync true если необходимо выполнить синхронизацию, false достаточно чтения из кэша
     */
    protected abstract fun requestCppData(cppFilter: CPP_FILTER, sync: Boolean): CPP_DATA?

    /**
     * Излучить данные для фильтра [filter].
     *
     * @param syncStage этап получения данных
     */
    private fun emitData(syncStage: SyncStage, hash: String = "") {
        val cppFilter = filter.prepareCppFilter(!syncStage.isInit, hash)
        val cppResult = requestCppData(cppFilter, syncStage.isInit)
        if (syncStage.isComplete && cppResult == null) {
            emitError(if (isConnected) Error.NoDataReceivedError() else Error.NoInternetConnection())
            return
        }
        if (!syncStage.isInit && hasPermissionError) {
            emitError(Error.NoPermissionsError())
            return
        }
        onFetchProcessor(filter, cppResult, syncStage.isComplete)
        val result = cppResult?.let(mapper::apply)
        if (result != null) {
            result.fromRefreshedCache = syncStage.isComplete
            result.error = if (!isConnected) Error.NoInternetConnection() else null
            emitter?.onNext(result)
        } else {
            cppResult?.let {
                Timber.e("Не удалось разобрать данные контроллера $it")
            }
        }
        if (syncStage.isComplete) {
            emitter?.onComplete()
        }
    }

    private fun emitError(error: Throwable) {
        emitter?.onError(error)
    }

    private fun onRefreshCallbackEvent(params: HashMap<String, String>?) {
        val callback = RefreshCallback(params)
        // Поиск работает с блокирующим вызовом без синхронизатора, поэтому по нему не может быть коллбэка
        val isSearchInProgress =
            this is SearchInteractor<*, *> && filter is SearchListFilter<*, *> && filter.asSearchFilter
        if (!filter.equalCallback(callback) || isSearchInProgress) {
            return
        }
        if (callback.isComplete) {
            hasPermissionError = false
            if (callback.haveMore) {
                emitData(SyncStage.NEXT, callback.syncHash)
            } else {
                emitData(SyncStage.COMPLETE, callback.syncHash)
            }
        } else if (callback.isFail) {
            callback.error?.let { error ->
                hasPermissionError = error is Error.NoPermissionsError
                emitError(error)
            }
        }
    }

    private fun subscribe() = repository
        .setDataRefreshCallback(::onRefreshCallbackEvent)
        .apply(Subscription::enable)

    /**
     * Выполнить строительство фильтра для репозитория фасада контроллера.
     */
    protected fun FILTER.prepareCppFilter(
        onRefreshCallback: Boolean = false,
        callbackHash: String = ""
    ): CPP_FILTER =
        if (onRefreshCallback && callbackHash.isNotBlank()) {
            build(callbackHash)
        } else {
            build()
        }.also { setMapperConditions(this) }

    /** Этапы получения данных при асинхронном запросе их получения с БЛ. */
    private enum class SyncStage(
        val isInit: Boolean = false,
        val isComplete: Boolean = false
    ) {
        INIT(isInit = true),
        NEXT,
        COMPLETE(isComplete = true)
    }
}

