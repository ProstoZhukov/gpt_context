package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode

/**
 * Реализация [DoneButtonViewModel] для режима работы [SelectorDoneButtonVisibilityMode.AUTO_DISABLE].
 * Обеспечивает активность кнопки подтверждения только при наличии изменений выбора относительно начального.
 *
 * @author us.bessonov
 */
internal class AutoDisableDoneButtonViewModel : DoneButtonViewModel {

    private val initialSelectionSubject = BehaviorSubject.create<List<SelectorItem>>()
    private val selectionSubject = BehaviorSubject.create<List<SelectorItem>>()

    override val doneButtonEnabled: Observable<Boolean> = selectionSubject
        .withLatestFrom(initialSelectionSubject, SelectionChangeFunction())

    override val doneButtonVisible = Observable.just(true)

    override fun setInitialData(data: List<SelectorItem>) {
        initialSelectionSubject.onNext(data)
    }

    override fun setSelectedData(data: List<SelectorItem>) {
        selectionSubject.onNext(data)
    }
}