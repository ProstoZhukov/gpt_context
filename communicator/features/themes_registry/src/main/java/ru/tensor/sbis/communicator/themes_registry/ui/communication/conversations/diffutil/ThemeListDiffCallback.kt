package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.diffutil

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.header_date.DateViewHolder.Companion.CHANGE_DATE_PAYLOAD
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.ConversationRegistryItem

/**
 * Реализация DiffUtil для реестра диалогов
 *
 * @author rv.krohalev
 */
internal class ThemeListDiffCallback(
    private val oldList: List<ConversationRegistryItem>,
    private val newList: List<ConversationRegistryItem>
) : DiffUtil.Callback() {

    private val themeItemMatcher = ThemeItemMatcher()

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        themeItemMatcher.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem is ContactVM && newItem is ContactVM) {
            areContactsTheSame(oldItem, newItem)
        } else if (oldItem is ConversationModel && newItem is ConversationModel) {
            oldItem == newItem &&
                arePreviousItemsTimestampTheSame(oldItemPosition, newItemPosition)
        } else {
            oldItem == newItem
        }
    }

    private fun areContactsTheSame(oldContact: ContactVM, newContact: ContactVM) =
        oldContact.name == newContact.name
                && oldContact.rawPhoto == newContact.rawPhoto
                && oldContact.data1 == newContact.data1
                && oldContact.data2 == newContact.data2
                && oldContact.activityStatus == newContact.activityStatus
                && oldContact.nameHighlight == newContact.nameHighlight

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem is ConversationModel && newItem is ConversationModel) {
            when {
                oldItem != newItem -> null
                arePreviousItemsTimestampTheSame(oldItemPosition, newItemPosition) -> CHANGE_DATE_PAYLOAD
                else -> null
            }
        } else {
            null
        }
    }

    private fun arePreviousItemsTimestampTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        var previousOldItem: ConversationModel? = null
        var previousNewItem: ConversationModel? = null
        if (oldItemPosition > 0 && newItemPosition > 0) {
            previousOldItem = oldList[oldItemPosition - 1] as? ConversationModel
            previousNewItem = newList[newItemPosition - 1] as? ConversationModel
        }
        return previousOldItem?.timestamp == previousNewItem?.timestamp
    }
}
