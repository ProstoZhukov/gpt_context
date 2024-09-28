package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.diff_util

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.util.ContactItemMatcher

/**
 * Реализация колбэка для DiffUtils для сравнения элементов списка.
 *
 * @author vv.chekurda
 */
internal class ContactListDiffCallback(
    private val oldList: List<ContactRegistryModel>,
    private val newList: List<ContactRegistryModel>
) : DiffUtil.Callback() {

    private val contactItemMatcher = ContactItemMatcher()

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        contactItemMatcher.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition] && arePreviousItemLastMessageDateTheSame(
            oldItemPosition,
            newItemPosition
        )

    private fun arePreviousItemLastMessageDateTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var previousOldItem: ContactsModel? = null
        var previousNewItem: ContactsModel? = null
        if (oldItemPosition > 0 && newItemPosition > 0) {
            previousOldItem = oldList[oldItemPosition - 1] as? ContactsModel
            previousNewItem = newList[newItemPosition - 1] as? ContactsModel
        }
        return previousOldItem?.lastMessageDate == previousNewItem?.lastMessageDate
    }
}