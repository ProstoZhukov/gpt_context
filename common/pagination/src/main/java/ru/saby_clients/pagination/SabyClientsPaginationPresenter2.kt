@file:Suppress("DEPRECATION")

package ru.saby_clients.pagination

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.delayRun
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.viper.R
import timber.log.Timber

/**
 * Абстрактная реализация презентера для односторонней пагинации
 */
abstract class SabyClientsPaginationPresenter2<
    DM : Any,
    VIEW : SabyClientsPaginationContract.View<DM>,
    FILTER : ListFilter,
    QUERY_FILTER : Any,
    INTERACTOR : SabyClientsPaginationContract.Interactor2<DM, QUERY_FILTER>>(
    protected val filter: FILTER,
    private val interactor: INTERACTOR
) : SabyClientsPaginationContract.Presenter<VIEW> {

    private companion object {
        const val DEFAULT_PAGE_SIZE = 40
        const val SHOW_LOADING_PROGRESS_DELAY = 1000L
    }

    /**
     * Идентификатор строкового ресурса с текстом сообщения для пустого списка
     */
    @StringRes
    open val emptyViewErrorId: Int = R.string.viper_no_items_placeholder

    /**
     * Размер загружаемой страницы при пагинации
     */
    protected var pageSize: Int = DEFAULT_PAGE_SIZE

    /**
     * Ссылка на контракт с фрагментом
     */
    protected var view: VIEW? = null

    /**@SelfDocumented*/
    protected val disposer = CompositeDisposable()

    private val listDisposer = SerialDisposable()
    private val listPageDisposer = SerialDisposable()
    private val showDelayedProgressDisposable = SerialDisposable()

    private var dataList: ArrayList<DM> = arrayListOf()
    private var isLoadingInProcess: Boolean = false
    private var hasMore: Boolean = false
    private var anchorModel: DM? = null

    init {
        disposer += listDisposer
        disposer += listPageDisposer
        disposer += showDelayedProgressDisposable
    }

    @CallSuper
    override fun attachView(view: VIEW) {
        this.view = view
        if (dataList.isNotEmpty()) view.updateDataList(dataList.copy())
    }

    @CallSuper
    override fun detachView() {
        disposer.clear()
        view = null
    }

    @CallSuper
    override fun onDestroy() {
        disposer.dispose()
    }

    override fun onRefresh() = forceReloadDataList()

    override fun onScroll(dy: Int, lastVisibleItemPosition: Int, computeVerticalScrollOffset: Int) {
        if (shouldLoadPage(dy, lastVisibleItemPosition.coerceAtLeast(0))) {
            view?.showListLoadingProgress(true)
            isLoadingInProcess = true
            loadPage()
        }
    }

    /**
     * Получение загруженных данных для наследников класса
     */
    protected fun getContent(): List<DM> = dataList

    /**
     * Инициализация загрузки
     */
    protected fun initLoading() {
        forceReloadDataList()
    }

    /**
     * Принудительное обновление списка от начала
     */
    @CallSuper
    protected open fun forceReloadDataList(withProgressDelay: Boolean = false) {
        resetState(withProgressDelay)
        loadList()
    }

    /**
     * Обработка результатов запроса [loadList]
     */
    @CallSuper
    protected open fun processLoadingListResult(pagedListResult: PagedListResult<DM>) {
        showDelayedProgressDisposable.dispose()
        hasMore = pagedListResult.hasMore()
        isLoadingInProcess = pagedListResult.isFullyCached.not()

        dataList = prepareList(pagedListResult.dataList.asArrayList())
        view?.apply {
            updateDataList(dataList.copy())
            if (dataList.isEmpty()) showStubView(emptyViewErrorId)
            showMainLoadingProgress(isLoadingInProcess && dataList.isEmpty())
        }
    }

    /**
     * Возможность изменить список после загрузки
     */
    protected open fun prepareList(updatedDataList: ArrayList<DM>): ArrayList<DM> = updatedDataList

    /**
     * Обработка результатов запроса [loadPage]
     */
    @CallSuper
    protected open fun processLoadingPageResult(pagedListResult: PagedListResult<DM>) {
        hasMore = pagedListResult.hasMore()
        isLoadingInProcess = pagedListResult.isFullyCached.not()

        val updatedDataList = dataList.take(dataList.indexOf(anchorModel) + 1).asArrayList()
        updatedDataList.addAll(pagedListResult.dataList)
        dataList = updatedDataList

        view?.apply {
            updateDataList(dataList.copy())
            showListLoadingProgress(isLoadingInProcess && dataList.isEmpty())
        }
    }

    /**
     * Обработка ошибок
     */
    protected open fun processLoadingError(throwable: Throwable, nextPage: Boolean) {
        Timber.e(throwable)
        view?.apply {
            if (nextPage) showListLoadingProgress(false) else showStubView(emptyViewErrorId)
        }
    }

    private fun loadList() {
        listPageDisposer.dispose()
        listDisposer.set(
            interactor.loadList(configureFilter(null, pageSize))
                .doOnNext { processLoadingListResult(it) }
                .doOnError { processLoadingError(it, false) }
                .subscribe()
        )
    }

    private fun loadPage() {
        anchorModel = dataList.last()
        listDisposer.set(interactor.loadPage(configureFilter(anchorModel, pageSize))
            .doOnNext { processLoadingPageResult(it) }
            .doOnError { processLoadingError(it, true) }
            .subscribe()
        )
    }

    private fun shouldLoadPage(dy: Int, lastVisibleItemPosition: Int): Boolean =
        hasMore && isLoadingInProcess.not() && dy > 0 && checkLastVisiblePositionForLoadPage(lastVisibleItemPosition)

    private fun checkLastVisiblePositionForLoadPage(lastVisibleItemPosition: Int): Boolean =
        dataList.lastIndex <= lastVisibleItemPosition

    @Suppress("UNCHECKED_CAST")
    private fun configureFilter(anchorModel: DM?, itemsCount: Int): QUERY_FILTER =
        (filter.queryBuilder() as ListFilter.Builder<DM, QUERY_FILTER>).run {
            anchorModel(anchorModel)
            itemsCount(itemsCount)
            build()
        }

    private fun resetState(withDelay: Boolean) {
        if (withDelay) {
            showDelayedProgressDisposable.set(delayRun(SHOW_LOADING_PROGRESS_DELAY) {
                view?.showMainLoadingProgress(true)
            })
        } else view?.showMainLoadingProgress(true)
        dataList.clear()
        isLoadingInProcess = true
        hasMore = false
    }
}