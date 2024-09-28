package ru.tensor.sbis.business.common.ui.viewmodel

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PROTECTED
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import ru.tensor.sbis.base_components.adapter.vmadapter.LoadMoreVM
import ru.tensor.sbis.business.common.data.ViewModelProvider
import ru.tensor.sbis.business.common.domain.NetworkAssistant
import ru.tensor.sbis.business.common.domain.PopupNotificationHelper
import ru.tensor.sbis.business.common.domain.ToastHelper
import ru.tensor.sbis.business.common.domain.filter.ListFilter
import ru.tensor.sbis.business.common.domain.interactor.RequestInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.ui.base.ComparableObservable
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.base.Error.NoDataReceivedError
import ru.tensor.sbis.business.common.ui.base.Error.NoPermissionsError
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper
import ru.tensor.sbis.business.common.ui.base.state_vm.*
import ru.tensor.sbis.business.common.ui.utils.*
import ru.tensor.sbis.business.common.ui.viewmodel.UpdateCause.*
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.SingleLiveEvent
import timber.log.Timber
import javax.inject.Inject

/**
 * Базовая VM списка с пагинацией
 *
 * @param filter фильтр для получения списочных данных с пагинацией
 * @param interactor интерактор источника списочных данных [PayloadPagedListResult]
 *
 * @property FILTER тип фильтра
 * @property EXTRA тип метаданных реестра
 * @property DATA тип данных реестра
 *
 * @property pagingHelper хэлпер для подписки на события пагинации
 * @property resourceProvider провайдер ресурсов
 * @property networkAssistant ассистент работы с сетью
 * @property toastHelper шина для инициирования отображения всплывающих сообщений
 * @property popupNotificationHelper шина для инициирования отображения сообщений информеров
 * @property errorVmProvider используемое описание ошибок
 *
 * @property fetchDataOnInit true, если нужно запускать загрузку данных сразу, false - не загружать
 *
 * @property listVms [ObservableField] списка вью-моделей основного реестра
 * @property extraVm [ObservableField] вью-модели из данных полезной нагрузки (обычно используется как заголовок)
 * @property errorVm [ObservableField] вью-модели ошибки
 * @property progressInitVm [ObservableField] состояние вью-модели прогресса инициализации [PagedListVM]
 * @property isRefreshing [ObservableField] состояние индикатора прогресса [PagedListVM] после свайпа пользователя
 *
 * @property metadata последние полученные данные полезной нагрузки (метаданные)
 * @property vmReceiverId идентификатор вью-модели
 * @property isInProgress состояние запроса к источнику данных
 * @property deferredResultReset флаг отложенного сброса предшествующего результата
 * @property shouldNotShowPagingProgress Переменная-флаг, по которой определяется, что обновление было
 * после pull to refresh и прогресс постраничной загрузки не нужно отображать
 * @property isInPageLoading канал уведомления о том что вью-модель в состоянии получения новой страницы (пагинации).
 * Примечание: При отсутствии подписчиков на [isInPageLoading] прогресс пагинации [LoadMoreVM] будет добавляться в [listVms]
 * @property isShowToast переменная-флаг для отображение тостов или информеров при обработке ошибок
 * используется для подписки на отображение/скрытие прогресса пагинации на экране.
 */
