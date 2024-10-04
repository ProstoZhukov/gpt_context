package ru.tensor.sbis.design.selection.ui.list.items.single.region

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация [ViewHolderHelper] для работы с [RegionSingleSelectorItemViewHolder] (элемент региона/нас. пункта)
 *
 * @author us.bessonov
 */
internal class RegionSingleSelectorViewHolderHelper :
    ViewHolderHelper<RegionSelectorItemModel, RegionSingleSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): RegionSingleSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_region_single_item, parentView, false)
            .run(::RegionSingleSelectorItemViewHolder)

    override fun bindToViewHolder(data: RegionSelectorItemModel, viewHolder: RegionSingleSelectorItemViewHolder) {
        viewHolder.bind(data)
    }
}