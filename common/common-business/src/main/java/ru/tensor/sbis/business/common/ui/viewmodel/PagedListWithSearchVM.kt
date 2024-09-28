package ru.tensor.sbis.business.common.ui.viewmodel

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.annotation.VisibleForTesting.Companion.PROTECTED
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import ru.tensor.sbis.business.common.data.ViewModelProvider
import ru.tensor.sbis.business.common.domain.filter.SearchListFilter
import ru.tensor.sbis.business.common.domain.interactor.RequestInteractor
import ru.tensor.sbis.business.common.domain.interactor.SearchInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper
import ru.tensor.sbis.business.common.ui.utils.isFalse
import ru.tensor.sbis.business.common.ui.utils.isTrue
import ru.tensor.sbis.business.common.ui.utils.reset
import ru.tensor.sbis.business.common.ui.utils.toFalse
import ru.tensor.sbis.business.common.ui.utils.toTrue
import ru.tensor.sbis.business.common.ui.viewmodel.UpdateCause.FILTER_CHANGE
import ru.tensor.sbis.business.common.ui.utils.hideNavigationPanel
import ru.tensor.sbis.business.common.ui.utils.showNavigationPanel
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import javax.inject.Inject

/**
 * Базовая VM списка с пагинацией и возможностью Поиска
 *
 * @param filter фильтр для получения списочных данных с пагинацией и Поиском
 * @param interactor интерактор источника списочных данных с поиском
 *
 * @property FILTER тип фильтра данных
 * @property EXTRA тип метаданных реестра
 * @property DATA тип данных реестра
 *
 * @property searchVM вью-модель поиска с фильтром
 * @property deferredFirstSearchPageProcessor флаг отложенной обработки результатов первого разворота с поиском
 */
