package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [PersonSingleSelectorItemViewHolder] (элемент персоны)
 *
 * @author ma.kolpakov
 */
internal class PersonSingleSelectorViewHolderHelper(
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?
) : ViewHolderHelper<PersonSelectorItemModel, PersonSingleSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): PersonSingleSelectorItemViewHolder {
        return PersonSelectorItemView(
            parentView.context,
            defStyleAttr = ResourcesCompat.ID_NULL,
            defStyleRes = R.style.SelectionRecipientItemTheme_Single_Person
        ).let { itemView ->
            PersonSingleSelectorItemViewHolder(
                itemView,
                iconClickListener,
                activityProvider
            )
        }
    }

    override fun bindToViewHolder(data: PersonSelectorItemModel, viewHolder: PersonSingleSelectorItemViewHolder) {
        viewHolder.bind(data)
    }

}