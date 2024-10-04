package ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Реализация [DiffUtil.Callback] для списка выбранных элементов.
 *
 * @author vv.chekurda
 */
internal class SelectedItemsDiffCallback(
    private val oldList: List<SelectionItem>,
    private val newList: List<SelectionItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}