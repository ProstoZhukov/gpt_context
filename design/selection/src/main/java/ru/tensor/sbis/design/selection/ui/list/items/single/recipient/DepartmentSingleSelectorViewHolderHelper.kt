package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация [ViewHolderHelper] для работы с [DepartmentSingleSelectorItemViewHolder] (элемент рабочей группы)
 *
 * @author ma.kolpakov
 */
internal class DepartmentSingleSelectorViewHolderHelper(
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : ViewHolderHelper<DepartmentSelectorItemModel, DepartmentSingleSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): DepartmentSingleSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_department_single_item, parentView, false)
            .let { DepartmentSingleSelectorItemViewHolder(it, iconClickListener, activityProvider) }

    override fun bindToViewHolder(
        data: DepartmentSelectorItemModel,
        viewHolder: DepartmentSingleSelectorItemViewHolder
    ) {
        viewHolder.bind(data)
    }
}