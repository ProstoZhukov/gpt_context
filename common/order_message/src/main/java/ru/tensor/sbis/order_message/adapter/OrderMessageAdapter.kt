package ru.tensor.sbis.order_message.adapter

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractListAdapter
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.order_message.R
import ru.tensor.sbis.order_message.adapter.holders.AvailableHolder
import ru.tensor.sbis.order_message.adapter.holders.NomenclatureHolder
import ru.tensor.sbis.order_message.adapter.holders.QueueHolder
import ru.tensor.sbis.order_message.adapter.holders.StopListNomenclatureHolder
import ru.tensor.sbis.order_message.adapter.holders.TextHolder
import ru.tensor.sbis.order_message.adapter.holders.TitleHolder

/**
 * Адаптер для списка номенклатур
 */
internal class OrderMessageAdapter : AbstractListAdapter<BaseItem<Any>, AbstractViewHolder<BaseItem<Any>>>() {

    /**@SelfDocumented*/
    var withImage: Boolean = false

    override fun getItemViewType(position: Int): Int = getItem(position).type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<BaseItem<Any>> =
        when (viewType) {
            R.id.order_message_text -> TextHolder(parent)
            R.id.order_message_queues -> QueueHolder(parent)
            R.id.order_message_title -> TitleHolder(parent)
            R.id.order_message_nomenclature -> NomenclatureHolder(parent, withImage)
            R.id.order_message_stop_list_nomenclature ->
                StopListNomenclatureHolder(parent, withImage)

            R.id.order_message_stop_list_available -> AvailableHolder(parent)
            else -> AbstractViewHolder(parent)
        }

    override fun onBindViewHolder(holder: AbstractViewHolder<BaseItem<Any>>, position: Int) {
        holder.bind(getItem(position))
    }
}