@Suppress("AddVarianceModifier")
abstract class PagedListVM<
        FILTER : ListFilter<*, *>,
        EXTRA : ViewModelProvider?,
        DATA : ViewModelProvider>(
    private val filter: FILTER,
    private val interactor: RequestInteractor<out PayloadPagedListResult<EXTRA, DATA>, FILTER>,
) : BaseViewModel() {

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var networkAssistant: NetworkAssistant

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var resourceProvider: ResourceProvider

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var pagingHelper: PagingScrollHelper

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var toastHelper: ToastHelper

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var popupNotificationHelper: PopupNotificationHelper

    protected open val fetchDataOnInit: Boolean = true

    val extraVm = ObservableField<BaseObservable>()
    val listVms = ObservableField<List<BaseObservable>>()
    val errorVm = ObservableField<BaseObservable>()
    var isRefreshing = ObservableBoolean(false)
    val progressInitVm = ObservableField<LoadMoreVM>()
    var isInPageLoading = SingleLiveEvent<Boolean>()
    val vmReceiverId = javaClass.canonicalName!!

    open val isShowToast = false

    /**
     * Вызвать стандартный сценарий обновления состояние вьюмодели
     *
     * @param causedByFilterChange true если состояние обновляется по причине изменений в фильтре данных
     * @param processFilterIfNoConnection false если [causedByFilterChange] игнорируется при отсутствии сети
     * Состояние [processFilterIfNoConnection] применимо в зависимости от специфики кэшировния контроллером
     */
    @MainThread
    @CallSuper
    open fun updateState(
        causedByFilterChange: Boolean = false,
        processFilterIfNoConnection: Boolean = true,
    ) {
        if (initialized.not()) {
            return
        }
        pagingHelper.relieve(vmReceiverId)
        filter.reset()
        if (causedByFilterChange) {
            if (processFilterIfNoConnection || networkAssistant.isConnected) {
                deferredResultReset = true
                fetchData(FILTER_CHANGE)
            } else {
                processDataError(Error.NoInternetConnection())
            }
        } else fetchData(PULL_TO_REFRESH)
    }

    /** Изменить состояние обработки пагинации */
    fun changePagingState(isPaging: Boolean) = if (isPaging) {
        pagingHelper.relieve(vmReceiverId)
    } else {
        pagingHelper.block(vmReceiverId)
    }

    /**
     * [BaseViewModel.initialize]
     */
    override fun onInitialization() {
        toastHelper.addExclusion(NoDataReceivedError::class.java)
        popupNotificationHelper.addExclusion(NoDataReceivedError::class.java)
        subscribeToNetworkEvents()
        subscribePagingEvents()

        if (fetchDataOnInit) {
            fetchDataFirst()
        }
    }

    override fun onCleared() {
        requestDisposables.dispose()
        super.onCleared()
    }

    //region set subscriptions
    /**
     * Подписка на событие доступности соединения
     */
    private fun subscribeToNetworkEvents() = addDisposable {
        networkAssistant.addOnConnectAction(
            action = { updateState() },
            actionFilter = ::beUpdatedOnConnected
        ).subscribe()
    }

    /**
     * Подписка на события пагинации
     * Включает базовую обработку события пагинации для получения следующего разворота.
     * Метод реализует базовый сценарий пагинации и не должен переопределяться без явной причины
     */
    private fun subscribePagingEvents() = addDisposable {
        pagingHelper.observePaging(vmReceiverId)
            .filter { needToProcessPaging(filter) }
            .subscribe { fetchData(SCROLL_TO_REFRESH) }
    }

    /**
     * Необходимо ли обрабатывать событие пагинации для текущего состояния вью-модели
     */
    open fun needToProcessPaging(filter: FILTER): Boolean = true
    //endregion set subscriptions

    //region receive Data

    /**
     * Изначальный запрос данных
     */
    protected open fun fetchDataFirst() {
        val initCause = if (networkAssistant.isConnected) {
            INITIAL_REFRESH
        } else {
            REQUEST_FROM_CACHE
        }
        fetchData(initCause)
    }

    /**
     * Запросить получение/обновление данных
     *
     * @param cause причина запроса данных
     */
    @MainThread
    protected open fun fetchData(cause: UpdateCause) {
        onStartUpdating(cause)
        requestDisposables += interactor.requestData(cause)
            .doOnEach { event ->
                if (event.isOnError || event.isOnNext) {
                    resetDeferredState()
                    onTerminateUpdatingProcess(cause)
                }
            }
            .subscribe(::processSourceResponse, ::processDataError)
    }

    private fun processSourceResponse(result: Result<PayloadPagedListResult<EXTRA, DATA>>) {
        if (result.isSuccess) {
            result.getOrNull()?.let(::processDataResult)
        } else if (result.isFailure) {
            result.exceptionOrNull()?.let(::processDataError)
        }
    }
    //endregion receive Data

    //region process Data
    /**
     * Вызвать стандартный сценарий обработки события/результата успешного получения списочных данных
     *
     * @param pagedResult результат списочных данных
     */
    protected open fun processDataResult(pagedResult: PayloadPagedListResult<EXTRA, DATA>) {
        /* скрываем прогресс пагинации только после синхронизации (чтобы не скрыть прогресс пока дынные не добавлены)
        или если результат из кэша не пуст (чтобы прогресс не оказался между записями списочных данных) */
        if (pagedResult.fromRefreshedCache || pagedResult.isNotEmpty) {
            hideLoadMoreProgress()
        }
        // при отсутствии прав доступа скрываем ранее полученные данные и прерываем обработку
        if (pagedResult.isPermissionError && shouldProcessNoPermission()) {
            extraVm.reset()
            listVms.reset()
            return
        }
        processMetadata(pagedResult)
        processListData(pagedResult)
        processHasMore(pagedResult)
        if (pagedResult.isNotEmpty) {
            processNoEmptyResult()
            // Обработать смежную неблокирующую ошибку при получении данных отобразив тост
            pagedResult.error?.let {
                postPopupNotification(it)
            }
        } else {
            processEmptyResult(pagedResult)
        }
    }

    /**
     * Вызвать стандартный сценарий обработки метаданных успешного получения результата итогов и тп.
     *
     * @param pagedResult результат списочных данных
     */
    private fun processMetadata(pagedResult: PayloadPagedListResult<EXTRA, DATA>) {
        if (metadata == null || metadata != pagedResult.extra || pagedResult.fromRefreshedCache) {
            val isChanged = metadata != null && metadata != pagedResult.extra
            if (pagedResult.extra != null) {
                metadata = pagedResult.extra
            }
            transformExtraDataToViewModel(
                extraData = metadata,
                changed = isChanged,
                fromRefreshCache = pagedResult.fromRefreshedCache
            )
                ?.let { extraVm.set(it) }
        }
    }

    /**
     * Обработать списочные данные полученного результата
     * @param pagedResult результат списочных данных
     */
    private fun processListData(pagedResult: PayloadPagedListResult<EXTRA, DATA>) =
        if (filter.isFirstPageOnlySynced) {
            processFirstDataListResults(pagedResult)
        } else {
            processNextDataListResults(pagedResult)
        }

    private fun processFirstDataListResults(firstPagedResult: PayloadPagedListResult<EXTRA, DATA>) =
        firstPagedResult.run {
            if (dataList.size == 1 && processAloneItemListData(dataList.single())) {
                return
            }
            var newListVms = transformListDataToViewModelList(dataList).toMutableList()
            val listHeader = getListHeader(extra, newListVms)
            listHeader?.let { header -> newListVms.add(0, header) }
            val oldComparableListVms = listVms.toList.filterIsInstance<ComparableObservable>()
            if (newListVms.isNotEmpty() && oldComparableListVms.isNotEmpty()) {
                newListVms = newListVms.update { new ->
                    val old = oldComparableListVms.find { it.isTheSame(new) }
                    old?.let(new::oneOf)
                }
            }
            listVms.set(postProcessViewModelList(newListVms))
        }

    private fun processNextDataListResults(nextPagedResult: PayloadPagedListResult<EXTRA, DATA>) {
        val newListVms = transformListDataToViewModelList(nextPagedResult.dataList).toMutableList()
        val newComparableListVms = newListVms.filterIsInstance<ComparableObservable>()
        var finalList = listVms.toList
        if (finalList.isNotEmpty() && newComparableListVms.isNotEmpty()) {
            finalList = finalList.update { old ->
                val new = newComparableListVms.find { it.isTheSame(old) }
                new?.oneOf(old)?.also { newListVms.remove(new) }
            }
        }
        if (newListVms.isNotEmpty()) {
            finalList = finalList.union(newListVms).toList()
        }
        listVms.set(postProcessViewModelList(finalList))
    }

    /**
     * Обработать получение последующих разворотов данных если необходимо:
     * - двинуть разворот если есть данные из кэша
     * - запретить пагинацию если нет данных из кэша после синхронизации
     * @param pagedResult результат списочных данных
     */
    private fun processHasMore(pagedResult: PayloadPagedListResult<EXTRA, DATA>) = pagedResult.run {
        if (hasNoMoreAfterRefresh || isEmptyAfterRefresh) {
            pagingHelper.block(vmReceiverId)
        } else if (isNotEmptyAfterRefresh || isNotEmpty) {
            turnPage()
        }
    }

    /**
     * Пост-обработка списочных данных вью-модели.
     * Вызывается для всего реестра вью-моделей после трансформации нового разворота
     * @return по-умолчанию список вью-моделей возвращается без изменений
     */
    protected open fun postProcessViewModelList(vmList: List<BaseObservable>): List<BaseObservable> =
        vmList

    /**
     * Делегирование обработки списка с единственной записью
     * @return true если дальнейшая обработка данных не понадобится, иначе false
     */
    protected open fun processAloneItemListData(aloneItem: DATA): Boolean = false

    /** Обработать НЕ пустой результат */
    protected open fun processNoEmptyResult() {
        hideInitProgress()
        hideError()
    }

    /**
     * Обработать пустой результат списочных данных
     *
     * @param pagedResult результат списочных данных
     */
    protected open fun processEmptyResult(pagedResult: PayloadPagedListResult<EXTRA, DATA>) {
        val isDisconnected = networkAssistant.isDisconnected
        val doNotWait = pagedResult.fromRefreshedCache || isDisconnected
        if (doNotWait && isListVmEmpty()) {
            hideInitProgress()
            val resultError = pagedResult.error
            when {
                resultError != null     -> showError(errorVmProvider.from(resultError))
                isDisconnected          -> showError(errorVmProvider.networkError)
                filter.hasCertainFilter -> showError(errorVmProvider.noDataForFilter)
                else                    -> showError(errorVmProvider.noDataFound)
            }
        }
    }

    private fun turnPage() {
        if (filter.isPageType) {
            filter.incPage()
        }
        pagingHelper.relieve(vmReceiverId)
    }
    //endregion process Data

    //region process Error
    protected abstract val errorVmProvider: DisplayedErrors

    /**
     * Вызвать стандартный сценарий обработки события возникновения ошибки при получении списочных данных
     *
     * @param error исключение возникшее при получении данных
     */
    protected open fun processDataError(error: Throwable) {
        // при отсутствии прав доступа скрываем ранее полученные данные
        val isPermissionError = error is NoPermissionsError
        if (isPermissionError && shouldProcessNoPermission()) {
            extraVm.reset()
            listVms.reset()
        }

        // не скрываем прогресс пагинации только если данные еще не получены
        if (error !is Error.NotLoadYetError) {
            hideLoadMoreProgress()
        }
        postTimber(error)
        val stubVm = errorVmProvider.from(error)
        val isErrorPriority = processErrorPriority(error)
        if (isErrorPriority) {
            hideInitProgress()
            showError(stubVm)
        } else {
            if (isShowToast) {
                postToast(error)
            } else {
                postPopupNotification(error)
            }
        }
    }

    /**
     * Получить состояние приоритета ошибки над данными
     *
     * @param error исключение возникшее при получении данных
     * @return true если приоритет ошибки
     */
    private fun processErrorPriority(error: Throwable): Boolean {
        val noData = isListVmEmpty()
        val noPermissionError = shouldProcessNoPermission() && error is NoPermissionsError
        val noInternetConnection = error is Error.NoInternetConnection
        val hasNotDataWhenNoConnection = noInternetConnection && noData
        val errorPriority = prioritizeErrorOverData(error) || noData
        return errorPriority || hasNotDataWhenNoConnection || noPermissionError
    }

    /**
     * Отобразить заглушку ошибки/доп. информации
     *
     * @param infoStubVm вьюмодель заглушки
     */
    private fun showError(infoStubVm: InformationVM) {
        infoStubVm.configure {
            showIcon.toTrue
            shouldPlaceholderTakeAllAvailableHeightInParent = true
        }
        errorVm.set(infoStubVm)
    }

    /**
     * Скрыть заглушку ошибки/доп. информации
     */
    protected fun hideError() = errorVm.reset()
    //endregion process Error

    //region transform Data
    /**
     * Трансформирует данные полезной нагрузки (метаданные) во вью-модель
     *
     * @param changed true если метаданные изменились (не путать с присвоением вместо null)
     * @param fromRefreshCache true если метаданные получены из кэша ПОСЛЕ первичной синхронизации
     * @return вью-моделей на основе метаданных
     */
    protected open fun transformExtraDataToViewModel(
        extraData: EXTRA?,
        changed: Boolean,
        fromRefreshCache: Boolean,
    ): BaseObservable? = null

    /**
     * Трансформирует списочные данные во вью-модели
     * @return список вью-моделей
     */
    protected abstract fun transformListDataToViewModelList(listData: List<DATA>): List<BaseObservable>

    /**
     * Добавляет заголовок к реестру
     * Рекомендуется использовать исключительно в случае,
     * когда требуется построить заголовок в зависимости содержания списка
     * @param meta метаданные реестра
     * @param content списочные данные реестра
     *
     * @return VM заголовка
     */
    protected open fun getListHeader(
        meta: EXTRA?,
        content: List<BaseObservable>,
    ): BaseObservable? = null
    //endregion transform Data

    //region process States and Progress
    /**
     * Предобработка получения/обновления данных
     *
     * @param cause причина обновления данных
     */
    @MainThread
    protected fun onStartUpdating(cause: UpdateCause) {
        isInProgress = true
        when (cause) {
            PULL_TO_REFRESH -> {
                isRefreshing.toTrue
                shouldNotShowPagingProgress = true
            }
            FILTER_CHANGE   -> {
                showInitProgressIfNeeded(true)
                hideError()
                hideLoadMoreProgress()
            }
            else            -> {
                showInitProgressIfNeeded()
                showLoadMoreProgressIfNeeded()
            }
        }
    }

    /**
     * Обработать событие завершения запроса к источнику данных
     * - скрытие прогресса инициализации
     * - скрытие индикатора обновления свайпом
     */
    protected open fun onTerminateUpdatingProcess(cause: UpdateCause) {
        val clearPullToRefresh = cause.isPullToRefresh && isListVmEmpty()

        /** если причина [INITIAL_REFRESH], [FILTER_CHANGE], "чистый" [PULL_TO_REFRESH] */
        val alikeInitCause = cause.isInitialRefresh || clearPullToRefresh || cause.isFilterChange
        if (alikeInitCause.not()) {
            hideInitProgress()
        }
        isInProgress = false
        isRefreshing.toFalse
    }

    /**
     * Удерживать отображение прогресса до завершения синхронизации с облаком
     * Примечание: предотвращает поведение когда [INITIAL_REFRESH] отработал с пустым результатом и прогресс был
     * скрыт еще до момента завершения синхронизации и получения коллбэка [REFRESH_CALLBACK]
     *
     * @return true если удерживаем индикатор прогресса, иначе false
     */
    protected open fun keepProgressForRefreshCallback(): Boolean = false

    /**
     * Изменить приоритет обработки возникающих ошибок над данными
     * Если true то при возникновении ошибки будет обновлено состояние [errorVm] независимо от [listVms],
     * иначе состояние [errorVm] обновится только если данные отсутсвуют, т.е. [listVms] - пуст, а ошибка будет обработана
     * через [toastHelper]
     * Примечание: не рекомендуется для целей приоритизации переопределять [processDataError]
     *
     * @param error исключение возникшее при получении данных
     * @return true приоритет ошибки, false приоритет данных
     */
    protected open fun prioritizeErrorOverData(error: Throwable): Boolean = false

    /**
     * Проверяет подлежит ли обновлению текущее состояние вью-модели при появлении подключения к сети
     *
     * @return true если подлежит обновлению
     */
    protected open fun beUpdatedOnConnected() =
        isInProgress.not() && errorVm.get()?.let {
            it is InformationVM && it.isType(UiErrorType.NETWORK_ERROR)
        } == true

    /**
     * Отобразить прогресс инициализации если это к месту
     */
    private fun showInitProgressIfNeeded(force: Boolean = false) {
        if (force) {
            progressInitVm.set(LoadMoreVM(delayedShowing = true))
        } else if (extraVm.isNull() && listVms.isNull() && errorVm.isNull()) {
            progressInitVm.set(LoadMoreVM(delayedShowing = true))
        }
    }

    /**
     * Отобразить прогресс пагинации на списке если это необходимо
     */
    protected fun showLoadMoreProgressIfNeeded() {
        if (isListVmEmpty() || shouldNotShowPagingProgress || filter.isFirstPageToSync) {
            shouldNotShowPagingProgress = false
            return
        }
        if (isInPageLoading.hasActiveObservers()) {
            isInPageLoading.postValue(true)
        } else {
            if (listVms.toList.lastOrNull() !is LoadMoreVM) {
                val progressedList = listVms.get()
                    .orEmpty()
                    .toMutableList()
                progressedList.add(pagingVM)
                listVms.set(progressedList)
            }
        }
    }

    /**
     * Скрыть прогресс инициализации
     */
    protected fun hideInitProgress() = progressInitVm.reset()

    /**
     * Скрыть прогресс пагинации на списке.
     * В случае когда нет активных подписчиков на [isInPageLoading] пробуем удалить все [LoadMoreVM] из списка
     */
    protected fun hideLoadMoreProgress() {
        if (isInPageLoading.hasActiveObservers()) {
            isInPageLoading.postValue(false)
        } else {
            listVms.removeAll(LoadMoreVM::class.java)
        }
    }

    /**
     * Проверить заполненность списка вью-моделей основного реестра [listVms]
     *
     * @param ignoreLoadMoreProgress true если игнорируем вью-модель прогресса
     * @return true если список вью-моделей основного реестра пуст
     */
    protected open fun isListVmEmpty(ignoreLoadMoreProgress: Boolean = true): Boolean =
        if (ignoreLoadMoreProgress) {
            listVms.isEmptyOrHasOnly { it is LoadMoreVM }
        } else {
            listVms.isNullOrEmpty()
        }

    /**
     * Обработка утраты прав доступа при получении ошибки доступа из колбека [RefreshCallback].
     * Базовая обработка: скрываются ранее полученные данные до момента исчезновения ошибки доступа
     *
     * @return true если необходимо использовать базовую обработку ошибки прав доступа
     */
    protected open fun shouldProcessNoPermission(): Boolean = false

    /** Выполнить отложенный сброс предшествующего состояния вью-модели */
    private fun resetDeferredState() {
        if (deferredResultReset.not()) {
            return
        }
        deferredResultReset = false
        metadata = null
        extraVm.reset()
        listVms.reset()
        hideError()
    }
    //endregion process States and Progress

    //region process Toast
    /** Публикация события через отображение всплывающих сообщений */
    private fun postToast(error: Throwable) {
        val message = errorVmProvider.from(error)
            .toString { resourceProvider.getString(it) }
        toastHelper.post(message, error)
    }

    /** Публикация события в логи */
    private fun postTimber(error: Throwable) = when (error) {
        is NoDataReceivedError ->
            Timber.w("Попытка получения записей несуществующего разворота")
        is NoPermissionsError  -> Timber.w("Облачный метод запретил доступ")
        else                   -> Timber.w(error)
    }
    //endregion process Toast

    private fun postPopupNotification(error: Throwable) {
        val errorType = errorVmProvider.from(error, true)
        val message = errorType.toString { resourceProvider.getString(it) }
        val icon = if (errorType.popupIconResId == ID_NULL) {
            null
        } else {
            resourceProvider.getString(errorType.popupIconResId)
        }
        popupNotificationHelper.post(message = message, error = error, icon = icon)
    }

    protected var metadata: EXTRA? = null
        private set
    protected val requestDisposables = CompositeDisposable()
    private val pagingVM = LoadMoreVM()
    private var isInProgress = false
    private var shouldNotShowPagingProgress = false

    @Volatile
    private var deferredResultReset = false
}