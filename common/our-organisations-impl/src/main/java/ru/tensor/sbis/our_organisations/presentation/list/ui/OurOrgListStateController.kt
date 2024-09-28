package ru.tensor.sbis.our_organisations.presentation.list.ui

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.tensor.sbis.our_organisations.data.OurOrgFilter
import ru.tensor.sbis.our_organisations.feature.data.Organisation

/**
 *  Результат списочных методов.
 *
 *  @param result список эдементов
 *  @param haveMore есть больше
 *
 *  @author mv.ilin
 */
internal class ListResultWrapper<Organisation>(
    val result: MutableList<Organisation>,
    val haveMore: Boolean,
    val syncState: SyncState? = null,
) {

    /**
     *  Состояние синхронизации.
     *
     *  [taskId] идентификатор асинхронной задачи.
     *  [initialCompleted] первичная синхронизация выполнена.
     */
    class SyncState(
        val taskId: String? = "",
        val initialCompleted: Boolean? = null,
    )
}

/**
 *  Класс для управления состоянием данных.
 *
 *  @author mv.ilin
 */
internal class OurOrgListStateController(
    private val requestFactory: (suspend (filter: OurOrgFilter) -> ListResultWrapper<Organisation>),
    private val viewController: ViewController<Organisation>,
    private val scope: CoroutineScope,
    private val itemsPage: Int = 0
) {

    interface ViewController<Organisation> {
        fun showEmptyProgress()
        fun showEmptyError(error: Throwable? = null)
        fun showEmptyView()
        fun showData(data: List<Organisation>, updatedAll: Boolean, refreshState: Boolean)
        fun showErrorMessage(error: Throwable)
        fun showRefreshProgress(show: Boolean)
        fun showPageProgress(show: Boolean)
    }

    companion object {
        private const val FIRST_PAGE = 0
    }

    private var currentState: State<Organisation> = Empty()
    private var currentPage = FIRST_PAGE
    private val currentData = mutableListOf<Organisation>()
    private var haveMore = false
    private var currentJob: Job? = null

    fun refresh(filter: OurOrgFilter) {
        currentState.refresh(filter)
    }

    fun refreshAllPage(filter: OurOrgFilter) {
        currentState.refreshAllPage(filter)
    }

    fun loadNewPage(filter: OurOrgFilter) {
        currentState.loadNewPage(filter)
    }

    fun release() {
        currentState = Released()
        currentJob?.cancel()
    }

    private fun loadPage(page: Int, filter: OurOrgFilter) {
        load(
            filter.apply {
                offset = page.toLong() * itemsPage
                count = itemsPage
            }
        )
    }

    private fun loadAll(filter: OurOrgFilter) {
        load(
            filter.apply {
                offset = 0
                count = currentPage * itemsPage
            }
        )
    }

    private fun refreshAllWithNextPage(filter: OurOrgFilter) {
        load(
            filter.apply {
                offset = 0
                count = (if (currentData.size > 0) currentData.size else itemsPage) + itemsPage
            }
        )
    }

    private fun load(filter: OurOrgFilter) {
        currentJob?.cancel()
        currentJob = scope.launch {
            try {
                val result = requestFactory.invoke(filter)
                if (result.result.isEmpty() && result.haveMore) {
                    currentPage++
                    loadPage(currentPage, filter)
                } else {
                    currentState.newData(result)
                }
            } catch (exception: CancellationException) {
                // игнорируем отмену корутины, ничего не делаем,
                // так как либо мы вышли с экрана, либо уже поставили другую корутину
            } catch (exception: Exception) {
                currentState.fail(exception)
            }
        }
    }

    private interface State<Organisation> {
        fun refresh(filter: OurOrgFilter) {}
        fun refreshAllPage(filter: OurOrgFilter) {}
        fun loadNewPage(filter: OurOrgFilter) {}
        fun newData(data: ListResultWrapper<Organisation>) {}
        fun fail(error: Throwable) {}
    }

    private inner class Empty : State<Organisation> {

        override fun refresh(filter: OurOrgFilter) {
            currentState = EmptyProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            refresh(filter)
        }
    }

    private inner class EmptyError(error: Throwable) : State<Organisation> {

        init {
            viewController.showEmptyError(error)
        }

        override fun refresh(filter: OurOrgFilter) {
            currentState = EmptyProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            refresh(filter)
        }
    }

    private inner class EmptyData : State<Organisation> {

        init {
            currentData.clear()
            viewController.showEmptyView()
        }

        override fun refresh(filter: OurOrgFilter) {
            currentState = EmptyProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            refresh(filter)
        }
    }

    private inner class Data(
        data: ListResultWrapper<Organisation>? = null,
        updatedAll: Boolean = true,
        refreshState: Boolean = false
    ) : State<Organisation> {

        init {
            if (data != null) {
                if (updatedAll) currentData.clear()
                currentData.addAll(data.result)
                haveMore = data.haveMore
                viewController.showData(currentData, updatedAll, refreshState)
            }
        }

        override fun refresh(filter: OurOrgFilter) {
            currentState = RefreshProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            currentState = RefreshAllPageProgress(filter)
        }

        override fun loadNewPage(filter: OurOrgFilter) {
            if (!haveMore) return
            currentState = PageProgress(filter)
        }
    }

    private inner class EmptyProgress(filter: OurOrgFilter) : State<Organisation> {

        init {
            currentPage = FIRST_PAGE
            viewController.showEmptyProgress()
            loadPage(currentPage, filter)
        }

        override fun refresh(filter: OurOrgFilter) {
            loadPage(currentPage, filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            refresh(filter)
        }

        override fun newData(data: ListResultWrapper<Organisation>) {
            when {
                data.result.isNotEmpty() -> {
                    currentPage++
                    currentState = Data(data)
                }

                data.syncState != null && data.syncState.initialCompleted != false -> {
                    currentState = EmptyData()
                }
            }
        }

        override fun fail(error: Throwable) {
            currentState = EmptyError(error)
        }
    }

    private inner class RefreshAllPageProgress(filter: OurOrgFilter) : State<Organisation> {

        init {
            loadAll(filter)
        }

        override fun refresh(filter: OurOrgFilter) {
            currentState = RefreshProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            loadAll(filter)
        }

        override fun loadNewPage(filter: OurOrgFilter) {
            currentState = PageProgress(filter, true)
        }

        override fun newData(data: ListResultWrapper<Organisation>) {
            if (data.result.isNotEmpty()) {
                currentState = Data(data)
            } else if (data.syncState?.initialCompleted != false) {
                currentState = EmptyData()
            }
        }

        override fun fail(error: Throwable) {
            currentState = Data()
            viewController.showErrorMessage(error)
        }
    }

    private inner class RefreshProgress(filter: OurOrgFilter) : State<Organisation> {

        init {
            currentPage = FIRST_PAGE
            viewController.showRefreshProgress(true)
            loadPage(currentPage, filter)
        }

        override fun newData(data: ListResultWrapper<Organisation>) {
            viewController.showRefreshProgress(false)
            if (data.result.isNotEmpty()) {
                currentState = Data(data, refreshState = true)
                currentPage++
            } else if (data.syncState?.initialCompleted != false) {
                currentState = EmptyData()
            }
        }

        override fun refresh(filter: OurOrgFilter) {
            loadPage(currentPage, filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            refresh(filter)
        }

        override fun fail(error: Throwable) {
            currentState = Data()
            viewController.showRefreshProgress(false)
            viewController.showErrorMessage(error)
        }
    }

    private inner class PageProgress(filter: OurOrgFilter, private var updatedAll: Boolean = false) :
        State<Organisation> {

        init {
            viewController.showPageProgress(true)
            if (updatedAll) {
                refreshAllWithNextPage(filter)
            } else {
                loadPage(currentPage, filter)
            }
        }

        override fun newData(data: ListResultWrapper<Organisation>) {
            viewController.showPageProgress(false)
            currentState = Data(data, updatedAll)
            currentPage++
        }

        override fun refresh(filter: OurOrgFilter) {
            viewController.showPageProgress(false)
            currentState = RefreshProgress(filter)
        }

        override fun refreshAllPage(filter: OurOrgFilter) {
            updatedAll = true
            refreshAllWithNextPage(filter)
        }

        override fun fail(error: Throwable) {
            currentState = Data()
            viewController.showPageProgress(false)
            viewController.showErrorMessage(error)
        }
    }

    private inner class Released : State<Organisation>
}
