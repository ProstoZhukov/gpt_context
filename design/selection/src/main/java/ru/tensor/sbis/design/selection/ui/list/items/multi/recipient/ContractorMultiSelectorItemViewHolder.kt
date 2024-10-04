package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.items.single.recipient.ContractorSingleSelectorItemViewHolder
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.PersonSelectorItemView

/**
 * Реализация [RecyclerView.ViewHolder] для отображения информации о контрагенте в компоненте выбора.
 *
 * @author us.bessonov
 */
internal class ContractorMultiSelectorItemViewHolder(
    itemView: PersonSelectorItemView,
    clickDelegate: SelectionClickDelegate<SelectorItemModel>
) : ContractorSingleSelectorItemViewHolder(itemView) {

    init {
        itemView.findViewById<View>(R.id.selection_icon_click_area).setOnClickListener {
            clickDelegate.onAddButtonClicked(data)
        }
    }

    override fun bind(data: ContractorSelectorItemModel) {
        super.bind(data)
        itemView.isSelected = data.meta.isSelected
    }
}