package ru.tensor.sbis.design.link_share.presentation.adapter

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractListAdapter
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.link_share.presentation.adapter.holder.LinkShareHolder
import ru.tensor.sbis.design.link_share.R
import ru.tensor.sbis.design.link_share.presentation.adapter.holder.CustomBlockLineHolder
import ru.tensor.sbis.design.link_share.presentation.adapter.holder.CustomBlockTitleHolder
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItemModel
import javax.inject.Inject

/**@SelfDocumented */
internal class LinkShareAdapter @Inject constructor() :
    AbstractListAdapter<BaseItem<Any>, AbstractViewHolder<BaseItem<Any>>>() {

    /**@SelfDocumented*/
    var onItemClick: (item: SbisLinkShareMenuItemModel) -> Unit = {}

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<BaseItem<Any>> =
        when (viewType) {
            R.id.sbis_link_share_base_item -> LinkShareHolder(parent, onItemClick)
            R.id.sbis_link_share_custom_block_title -> CustomBlockTitleHolder(parent)
            R.id.sbis_link_share_custom_block_line -> CustomBlockLineHolder(parent)
            else -> AbstractViewHolder(parent)
        }

    override fun onBindViewHolder(holder: AbstractViewHolder<BaseItem<Any>>, position: Int) =
        holder.bind(getItem(position))
}