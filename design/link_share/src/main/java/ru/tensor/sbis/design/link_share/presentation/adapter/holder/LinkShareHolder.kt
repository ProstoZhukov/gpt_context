package ru.tensor.sbis.design.link_share.presentation.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.link_share.databinding.LinkShareBaseHolderBinding
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItem.CUSTOM
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMenuItemModel

/**@SelfDocumented */
internal class LinkShareHolder(
    binding: LinkShareBaseHolderBinding,
    private val onItemClick: (item: SbisLinkShareMenuItemModel) -> Unit
) : AbstractViewHolder<BaseItem<Any>>(binding.root) {

    constructor(parent: ViewGroup, onItemClick: (item: SbisLinkShareMenuItemModel) -> Unit) : this(
        LinkShareBaseHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onItemClick
    )

    private val textOfOption: SbisTextView = binding.linkShareBaseHolderTitle
    private val imageOfOption: SbisTextView = binding.linkShareBaseHolderImage

    override fun bind(dataModel: BaseItem<Any>) {
        super.bind(dataModel)
        with(dataModel.data as SbisLinkShareMenuItemModel) {
            imageOfOption.text = this.icon
            textOfOption.text = this.title
            textOfOption.setOnClickListener {
                if (type == CUSTOM) action.invoke() else onItemClick(this)
            }
        }
    }
}