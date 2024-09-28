package ru.tensor.sbis.communicator.base.conversation.presentation.adapter

import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.core.views.safeUpdate

/**
 * Класс для точечного обновления сообщений.
 *
 * @author rv.krohalev
 */
internal class BaseConversationUpdateCallback<T : BaseConversationMessage>(
    private val adapter: BaseConversationAdapter<T>,
    private val offset: Int,
    private val recyclerView: RecyclerView?
) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) = recyclerView.safeUpdate {
        adapter.notifyItemRangeInserted(offset + position, count)
    }

    override fun onRemoved(position: Int, count: Int) = recyclerView.safeUpdate {
        adapter.notifyItemRangeRemoved(offset + position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) = recyclerView.safeUpdate {
        adapter.notifyItemMoved(offset + fromPosition, offset + toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) = recyclerView.safeUpdate {
        adapter.notifyItemRangeChanged(offset + position, count, payload)
    }
}