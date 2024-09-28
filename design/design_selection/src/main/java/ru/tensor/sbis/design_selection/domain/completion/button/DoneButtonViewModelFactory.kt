package ru.tensor.sbis.design_selection.domain.completion.button

import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate
import javax.inject.Inject

/**
 * Фабрика для создания вью-модели кнопки подтверждения.
 *
 * @author vv.chekurda
 */
internal class DoneButtonViewModelFactory @Inject constructor() {

    /**
     * Создать вью-модель кнопки подтверждения по моду видимости [doneButtonMode].
     */
    fun create(doneButtonMode: SelectionDoneButtonVisibilityMode): DoneButtonDelegate<SelectionItem> =
        DoneButtonVisibilityViewModel(
            behavior = when (doneButtonMode) {
                SelectionDoneButtonVisibilityMode.VISIBLE -> AlwaysVisibleBehavior()
                SelectionDoneButtonVisibilityMode.SELECTED_CHANGED -> SelectionChangedBehavior()
                SelectionDoneButtonVisibilityMode.AT_LEAST_ONE -> AtLeastOneBehavior()
            }
        )
}