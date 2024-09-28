package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener

/**
 * Базовый интерфейс действий по кликам на различные контентные области сообщения.
 *
 * @author vv.chekurda
 */
interface BaseMessageActionsListener<MESSAGE : BaseConversationMessage>
    : MessageSelectionListener<MESSAGE>,
    MessageClickListener<MESSAGE>,
    MessageAttachmentUploadActionsHandler<MESSAGE>,
    LinkClickListener,
    QuoteClickListener {

    /**@SelfDocumented */
    fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel)

    /**@SelfDocumented */
    fun onMessageQuotedBySwipe(message: MESSAGE) = Unit

    /**@SelfDocumented */
    fun onMessageErrorStatusClicked(conversationMessage: MESSAGE) = Unit
}