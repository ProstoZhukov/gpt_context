package ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItem

/**
 * Реализация [DiffUtil.Callback] для панели выбранных элементов
 *
 * @author us.bessonov
 */
internal class SelectedItemsDiffCallback(
    private val oldList: List<SelectedItem>,
    private val newList: List<SelectedItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}