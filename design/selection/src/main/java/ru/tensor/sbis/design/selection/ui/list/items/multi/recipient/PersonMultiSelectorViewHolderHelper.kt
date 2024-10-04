package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [PersonMultiSelectorItemViewHolder] (элемент персоны)
 *
 * @author ma.kolpakov
 */
internal class PersonMultiSelectorViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?
) : ViewHolderHelper<PersonSelectorItemModel, PersonMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): PersonMultiSelectorItemViewHolder {
        return PersonSelectorItemView(
            parentView.context,
            defStyleAttr = R.attr.Selector_personMultiItemTheme,
            defStyleRes = R.style.SelectionRecipientItemTheme_Multi_Person
        ).let { itemView ->
            PersonMultiSelectorItemViewHolder(
                itemView,
                clickDelegate,
                iconClickListener,
                activityProvider
            )
        }
    }

    override fun bindToViewHolder(data: PersonSelectorItemModel, viewHolder: PersonMultiSelectorItemViewHolder) {
        viewHolder.bind(data)
    }

}