package ru.tensor.sbis.design_selection.ui.content.vm.search

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils

/**
 * Реализация вью-модели поисковой строки.
 *
 * @author vv.chekurda
 */
internal class SelectionSearchViewModelImpl :
    ViewModel(),
    SelectionSearchViewModel {

    private val querySubject = BehaviorSubject.createDefault(DEFAULT_SEARCH_QUERY)
    private val hideKeyboardEventSubject = PublishSubject.create<Unit>()

    override val searchQuery: String
        get() = querySubject.value!!

    override val searchTextObservable: Observable<String> = querySubject
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())

    override val searchQueryObservable: Observable<String> = querySubject
        .distinctUntilChanged()
        .skip(1)
        .observeOn(AndroidSchedulers.mainThread())

    override val hideKeyboardEventObservable: Observable<Unit> = hideKeyboardEventSubject
        .observeOn(AndroidSchedulers.mainThread())

    override var isEnabled: Boolean = true

    override var isFocused: Boolean = false

    override fun setSearchText(text: String) {
        if (isEnabled) {
            querySubject.onNext(text)
        }
    }

    override fun cancelSearch() {
        setSearchText(DEFAULT_SEARCH_QUERY)
        hideKeyboardEventSubject.onNext(Unit)
    }

    override fun clearSearch() {
        setSearchText(DEFAULT_SEARCH_QUERY)
    }

    override fun hideKeyboard() {
        hideKeyboardEventSubject.onNext(Unit)
    }
}

private const val DEFAULT_SEARCH_QUERY = StringUtils.EMPTY