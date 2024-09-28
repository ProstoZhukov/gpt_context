package ru.tensor.sbis.design.selection.ui.list.items.multi.region

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация [ViewHolderHelper] для работы с [RegionMultiSelectorItemViewHolder] (элемент региона/нас. пункта)
 *
 * @author ma.kolpakov
 */
internal class RegionMultiSelectorViewHolderHelper(
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>
) : ViewHolderHelper<RegionSelectorItemModel, RegionMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): RegionMultiSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_region_multi_item, parentView, false)
            .let { RegionMultiSelectorItemViewHolder(it, clickDelegate) }

    override fun bindToViewHolder(data: RegionSelectorItemModel, viewHolder: RegionMultiSelectorItemViewHolder) {
        viewHolder.bind(data)
    }
}