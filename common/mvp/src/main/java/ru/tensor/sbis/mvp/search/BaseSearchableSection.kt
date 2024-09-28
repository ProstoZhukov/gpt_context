package ru.tensor.sbis.mvp.search

import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.view.input.searchinput.DEFAULT_SEARCH_QUERY
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationSection
import ru.tensor.sbis.mvp.search.behavior.SearchInputBehavior

/**
 * Аналог [BaseSearchableView].
 *
 * @author am.boldinov
 */
abstract class BaseSearchableSection<ITEM : ListItem, CONTROLLER : ListController, ADAPTER : BaseTwoWayPaginationAdapter<ITEM>, LIST : AbstractListView<*, *>>(
    controller: CONTROLLER,
    adapter: ADAPTER,
    isRequired: Boolean = false,
    protected val searchInputBehavior: () -> SearchInputBehavior,
    listView: () -> LIST
) : BaseTwoWayPaginationSection<ITEM, CONTROLLER, ADAPTER, LIST>(controller, adapter, isRequired, listView),
    SearchableView<ITEM> {

    private val behavior get() = searchInputBehavior.invoke()

    override fun showCursorInFiltersPanel() {
        behavior.showCursorInSearch()
    }

    override fun clearSearchQuery() {
        behavior.setSearchText(DEFAULT_SEARCH_QUERY)
    }

    override fun hideCursorFromSearch() {
        behavior.hideCursorFromSearch()
    }

    override fun hideKeyboard() {
        behavior.hideKeyboard()
    }

    override fun showKeyboard() {
        behavior.showKeyboard()
    }

    override fun hideInformationView() {
        if (isRequired) {
            if (behavior.getSearchText().isNotEmpty()) {
                listView().postHideInformationView()
            } else {
                listView().hideInformationView()
            }
        }
    }

    override fun enableFolders() {
        // nothing
    }

    override fun disableFolders() {
        // nothing
    }

    override fun enableFilters() {
        // nothing
    }

    override fun disableFilters() {
        // nothing
    }
}