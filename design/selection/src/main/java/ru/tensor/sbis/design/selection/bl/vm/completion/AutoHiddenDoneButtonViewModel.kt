package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.ui.contract.SelectorDoneButtonVisibilityMode

/**
 * Реализации [DoneButtonViewModel] для режима работы [SelectorDoneButtonVisibilityMode.AUTO_HIDDEN].
 *
 * Применяется правило, которое разрешает применять выбор, если состав выбранных элементов изменился относительно
 * начального.
 * Правило некорректно работает с повторами в списке выбранных, их отсутствие нужно обеспечивать на внешнем уровне
 *
 * @author ma.kolpakov
 */
internal class AutoHiddenDoneButtonViewModel(
    visibilityFunction: BiFunction<List<SelectorItem>, List<SelectorItem>, Boolean>
) : DoneButtonViewModel {

    private val initialSelectionSubject = BehaviorSubject.create<List<SelectorItem>>()
    private val selectionSubject = BehaviorSubject.create<List<SelectorItem>>()

    override val doneButtonVisible: Observable<Boolean> = selectionSubject
        .withLatestFrom(initialSelectionSubject, visibilityFunction)

    /**
     * Не требуется переопределения, если вьюмодель управляет только видимостью кнопки
     */
    override val doneButtonEnabled: Observable<Boolean> = Observable.empty()

    override fun setInitialData(data: List<SelectorItem>) {
        initialSelectionSubject.onNext(data)
    }

    override fun setSelectedData(data: List<SelectorItem>) {
        selectionSubject.onNext(data)
    }
}