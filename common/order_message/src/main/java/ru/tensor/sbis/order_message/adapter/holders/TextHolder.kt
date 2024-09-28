package ru.tensor.sbis.order_message.adapter.holders

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem

/**@SelfDocumented*/
internal class TextHolder(
    parent: ViewGroup
) : AbstractViewHolder<BaseItem<Any>>(HeaderItemView(parent.context)) {

    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
        (itemView as HeaderItemView).setData(model.data as String)
    }
}