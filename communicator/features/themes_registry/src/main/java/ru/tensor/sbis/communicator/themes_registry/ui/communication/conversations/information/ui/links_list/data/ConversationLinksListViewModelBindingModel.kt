package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import android.text.Spannable
import android.view.View
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import java.util.UUID

/**
 * UI модель элемента списка ссылок на экране информации о диалоге/канале.
 *
 * @property id идентификатор объекта.
 * @property link текст ссылки.
 * @property messageId uuid сообщения, в котором эта ссылка отправлена.
 * @property isPinned признак того, что ссылка закреплена.
 * @property onLongItemClick действие на лонгклик по ссылке.
 * @property convertAction действие конвертации текста ссылки в декорированную ссылку.
 *
 * @author dv.baranov
 */
internal data class ConversationLinksListViewModelBindingModel(
    val id: UUID,
    val link: String,
    val messageId: UUID?,
    val isPinned: Boolean,
    val onLongItemClick: (view: View) -> Unit,
    val convertAction: (text: String) -> Spannable
) : ComparableItem<ConversationLinksListViewModelBindingModel> {

    override fun areTheSame(otherItem: ConversationLinksListViewModelBindingModel): Boolean = id == otherItem.id

    /** @SelfDocumented */
    fun onLongClick(view: View): Boolean {
        onLongItemClick(view)
        return true
    }
}
