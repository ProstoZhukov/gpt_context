package ru.tensor.sbis.design_selection.ui.content.listener

import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Реализация делегата по обработке кликов по элементам списка невыбранных.
 *
 * @property clickListener слушатель кликов по ячейкам списка невыбранных.
 *
 * @author vv.chekurda
 */
@Suppress("UNCHECKED_CAST")
internal class SelectionClickDelegateImpl<ITEM : SelectionItem>(
    private val clickListener: SelectionItemClickListener<ITEM>
) : SelectionClickDelegate {

    override fun onAddButtonClicked(item: SelectionItem) {
        clickListener.onAddButtonClicked(item as ITEM)
    }

    override fun onItemClicked(item: SelectionItem) {
        clickListener.onItemClicked(item as ITEM)
    }

    override fun onItemLongClicked(item: SelectionItem) {
        clickListener.onItemLongClicked(item as ITEM)
    }

    override fun onNavigateClicked(item: SelectionItem) {
        clickListener.onNavigateClicked(item as ITEM)
    }
}