@Suppress("AddVarianceModifier")
abstract class PagedListWithSearchVM<
        FILTER : SearchListFilter<*, *>,
        EXTRA : ViewModelProvider?,
        DATA : ViewModelProvider>(
    private val filter: FILTER,
    private val interactor: RequestInteractor<out PayloadPagedListResult<EXTRA, DATA>, FILTER>,
    @get:VisibleForTesting(otherwise = PROTECTED)
    val searchVM: FilterSearchPanelVM
) : PagedListVM<FILTER, EXTRA, DATA>(
    filter = filter,
    interactor = interactor
) {
    //region workaround members injection
    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var rxBus: RxBus

    @Inject
    @VisibleForTesting(otherwise = PROTECTED)
    lateinit var scrollHelper: ScrollHelper
    //endregion

    override fun onInitialization() {
        //игнорируем toast для ошибки NoSearchDataError, когда какие-то данные уже показаны
        toastHelper.addExclusion(Error.NoSearchDataError::class.java)
        popupNotificationHelper.addExclusion(Error.NoSearchDataError::class.java)
        super.onInitialization()
    }

    /**
     * Обновление состояния. Если поисковой запрос пуст, просто обновляем.
     * Если не пуст - производим поиск с применением фильтра.
     * @see [PagedListVM.updateState]
     */
    override fun updateState(causedByFilterChange: Boolean, processFilterIfNoConnection: Boolean) {
        if (filter.asSearchFilter) {
            deferredFirstSearchPageProcessor = true
        }
        super.updateState(causedByFilterChange, processFilterIfNoConnection)
    }

    override fun fetchData(cause: UpdateCause) =
        if (filter.asSearchFilter && interactor is SearchInteractor) {
            // отписка от предыдущих запросов для постановки в очередь только текущего поиска
            if (cause == FILTER_CHANGE) {
                searchRequestDisposables.clear()
            }
            onStartUpdating(cause)
            isRefreshing.toTrue
            searchRequestDisposables += interactor.searchData(filter.searchQuery)
                .doOnTerminate { onTerminateUpdatingProcess(cause) }
                .subscribe(::processSearchResponse, ::safeThrow)
        } else {
            // отписка от предыдущих запросов поиска для постановки в очередь только текущего
            if (cause == FILTER_CHANGE) {
                searchRequestDisposables.clear()
            }
            super.fetchData(cause)
        }

    /**
     * Вызвать стандартный сценарий обработки события возникновения ошибки при получении списочных данных с Поиском
     *
     * @param error исключение возникшее при получении данных
     */
    override fun processDataError(error: Throwable) {
        if (prioritizeErrorOverData(error)) {
            resetSearchResult()
        }
        if (showNoResultsForFilter(error)) {
            return
        }
        super.processDataError(error)
    }

    /**
     * Приоритезируем обработку ошибок при Поиске первого разворота
     */
    override fun prioritizeErrorOverData(error: Throwable): Boolean =
        error is Error.NoSearchDataError && deferredFirstSearchPageProcessor

    /**
     * Скрыть клавиатуру на экране c поиском через [FilterSearchPanel]
     *
     * @param showNavigation должны ли при скрытии клавиатуры показывать ННП. По-умолчанию true
     */
    @VisibleForTesting(otherwise = PROTECTED)
    fun hideKeyboard(showNavigation: Boolean = true) {
        searchVM.hideKeyboardForPanel()
        if (showNavigation) {
            scrollHelper.showNavigationPanel()
        }
    }

    /**
     * Показывает клавиатуру на экране c поиском [FilterSearchPanel]
     * Скрывает ННП
     */
    protected fun showKeyboard() {
        searchVM.showKeyboardForPanel()
        scrollHelper.hideNavigationPanel()
    }

    /**
     * Анимировать отображение / скрытие строки поиска с фильтром по скроллу [FilterSearchPanel]
     */
    @VisibleForTesting(otherwise = PROTECTED)
    fun animateFilterSearchPanelOnScroll() = addDisposable {
        searchVM.isAnimated.set(true)
        pagingHelper.observeScroll()
            .subscribe(::processScrollEventOnFilterSearchPanel)
    }

    override fun onCleared() {
        searchRequestDisposables.dispose()
        if (searchVM.isInHost.not()) {
            searchVM.dispose()
        }
        super.onCleared()
    }

    /**
     * Отображение заглушки об отсутствии данных по фильтру, если необходимо
     */
    private fun showNoResultsForFilter(error: Throwable): Boolean {
        val noDataForFilter =
            error is Error.NoDataReceivedError && filter.hasCertainFilter && isListVmEmpty()
        if (noDataForFilter) {
            val informationVm = errorVmProvider.noDataForFilter.configure {
                showIcon.toTrue
                shouldPlaceholderTakeAllAvailableHeightInParent = true
            }
            errorVm.set(informationVm)
            hideInitProgress()
        }
        return noDataForFilter
    }

    @CallSuper
    protected open fun processSearchResponse(result: Result<PayloadPagedListResult<EXTRA, DATA>>) {
        if (result.isSuccess) {
            result.getOrNull()?.let(::processDataResult)
        } else if (result.isFailure) {
            result.exceptionOrNull()?.let(::processDataError)
        }
        deferredFirstSearchPageProcessor = false
        hideLoadMoreProgress()
    }

    /**
     * Обработать скролл на Строке поиска с фильтром
     */
    private fun processScrollEventOnFilterSearchPanel(event: PagingScrollHelper.ScrollConsumerEvent) {
        val thresholdTopOffset = searchVM.panelHeight
        if (event.scrollDown && searchVM.isVisible.isTrue) {
            if (event.computedVerticalOffset > thresholdTopOffset) {
                searchVM.isVisible.toFalse
            }
        } else if (event.scrollUp && searchVM.isVisible.isFalse) {
            searchVM.isVisible.set(true)
        }
        searchVM.isElevated.set(event.computedVerticalOffset > 0)
    }

    /**
     * Сбрасываем поисковые результаты
     */
    private fun resetSearchResult() {
        extraVm.reset()
        listVms.reset()
    }

    @VisibleForTesting(otherwise = PRIVATE)
    var deferredFirstSearchPageProcessor = false
        private set
    private val searchRequestDisposables = CompositeDisposable()
}
