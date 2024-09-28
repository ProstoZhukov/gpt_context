package ru.tensor.sbis.design_selection.contract.customization.selection.person

import android.view.ViewGroup
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками персон [SelectionPersonItemViewHolder].
 *
 * @author vv.chekurda
 */
class SelectionPersonViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean = true,
    private val personClickListener: PersonClickListener? = null
) : ViewHolderHelper<SelectionPersonItem, SelectionPersonItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SelectionPersonItemViewHolder =
        SelectionPersonItemViewHolder(parentView, personClickListener)

    override fun bindToViewHolder(data: SelectionPersonItem, viewHolder: SelectionPersonItemViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}