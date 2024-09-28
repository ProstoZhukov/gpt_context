package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.folders

import android.view.ViewGroup
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionFolderItem
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками [UniversalSelectionFolderViewHolder].
 *
 * @property clickDelegate делегат обработки кликов по ячейке.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionFolderViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean
) : ViewHolderHelper<UniversalSelectionFolderItem, UniversalSelectionFolderViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): UniversalSelectionFolderViewHolder =
        UniversalSelectionFolderViewHolder(parentView)

    override fun bindToViewHolder(data: UniversalSelectionFolderItem, viewHolder: UniversalSelectionFolderViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}