package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.diff_util

import androidx.recyclerview.widget.ListUpdateCallback
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter

/**
 * Реализация колбэка для DiffUtils для обновления списка.
 *
 * @author vv.chekurda
 */
internal class ContactListUpdateCallback(
    private val adapter: ContactListAdapter,
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