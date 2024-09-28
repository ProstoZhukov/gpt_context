package ru.tensor.sbis.design.link_share.presentation.adapter.holder

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem

/**@SelfDocumented*/
internal class CustomBlockLineHolder(
    parent: ViewGroup
) : AbstractViewHolder<BaseItem<Any>>(CustomBlockLineView(parent.context)) {

    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
    }
}
