package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.folder

import android.view.ViewGroup
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками
 * однострочных папок [SingleLineSelectionFolderItemViewHolder].
 *
 * @author vv.chekurda
 */
class SingleLineSelectionFolderViewHolderHelper<ITEM : SelectionFolderItem>(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean = true
) : ViewHolderHelper<ITEM, SingleLineSelectionFolderItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SingleLineSelectionFolderItemViewHolder =
        SingleLineSelectionFolderItemViewHolder(parentView)

    override fun bindToViewHolder(data: ITEM, viewHolder: SingleLineSelectionFolderItemViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}