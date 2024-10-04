package ru.tensor.sbis.design.message_view.utils

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.design.message_view.model.MessageViewData

/**
 * Реализация [DiffUtil.Callback] списка сообщений для любого чата, в котором используется MessageViewData.
 *
 * @property last    старый список сообщений.
 * @property current текущий обрабатываемый.
 *
 * @author dv.baranov
 */
class MessageViewListDiffCallback(
    private val last: List<MessageViewData>,
    private val current: List<MessageViewData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = last.size

    override fun getNewListSize(): Int = current.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val lastItem = last[oldItemPosition]
        val currentItem = current[newItemPosition]
        return lastItem.areTheSame(currentItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return messageViewDataTheSame(last[oldItemPosition], current[newItemPosition])
    }

    private fun messageViewDataTheSame(old: MessageViewData, new: MessageViewData): Boolean =
        old.type == new.type && old.hasTheSameContent(new)
}