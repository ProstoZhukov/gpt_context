package ru.tensor.sbis.order_message.adapter.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.order_message.databinding.OrderMessageTitleHolderBinding

/**@SelfDocumented*/
internal class TitleHolder private constructor(
    private val binding: OrderMessageTitleHolderBinding
) : AbstractViewHolder<BaseItem<Any>>(binding.root) {

    /**@SelfDocumented*/
    constructor(parent: ViewGroup) : this(
        OrderMessageTitleHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @Suppress("UNCHECKED_CAST")
    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
        val (title, subtitle) = model.data as Pair<String, String>
        binding.orderMessageTitle.text = title
        binding.orderMessageMessage.text = subtitle
    }
}