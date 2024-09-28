package ru.tensor.sbis.communicator.sbis_conversation.data.model

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.design.message_view.model.MessageViewData
import java.util.*

/**
 * Модель сообщения для переписки
 *
 * @param message - модель сообщения
 * @param conversationServiceMessage - модель сервисного сообщения
 * @param isChannel - true, если переписка по каналу
 */
internal data class ConversationMessage constructor(
    override val message: Message? = null,
    override val conversationServiceMessage: ConversationServiceMessage? = null,
    override val viewData: MessageViewData,
    var isChannel: Boolean
) : BaseConversationMessage {

    /** UUID модели сообщения в зависимости от его вида (обычное или сервисное) */
    override val itemTypeId: String
        get() = when {
            message != null                    -> UUIDUtils.toString(message.uuid)
            conversationServiceMessage != null -> UUIDUtils.toString(conversationServiceMessage.uuid)
            else                               -> ""
        }

    override fun areContentsTheSame(other: Any?): Boolean = equals(other)

    override val uuid: UUID
        get() = message?.uuid ?: conversationServiceMessage!!.uuid

    override var groupConversation: Boolean
        get() = viewData.groupConversation
        set(value) {
            viewData.groupConversation = value
        }

    override val isRedrawable: Boolean
        get() = isOutgoing() && hasQuote || hasSignature

    override val isReadByMe: Boolean
        get() = message?.readByMe
            ?: (conversationServiceMessage!!.read &&
                (conversationServiceMessage.serviceMessageGroup?.unreadCount ?: 0) == 0
            )

    override val readByReceiver: Boolean
        get() = message?.readByReceiver ?: conversationServiceMessage!!.read

    override val timestampSent: Long
        get() = message?.timestampSent
            ?: conversationServiceMessage!!.timestampSent

    override val removableType: MessageRemovableType?
        get() = message?.removableType

    private val hasSignature: Boolean
        get() = message?.content?.any { it.signature != null } ?: false

    override fun isForMe(): Boolean = message?.forMe ?: conversationServiceMessage!!.forMe

    override fun isOutgoing(): Boolean = message?.outgoing ?: conversationServiceMessage!!.outgoing

    override fun toString(): String {
        return "ConversationMessage(message=$message, conversationServiceMessage=$conversationServiceMessage, groupDialog=$groupConversation, isChannel=$isChannel)"
    }
}