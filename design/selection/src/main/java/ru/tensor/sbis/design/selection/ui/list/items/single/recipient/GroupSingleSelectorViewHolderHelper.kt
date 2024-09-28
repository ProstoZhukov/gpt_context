package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [GroupSingleSelectorItemViewHolder] (элемент группы соц. сети)
 *
 * @author ma.kolpakov
 */
internal class GroupSingleSelectorViewHolderHelper(
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : ViewHolderHelper<GroupSelectorItemModel, GroupSingleSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): GroupSingleSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_group_single_item, parentView, false)
            .let { GroupSingleSelectorItemViewHolder(it, iconClickListener, activityProvider) }

    override fun bindToViewHolder(data: GroupSelectorItemModel, viewHolder: GroupSingleSelectorItemViewHolder) {
        viewHolder.bind(data)
    }
}