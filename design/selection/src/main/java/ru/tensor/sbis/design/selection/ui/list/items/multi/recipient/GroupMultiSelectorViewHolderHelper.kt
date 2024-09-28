package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [GroupMultiSelectorItemViewHolder] (элемент групп соц.сетей)
 *
 * @author ma.kolpakov
 */
internal class GroupMultiSelectorViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : ViewHolderHelper<GroupSelectorItemModel, GroupMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): GroupMultiSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_group_multi_item, parentView, false)
            .let { GroupMultiSelectorItemViewHolder(it, clickDelegate, iconClickListener, activityProvider) }

    override fun bindToViewHolder(data: GroupSelectorItemModel, viewHolder: GroupMultiSelectorItemViewHolder) {
        viewHolder.bind(data)
    }
}