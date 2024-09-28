package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import androidx.recyclerview.widget.ListUpdateCallback

/** @SelfDocumented */
internal class ConversationListUpdateCallback(
    private val adapter: ConversationListAdapter,
    private val offset: Int
) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(offset + position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(offset + position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(offset + fromPosition, offset + toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(offset + position, count, payload)
    }
}