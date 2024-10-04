package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.single.DefaultSingleSelectorViewHolder
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.fresco_view.SuperEllipseDraweeView
import javax.inject.Provider

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о группе (соц. сети) в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal class GroupSingleSelectorItemViewHolder(
    view: View,
    private val iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    private val activityProvider: Provider<FragmentActivity>?,
) : DefaultSingleSelectorViewHolder<GroupSelectorItemModel>(view) {

    private val groupImage: SuperEllipseDraweeView = view.findViewById(R.id.groupImage)

    init {
        if (iconClickListener != null && activityProvider?.get() != null) {
            groupImage.setOnClickListener {
                iconClickListener.onClicked(activityProvider.get(), data)
            }
        }
    }

    override fun bind(data: GroupSelectorItemModel) {
        super.bind(data)

        groupImage.setImageURI(data.imageUri)
    }
}