package ru.tensor.sbis.design_selection.domain.completion.button

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate

/**
 * Вью-модель кнопки подтверждения выбора в компоненте выбора.
 *
 * @param behavior поведение видимости кнопки, где
 * первый аргумент - список предвыбранных элементов, с которым инициализурется компонент.
 * второй аргумент - текущий список выбранных элементов.
 *
 * @author vv.chekurda
 */
internal class DoneButtonVisibilityViewModel(
    behavior: BiFunction<SelectedData<SelectionItem>, SelectedData<SelectionItem>, Boolean>
) : DoneButtonDelegate<SelectionItem> {

    private val initialSelectionSubject = BehaviorSubject.create<SelectedData<SelectionItem>>()
    private val selectionSubject = BehaviorSubject.create<SelectedData<SelectionItem>>()

    override val doneButtonVisible: Observable<Boolean> = selectionSubject
        .withLatestFrom(initialSelectionSubject, behavior)

    override fun setInitialData(data: SelectedData<SelectionItem>) {
        initialSelectionSubject.onNext(data)
        selectionSubject.onNext(data)
    }

    override fun setSelectedData(data: SelectedData<SelectionItem>) {
        selectionSubject.onNext(data)
    }
}