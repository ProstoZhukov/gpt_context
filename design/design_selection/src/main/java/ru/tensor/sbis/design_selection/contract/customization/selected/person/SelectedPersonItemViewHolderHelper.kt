package ru.tensor.sbis.design_selection.contract.customization.selected.person

import android.view.ViewGroup
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.customization.selected.folder.SelectedFolderItemViewHolder
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками выбранных персон [SelectedFolderItemViewHolder].
 *
 * @property clickDelegate делегат обработки кликов на ячейку.
 *
 * @author vv.chekurda
 */
class SelectedPersonItemViewHolderHelper(
    private val clickDelegate: SelectedItemClickDelegate,
    private val personClickListener: PersonClickListener? = null
) : ViewHolderHelper<SelectionPersonItem, SelectedPersonItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SelectedPersonItemViewHolder =
        SelectedPersonItemViewHolder(parentView, clickDelegate, personClickListener)

    override fun bindToViewHolder(
        data: SelectionPersonItem,
        viewHolder: SelectedPersonItemViewHolder
    ) {
        viewHolder.bind(data)
    }
}