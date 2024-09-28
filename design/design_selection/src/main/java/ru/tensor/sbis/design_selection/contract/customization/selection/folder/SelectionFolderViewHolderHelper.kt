package ru.tensor.sbis.design_selection.contract.customization.selection.folder

import android.view.ViewGroup
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками папок [SelectionFolderItemViewHolder].
 *
 * @author vv.chekurda
 */
class SelectionFolderViewHolderHelper<ITEM : SelectionFolderItem>(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean = true
) : ViewHolderHelper<ITEM, SelectionFolderItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SelectionFolderItemViewHolder =
        SelectionFolderItemViewHolder(parentView)

    override fun bindToViewHolder(data: ITEM, viewHolder: SelectionFolderItemViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}