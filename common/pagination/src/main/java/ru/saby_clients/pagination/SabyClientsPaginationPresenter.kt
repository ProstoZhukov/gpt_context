@file:Suppress("DEPRECATION")

package ru.saby_clients.pagination

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.delayRun
import ru.tensor.sbis.common.util.runOnUiThread
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.viper.R
import timber.log.Timber
import java.util.ArrayDeque
import java.util.Queue

/**
 * Абстрактная реализация презентера для односторонней пагинации
 */
abstract class SabyClientsPaginationPresenter<
    DM : Any,
    VIEW : SabyClientsPaginationContract.View<DM>,
    FILTER : ListFilter,
    QUERY_FILTER : Any,
    INTERACTOR : SabyClientsPaginationContract.Interactor<DM, QUERY_FILTER>>(
    protected val filter: FILTER,
    private val interactor: INTERACTOR,
    private val dataFromCallback: Boolean = false
) : SabyClientsPaginationContract.Presenter<VIEW> {

    companion object {
        private const val DEFAULT_PAGE_SIZE = 40

        private const val SHOW_LOADING_PROGRESS_DELAY = 1000L
    }

    /**
     * Идентификатор строкового ресурса с текстом сообщения для пустого списка
     */
    @StringRes
    open val emptyViewErrorId: Int = R.string.viper_no_items_placeholder

    /**
     * Размер загружаемой страницы при пагинации
     */
    open val pageSize: Int = DEFAULT_PAGE_SIZE

    /**
     * Ссылка на контракт с фрагментом
     */
    protected var view: VIEW? = null

    /**
     * Контент
     */
    protected var dataList: ArrayList<DM> = arrayListOf()

    /**@SelfDocumented*/
    protected val disposer = CompositeDisposable()

    /**@SelfDocumented*/
    protected var listDisposer = CompositeDisposable()
    protected var listSyncSubscription: Subscription? = null
    protected var anchorPosition: Int = 0

    private val showDelayedProgressDisposable = SerialDisposable()
    private var lastVisibleItemPosition: Int = 0
    private var waitingForRefreshCallback: Boolean = false
    private var isLoadingInProcess: Boolean = false
    private var hasMore: Boolean = false
    private var anchorModel: DM? = null

    private var isListRequested = false
    private val actionQueue: Queue<() -> Unit> = ArrayDeque()

    init {
        listDisposer += showDelayedProgressDisposable
    }

    @CallSuper
    override fun attachView(view: VIEW) {
        this.view = view
        listSyncSubscription?.enable()
        if (dataList.isNotEmpty()) view.updateDataList(dataList.copy())
        else if (isLoadingInProcess.not()) view.showStubView(emptyViewErrorId)
    }

    @CallSuper
    override fun detachView() {
        disposer.clear()
        view = null
        listSyncSubscription?.disable()
    }

    @CallSuper
    override fun onDestroy() {
        listSyncSubscription = null
        listDisposer.dispose()
    }

    /**
     * Инициализация загрузки
     */
    protected fun initLoading() {
        listSyncSubscription?.enable() ?: subscribeListCallback()
        forceReloadDataList()
    }

    /**
     * Принудительное обновление списка от начала
     */
    @CallSuper
    protected open fun forceReloadDataList(withProgressDelay: Boolean = false) {
        resetState(withProgressDelay)
        loadPage(fromCallback = false, nextPage = false)
    }

    protected fun loadPage(fromCallback: Boolean, nextPage: Boolean, itemsCount: Int = pageSize) {
        val currentAnchorModel = if (nextPage) dataList.last() else null
        if (fromCallback.not() || currentAnchorModel == null) {
            anchorModel = currentAnchorModel
            anchorPosition = if (nextPage) dataList.size else 0
        }
        listDisposer += interactor.loadList(configureFilter(anchorModel, itemsCount), fromCallback)
            .subscribe(
                { processLoadingPageResult(it, fromCallback, nextPage) },
                { processLoadingError(it, nextPage) })
    }

    /**
     * Получение загруженных данных для наследников класса
     */
    protected fun getContent(): ArrayList<DM> = dataList

    /**
     * Получение якоря
     */
    protected fun getAnchor(): DM? = anchorModel

    override fun onRefresh() = forceReloadDataList()

    override fun onScroll(dy: Int, lastVisibleItemPosition: Int, computeVerticalScrollOffset: Int) {
        onScroll(dy, computeVerticalScrollOffset)
        if (dy != 0 && isLoadingInProcess.not()) this.lastVisibleItemPosition = lastVisibleItemPosition.coerceAtLeast(0)

        if (couldLoadPage(dy)) {
            view?.showListLoadingProgress(true)
            isLoadingInProcess = true
            loadPage(fromCallback = false, nextPage = true)
        }
    }

    /**
     * Метод реакции на скролл для наследников для использования [ScrollHelper]
     */
    open fun onScroll(dy: Int, computeVerticalScrollOffset: Int) {
        //Nothing by default
    }

    private fun couldLoadPage(dy: Int): Boolean = hasMore && isLoadingInProcess.not() && dy > 0 && isNeedLoadNextPage()

    private fun isNeedLoadNextPage(): Boolean =
        if (waitingForRefreshCallback) false else checkLastVisiblePositionForLoadPage()

    private fun checkLastVisiblePositionForLoadPage(): Boolean = dataList.lastIndex <= lastVisibleItemPosition

    open fun subscribeListCallback() {
        val request =
            if (dataFromCallback) interactor.setListDataCallback(::onRefreshCallback)
            else interactor.setListCallback(::onRefreshCallback)
        request?.let { req ->
            listDisposer += req.subscribe { listSyncSubscription = it.apply { enable() } }
        }
    }

    /**
     * Обработка вызова колбэка
     */
    private fun onRefreshCallback(param: HashMap<String, String>) {
        listDisposer += runOnUiThread {
            val action = {
                if (refreshNextPageNeeded(param)) loadPage(true, lastVisibleItemPosition != 0 && hasMore)
                else if (refreshDataListNeeded(param)) loadPage(
                    true,
                    nextPage = false,
                    dataList.size.takeIf { it > pageSize } ?: pageSize)
            }
            if (isListRequested) action.invoke() else actionQueue.add(action)
        }
    }

    /**
     * Обработка вызова колбэка
     */
    private fun onRefreshCallback(result: PagedListResult<DM>) {
        listDisposer += runOnUiThread {
            val action = {
                val metadata = result.metaData
                val nextPage = if (refreshNextPageNeeded(metadata)) lastVisibleItemPosition != 0 && hasMore
                else if (refreshDataListNeeded(metadata)) false
                else null
                if (nextPage != null) processLoadingPageResult(result, true, nextPage)
            }
            if (isListRequested) action.invoke() else actionQueue.add(action)
        }
    }

    /**
     * Нужно ли обновить весь список из бд
     * Данный метод необходимо переопределить, если контроллер кидает события колбэка без вызова метода list с UI
     */
    open fun refreshDataListNeeded(param: HashMap<String, String>): Boolean = false

    /**
     * Нужно ли регировать на колбэк, по умолчанию да
     * Данный метод нужно переопределить, если есть особые условия обработки колбэка со стороны UI, ex. FilterHash
     */
    open fun refreshNextPageNeeded(param: HashMap<String, String>): Boolean = true

    @Suppress("UNCHECKED_CAST")
    open fun configureFilter(anchorModel: DM?, itemsCount: Int): QUERY_FILTER =
        (filter.queryBuilder() as ListFilter.Builder<DM, QUERY_FILTER>).run {
            anchorModel(anchorModel)
            itemsCount(itemsCount)
            build()
        }

    /**
     * Обработка результатов запроса [loadPage]
     */
    @CallSuper
    open fun processLoadingPageResult(
        pagedListResult: PagedListResult<DM>,
        fromCallback: Boolean,
        nextPage: Boolean
    ) {
        val resultList = pagedListResult.dataList.mapNotNull { it }.asArrayList()
        val listIsEmpty = resultList.isEmpty()

        hasMore = pagedListResult.hasMore()
        waitingForRefreshCallback = hasMore && listIsEmpty
        isLoadingInProcess = fromCallback.not()

        dataList = if (nextPage) {
            val updatedDataList = dataList.take(dataList.indexOf(anchorModel) + 1).asArrayList()
            updatedDataList.addAll(resultList)
            updatedDataList
        } else resultList

        showDelayedProgressDisposable.dispose()

        view?.apply {
            if (nextPage && fromCallback.not()) addContentToDataList(resultList.copy()) else updateDataList(dataList.copy())

            if (nextPage) showListLoadingProgress(waitingForRefreshCallback)
            else if (fromCallback && listIsEmpty) {
                showStubView(emptyViewErrorId)
                showMainLoadingProgress(false)
            } else showMainLoadingProgress(listIsEmpty)
        }

        if (fromCallback.not()) {
            isListRequested = true
            val mutableIterator = actionQueue.iterator()
            for (action in mutableIterator) {
                action()
                mutableIterator.remove()
            }
        }
    }

    private fun processLoadingError(throwable: Throwable, nextPage: Boolean) {
        view?.apply {
            if (nextPage) showListLoadingProgress(false) else showStubView(emptyViewErrorId)
        }
        Timber.e(throwable)
    }

    open fun resetState(withDelay: Boolean) {
        if (withDelay) {
            showDelayedProgressDisposable.set(delayRun(SHOW_LOADING_PROGRESS_DELAY) {
                view?.showMainLoadingProgress(true)
            })
        } else view?.showMainLoadingProgress(true)
        view?.showListLoadingProgress(false)
        dataList.clear()
        lastVisibleItemPosition = 0
        waitingForRefreshCallback = false
        isLoadingInProcess = true
        hasMore = false
        isListRequested = false
    }
}