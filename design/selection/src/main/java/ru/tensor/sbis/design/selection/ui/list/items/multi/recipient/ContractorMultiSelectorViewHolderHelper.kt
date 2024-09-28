package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.ViewGroup
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация [ViewHolderHelper] для работы с [ContractorMultiSelectorItemViewHolder]
 *
 * @author us.bessonov
 */
internal class ContractorMultiSelectorViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>
) : ViewHolderHelper<ContractorSelectorItemModel, ContractorMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): ContractorMultiSelectorItemViewHolder {

        return PersonSelectorItemView(
            parentView.context,
            defStyleAttr = R.attr.Selector_personMultiItemTheme,
            defStyleRes = R.style.SelectionRecipientItemTheme_Multi_Person
        ).let { itemView ->
            ContractorMultiSelectorItemViewHolder(
                itemView,
                clickDelegate
            )
        }
    }

    override fun bindToViewHolder(
        data: ContractorSelectorItemModel,
        viewHolder: ContractorMultiSelectorItemViewHolder
    ) = viewHolder.bind(data)
}