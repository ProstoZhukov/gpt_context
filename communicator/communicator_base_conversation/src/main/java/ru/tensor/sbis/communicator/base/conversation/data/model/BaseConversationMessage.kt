package ru.tensor.sbis.communicator.base.conversation.data.model

import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.model.MessageViewData
import java.util.*

/**
 * Базовый интерфейс сообщения в переписке.
 *
 * @author vv.chekurda
 */
interface BaseConversationMessage : ListItem {
    val message: Message?
    val conversationServiceMessage: BaseServiceMessage?

    val uuid: UUID
    var groupConversation: Boolean

    /**
     * true, если сообщение зависимо от контроллера и может перерисоваться -
     * стать больше или меньше из-за этого.
     */
    val isRedrawable: Boolean
    val isReadByMe: Boolean
    val readByReceiver: Boolean
    val timestampSent: Long
    val removableType: MessageRemovableType?
    val viewData: MessageViewData

    fun getSyncStatus(): SyncStatus = message?.syncStatus ?: SyncStatus.SUCCEEDED

    fun setRead(read: Boolean) = message?.also { it.readByMe = read } ?: run { conversationServiceMessage!!.read = read }
    fun isOutgoing(): Boolean
    fun isForMe(): Boolean

    val hasQuote: Boolean
        get() = message?.content?.any { it.quote != null } ?: false
}