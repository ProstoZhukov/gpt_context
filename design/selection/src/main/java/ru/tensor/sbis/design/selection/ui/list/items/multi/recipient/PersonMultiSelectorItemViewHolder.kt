package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.single.recipient.PersonSingleSelectorItemViewHolder
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView
import javax.inject.Provider

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о персоне в компоненте выбора.
 *
 * @author vv.chekurda
 */
internal class PersonMultiSelectorItemViewHolder(
    personItemView: PersonSelectorItemView,
    clickDelegate: SelectionClickDelegate<SelectorItemModel>,
    iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
    activityProvider: Provider<FragmentActivity>?
) : PersonSingleSelectorItemViewHolder(personItemView, iconClickListener, activityProvider) {

    init {
        personItemView.findViewById<View>(R.id.selection_icon_click_area).setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
    }

    override fun bind(data: PersonSelectorItemModel) {
        super.bind(data)
        personItemView.isSelected = data.meta.isSelected
    }
}