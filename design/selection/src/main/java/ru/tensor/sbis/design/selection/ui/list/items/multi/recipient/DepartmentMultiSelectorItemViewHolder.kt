package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.multi.DefaultMultiSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import javax.inject.Provider

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о рабочей группе в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal class DepartmentMultiSelectorItemViewHolder(
    view: View,
    clickDelegate: SelectionClickDelegate<SelectorItemModel>,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : DefaultMultiSelectorViewHolder<DepartmentSelectorItemModel>(view, clickDelegate) {

    init {
        if (iconClickListener != null && activityProvider?.get() != null) {
            view.findViewById<View>(R.id.groupIcon).setOnClickListener {
                iconClickListener.onClicked(activityProvider.get(), data)
            }
        }
    }

    override fun getSubtitle(data: DepartmentSelectorItemModel): String? = with(data) {
        when {
            !subtitle.isNullOrEmpty() && membersCount > 0 -> "$subtitle ($membersCount)"
            !subtitle.isNullOrEmpty() && membersCount == 0 -> subtitle
            subtitle.isNullOrEmpty() && membersCount > 0 -> ("($membersCount)")
            else -> null
        }
    }

    override fun isSubtitleVisible(data: DepartmentSelectorItemModel): Boolean =
        !data.subtitle.isNullOrEmpty() || data.membersCount != 0
}
