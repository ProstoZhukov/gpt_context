package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация [ViewHolderHelper] для работы с [ContractorSingleSelectorItemViewHolder]
 *
 * @author us.bessonov
 */
internal class ContractorSingleSelectorViewHolderHelper :
    ViewHolderHelper<ContractorSelectorItemModel, ContractorSingleSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): ContractorSingleSelectorItemViewHolder {
        return PersonSelectorItemView(
            parentView.context,
            defStyleAttr = ResourcesCompat.ID_NULL,
            defStyleRes = R.style.SelectionRecipientItemTheme_Single_Person
        ).let { itemView ->
            ContractorSingleSelectorItemViewHolder(itemView)
        }
    }

    override fun bindToViewHolder(
        data: ContractorSelectorItemModel,
        viewHolder: ContractorSingleSelectorItemViewHolder
    ) = viewHolder.bind(data)

}