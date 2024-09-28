package ru.tensor.sbis.mvp.search.behavior

import io.reactivex.Observable
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Реализация поведения поиска по умолчанию на основе общего компонента [SearchInput]
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class DefaultSearchInputBehavior(
    private val searchInput: SearchInput
) : SearchInputBehavior {

    override fun searchQueryChangedObservable(): Observable<String> = searchInput.searchQueryChangedObservable()

    override fun cancelSearchObservable(): Observable<Any> = searchInput.cancelSearchObservable()

    override fun searchFocusChangeObservable(): Observable<Boolean> = searchInput.searchFocusShareChangeObservable()

    override fun searchFieldEditorActionsObservable(): Observable<Int> =
        searchInput.searchFieldShareEditorActionsObservable()

    override fun filterClickObservable(): Observable<Any> = searchInput.filterClickObservable()

    override fun setSearchText(searchText: String) = searchInput.setSearchText(searchText)

    override fun getSearchText(): String = searchInput.getSearchText()

    override fun showCursorInSearch() = searchInput.showCursorInSearch()

    override fun hideCursorFromSearch() = searchInput.hideCursorFromSearch()

    override fun showKeyboard() = searchInput.showKeyboard()

    override fun hideKeyboard() = searchInput.hideKeyboard()

    override fun setSelectedFilters(filters: List<String>) = searchInput.setSelectedFilters(filters)
}