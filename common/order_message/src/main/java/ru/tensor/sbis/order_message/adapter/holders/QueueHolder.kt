package ru.tensor.sbis.order_message.adapter.holders

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.order_message.R
import ru.tensor.sbis.queue_list_decl.models.Queue
import ru.tensor.sbis.queue_list_decl.models.isNotStub

/**@SelfDocumented*/
internal class QueueHolder(
    parent: ViewGroup
) : AbstractViewHolder<BaseItem<Any>>(HeaderItemView(parent.context, withImage = true)) {

    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
        with(model.data as Queue) {
            val hasNotStub = isNotStub()
            val title =
                if (hasNotStub) fullName
                else itemView.resources.getString(R.string.order_message_queue_no_specialist)
            (itemView as HeaderItemView).setData(title, photoLink, hasNotStub.not())
        }
    }
}