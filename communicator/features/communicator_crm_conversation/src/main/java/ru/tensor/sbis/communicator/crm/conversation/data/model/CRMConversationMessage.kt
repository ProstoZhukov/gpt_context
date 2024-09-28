package ru.tensor.sbis.communicator.crm.conversation.data.model

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.model.MessageViewData
import java.util.UUID

/**
 * Модель сообщения чата CRM.
 * @property message модель информации о сообщении.
 * @property conversationServiceMessage модель информации о сервисном сообщении.
 *
 * @author da.zhukov
 */
class CRMConversationMessage(
    override val message: Message? = null,
    override val conversationServiceMessage: CRMServiceMessage? = null,
    override val viewData: MessageViewData
) : BaseConversationMessage {

    override val uuid: UUID
        get() = message?.uuid ?: conversationServiceMessage!!.uuid

    override var groupConversation: Boolean
        get() = viewData.groupConversation
        set(value) {
            viewData.groupConversation = value
        }

    override val isRedrawable: Boolean
        get() = hasQuote
    override val isReadByMe: Boolean
        get() = message?.readByMe ?: conversationServiceMessage!!.read
    override val readByReceiver: Boolean
        get() = message?.readByReceiver ?: conversationServiceMessage!!.read
    override val timestampSent: Long
        get() = message?.timestampSent ?: conversationServiceMessage!!.timestampSent

    override fun isOutgoing(): Boolean = message?.outgoing ?: conversationServiceMessage!!.outgoing

    override fun isForMe(): Boolean = message?.forMe ?: conversationServiceMessage!!.forMe

    override val itemTypeId: String
        get() = when {
            message != null                    -> UUIDUtils.toString(message.uuid)
            conversationServiceMessage != null -> UUIDUtils.toString(conversationServiceMessage.uuid)
            else                               -> ""
        }

    override val removableType: MessageRemovableType?
        get() = message?.removableType

    override fun areContentsTheSame(other: Any?): Boolean = equals(other)
}