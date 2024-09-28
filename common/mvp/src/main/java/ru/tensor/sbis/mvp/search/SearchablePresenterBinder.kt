package ru.tensor.sbis.mvp.search

import android.view.inputmethod.EditorInfo
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.mvp.search.behavior.SearchInputBehavior

/**
 * Набор утилит для привязки View и [SearchablePresenter].
 *
 * Legacy-код. Желательно не использовать, только в исключительных случаях. Переходим на MVI.
 *
 * @author am.boldinov
 */
object SearchablePresenterBinder {

    /**
     * Привязывает презентер к событиям строки поиска.
     */
    @JvmStatic
    fun <PRESENTER : SearchablePresenter<*>> bindToSearchInputBehavior(
        presenter: PRESENTER,
        behavior: SearchInputBehavior
    ) = CompositeDisposable().apply {
        with(behavior) {
            addAll(
                searchQueryChangedObservable()
                    .subscribe(
                        { presenter.onSearchQueryChanged(it) },
                        FallbackErrorConsumer.DEFAULT
                    ),
                cancelSearchObservable()
                    .subscribe(
                        { presenter.onSearchClearButtonClicked() },
                        FallbackErrorConsumer.DEFAULT
                    ),
                searchFocusChangeObservable()
                    .subscribe(
                        { hasFocus ->
                            presenter.onFilterPanelFocusStateChanged(hasFocus)
                            if (hasFocus) {
                                presenter.onKeyboardOpened(false)
                            } else {
                                presenter.onKeyboardClosed(true)
                            }
                        }, FallbackErrorConsumer.DEFAULT
                    ),
                searchFieldEditorActionsObservable()
                    .subscribe(
                        { actionId ->
                            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH) {
                                presenter.onSearchButtonClicked()
                            }
                        }, FallbackErrorConsumer.DEFAULT
                    )
            )
        }
    }
}