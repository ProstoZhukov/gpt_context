package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.simple

import android.view.ViewGroup
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками [UniversalSelectionViewHolder].
 *
 * @property clickDelegate делегат обработки кликов по ячейке.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean
) : ViewHolderHelper<UniversalSelectionItem, UniversalSelectionViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): UniversalSelectionViewHolder =
        UniversalSelectionViewHolder(parentView)

    override fun bindToViewHolder(data: UniversalSelectionItem, viewHolder: UniversalSelectionViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}