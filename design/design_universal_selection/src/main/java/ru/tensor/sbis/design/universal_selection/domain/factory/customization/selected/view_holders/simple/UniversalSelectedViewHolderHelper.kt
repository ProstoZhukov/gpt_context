package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selected.view_holders.simple

import android.view.ViewGroup
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками выбранных элементов [UniversalSelectedViewHolder].
 *
 * @property clickDelegate делегат обработки кликов на ячейку.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectedViewHolderHelper(
    private val clickDelegate: SelectedItemClickDelegate
) : ViewHolderHelper<UniversalSelectionItem, UniversalSelectedViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): UniversalSelectedViewHolder =
        UniversalSelectedViewHolder(parentView, clickDelegate)

    override fun bindToViewHolder(data: UniversalSelectionItem, viewHolder: UniversalSelectedViewHolder) {
        viewHolder.bind(data)
    }
}