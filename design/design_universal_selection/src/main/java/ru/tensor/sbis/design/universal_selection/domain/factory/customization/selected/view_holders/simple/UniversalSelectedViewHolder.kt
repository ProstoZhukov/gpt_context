package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selected.view_holders.simple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.universal_selection.R
import ru.tensor.sbis.design.universal_selection.databinding.DesignUniversalSelectionSelectedItemBinding
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate

/**
 * Ячейка обычного элемента универсального справочника.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectedViewHolder(
    parentView: ViewGroup,
    clickDelegate: SelectedItemClickDelegate
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.design_universal_selection_selected_item, parentView, false)
) {
    private val binding = DesignUniversalSelectionSelectedItemBinding.bind(itemView)
    private lateinit var data: UniversalSelectionItem

    init {
        binding.universalSelectedItemCloseIcon.setOnClickListener {
            clickDelegate.onUnselectClicked(data)
        }
    }

    fun bind(data: UniversalSelectionItem) {
        this.data = data
        binding.universalSelectedItemTitle.setTextWithHighlight(data.title, data.titleHighlights)
    }
}