package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [DepartmentMultiSelectorItemViewHolder] (элемент рабочей группы)
 *
 * @author ma.kolpakov
 */
internal class DepartmentMultiSelectorViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : ViewHolderHelper<DepartmentSelectorItemModel, DepartmentMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): DepartmentMultiSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_department_multi_item, parentView, false)
            .let { DepartmentMultiSelectorItemViewHolder(it, clickDelegate, iconClickListener, activityProvider) }

    override fun bindToViewHolder(
        data: DepartmentSelectorItemModel,
        viewHolder: DepartmentMultiSelectorItemViewHolder
    ) {
        viewHolder.bind(data)
    }
}