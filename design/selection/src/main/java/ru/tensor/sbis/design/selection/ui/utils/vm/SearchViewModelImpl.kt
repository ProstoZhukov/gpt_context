package ru.tensor.sbis.design.selection.ui.utils.vm

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.SEARCH
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent

internal const val DEFAULT_SEARCH_QUERY = ""

/**
 * @author ma.kolpakov
 */
internal class SearchViewModelImpl(
    private val searchQueryMinLength: Int,
    private val useCaseValue: String
) : ViewModel(), SearchViewModel {

    private val querySubject = BehaviorSubject.createDefault(DEFAULT_SEARCH_QUERY)

    override val searchText: Observable<String> = querySubject
        .skipWhile(CharSequence::isEmpty)
        .distinctUntilChanged()

    override val searchQuery: Observable<String> = querySubject
        .map { if (it.length < searchQueryMinLength) DEFAULT_SEARCH_QUERY else it }
        .distinctUntilChanged()

    override val hideKeyboardEvent = PublishSubject.create<Unit>()

    override var isEnabled: Boolean = true

    override var isFocused: Boolean = false

    override fun setSearchText(text: String) {
        if (isEnabled) {
            SelectionStatisticUtil.sendStatistic(SelectionStatisticEvent(useCaseValue, SEARCH.value))
            querySubject.onNext(text)
        }
    }

    override fun cancelSearch() {
        setSearchText(DEFAULT_SEARCH_QUERY)
        hideKeyboardEvent.onNext(Unit)
    }

    override fun clearSearch() {
        setSearchText(DEFAULT_SEARCH_QUERY)
    }

    override fun finishEditingSearchQuery() {
        hideKeyboardEvent.onNext(Unit)
    }
}