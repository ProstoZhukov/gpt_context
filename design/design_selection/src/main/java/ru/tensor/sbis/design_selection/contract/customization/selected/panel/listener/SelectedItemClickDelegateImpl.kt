package ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener

import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Реализация делегата по обработке кликов по невыбранным элементам компонента выбора.
 *
 * @property clickListener слушатель кликов по невыбранным элементам.
 *
 * @author vv.chekurda
 */
internal class SelectedItemClickDelegateImpl<ITEM : SelectionItem>(
    private val clickListener: SelectedItemClickListener<ITEM>
) : SelectedItemClickDelegate {

    override fun onUnselectClicked(item: SelectionItem) {
        clickListener.onUnselectClicked(item)
    }
}