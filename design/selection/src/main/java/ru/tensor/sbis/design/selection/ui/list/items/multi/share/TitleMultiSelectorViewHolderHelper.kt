package ru.tensor.sbis.design.selection.ui.list.items.multi.share

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Хэлпер для отображения заголовка через [TitleMultiSelectorItemViewHolder]
 *
 * @author ma.kolpakov
 */
internal class TitleMultiSelectorViewHolderHelper : ViewHolderHelper<TitleItemModel, TitleMultiSelectorItemViewHolder> {

    override fun createViewHolder(parentView: ViewGroup): TitleMultiSelectorItemViewHolder =
        LayoutInflater.from(parentView.context)
            .inflate(R.layout.selection_list_title_item, parentView, false)
            .let { TitleMultiSelectorItemViewHolder(it) }

    override fun bindToViewHolder(data: TitleItemModel, viewHolder: TitleMultiSelectorItemViewHolder) {
        viewHolder.bind(data)
    }
}
