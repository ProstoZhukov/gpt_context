package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import javax.inject.Provider

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о рабочей группе в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal class DepartmentSingleSelectorItemViewHolder(
    view: View,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : DefaultSingleSelectorViewHolder<DepartmentSelectorItemModel>(view) {

    init {
        if (iconClickListener != null && activityProvider?.get() != null) {
            view.findViewById<View>(R.id.groupIcon).setOnClickListener {
                iconClickListener.onClicked(activityProvider.get(), data)
            }
        }
    }
}