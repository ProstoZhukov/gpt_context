package ru.tensor.sbis.design_selection.contract.customization.selected.folder

import android.view.ViewGroup
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками выбранных папок [SelectedFolderItemViewHolder].
 *
 * @property clickDelegate делегат обработки кликов на ячейку.
 *
 * @author vv.chekurda
 */
class SelectedFolderItemViewHolderHelper(
    private val clickDelegate: SelectedItemClickDelegate
) : ViewHolderHelper<SelectionFolderItem, SelectedFolderItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SelectedFolderItemViewHolder =
        SelectedFolderItemViewHolder(parentView, clickDelegate)

    override fun bindToViewHolder(
        data: SelectionFolderItem,
        viewHolder: SelectedFolderItemViewHolder
    ) {
        viewHolder.bind(data)
    }
}