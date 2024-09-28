package ru.tensor.sbis.mvp.presenter

import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.content.localToGlobalPosition
import ru.tensor.sbis.design.list_utils.AbstractListView

/**
 * Аналог [ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination].
 *
 * @author am.boldinov
 */
abstract class BaseTwoWayPaginationSection<ITEM : ListItem, CONTROLLER : ListController, ADAPTER : BaseTwoWayPaginationAdapter<ITEM>, LIST : AbstractListView<*, *>>(
    controller: CONTROLLER,
    adapter: ADAPTER,
    isRequired: Boolean = false,
    protected val listView: () -> LIST
) : ListSection<ITEM, CONTROLLER, ADAPTER>(controller, adapter, isRequired), BaseTwoWayPaginationView<ITEM>,
    TwoWayAdapterDispatcher<ITEM> by PresenterViewDelegates.adapterDispatcher(adapter) {

    override fun showLoading() {
        // nothing
    }

    override fun hideLoading() {
        if (isRequired) {
            listView().isRefreshing = false
        }
    }

    override fun updateListViewState() {
        if (isRequired) {
            listView().updateViewState()
        }
    }

    override fun hideInformationView() {
        if (isRequired) {
            listView().hideInformationView()
        }
    }

    override fun ignoreProgress(ignore: Boolean) {
        if (isRequired) {
            listView().ignoreProgress(ignore)
        }
    }

    override fun scrollToPosition(position: Int) {
        if (position >= 0 && position < getItemCount()) {
            val scrollPosition = localToGlobalPosition(position)
            listView().scrollToPosition(scrollPosition)
        }
    }

    override fun showOlderLoadingProgress(show: Boolean) {
        adapter.showOlderLoadingProgress(show)
    }

    override fun showNewerLoadingProgress(show: Boolean) {
        adapter.showNewerLoadingProgress(show)
    }

    override fun showControls() {
        // nothing
    }

    override fun hideControls() {
        // nothing
    }

    override fun resetUiState() {
        if (isRequired) {
            showControls()
            listView().scrollToPosition(0)
            hideInformationView()
        }
    }
}