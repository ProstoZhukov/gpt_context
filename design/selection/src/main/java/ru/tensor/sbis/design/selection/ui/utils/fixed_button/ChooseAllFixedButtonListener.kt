package ru.tensor.sbis.design.selection.ui.utils.fixed_button

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация [FixedButtonListener] для работы с кнопкой "Выбрать все"
 *
 * @author ma.kolpakov
 */
internal class ChooseAllFixedButtonListener(
    private val action: (SelectorItemModel) -> Unit
) : FixedButtonListener<SelectorItemModel, FragmentActivity> {

    override fun onButtonClicked(activity: FragmentActivity, result: SelectorItemModel) {
        action.invoke(result)
    }
}