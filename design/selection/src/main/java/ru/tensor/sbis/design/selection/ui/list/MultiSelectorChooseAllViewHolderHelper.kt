package ru.tensor.sbis.design.selection.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * @author ma.kolpakov
 */
internal class MultiSelectorChooseAllViewHolderHelper :
    ViewHolderHelper<SelectorItemModel, MultiSelectorChooseAllItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): MultiSelectorChooseAllItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_choose_all_item_multi, parentView, false)
            .let { MultiSelectorChooseAllItemViewHolder(it) }

    override fun bindToViewHolder(data: SelectorItemModel, viewHolder: MultiSelectorChooseAllItemViewHolder) {
        viewHolder.bind(data)
    }
}