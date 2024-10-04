package ru.tensor.sbis.design.recipient_selection.ui.items.single_line.person

import android.view.ViewGroup
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация делегата [ViewHolderHelper] для работы с ячейками
 * однострочных персон [SingleLineSelectionPersonItemViewHolder].
 *
 * @author vv.chekurda
 */
class SingleLineSelectionPersonViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate,
    private val viewHolderType: Int,
    private val isMultiSelection: Boolean = true,
    private val personClickListener: PersonClickListener? = null
) : ViewHolderHelper<SelectionPersonItem, SingleLineSelectionPersonItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): SingleLineSelectionPersonItemViewHolder =
        SingleLineSelectionPersonItemViewHolder(parentView, personClickListener)

    override fun bindToViewHolder(data: SelectionPersonItem, viewHolder: SingleLineSelectionPersonItemViewHolder) {
        viewHolder.bind(data, clickDelegate, isMultiSelection)
    }

    override fun getViewHolderType(): Any = viewHolderType
}