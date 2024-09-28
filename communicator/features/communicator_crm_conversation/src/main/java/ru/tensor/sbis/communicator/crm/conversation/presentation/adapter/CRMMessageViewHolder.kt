package ru.tensor.sbis.communicator.crm.conversation.presentation.adapter

import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageViewHolder
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.MessageType.INCOME_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.INCOME_RATE_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.INCOME_VIDEO_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.OUTCOME_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.OUTCOME_RATE_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.OUTCOME_VIDEO_MESSAGE
import ru.tensor.sbis.design.message_view.ui.MessageView

/**
 * Холдер кнопок приветствий для случая их отображения вместе с сообщениями.
 * (Частью списка, а не отдельной вьюхой)
 *
 * @author dv.baranov
 */
internal class CRMMessageViewHolder(
    messageView: MessageView,
    actionsListener: CRMMessageActionsListener,
    isOperator: Boolean
) : BaseMessageViewHolder<CRMConversationMessage, CRMMessageActionsListener>(
    messageView,
    actionsListener
) {
    override val CRMConversationMessage.hasLongClickMenu: Boolean
        get() = when (viewData.type) {
            INCOME_MESSAGE,
            OUTCOME_MESSAGE,
            INCOME_VIDEO_MESSAGE,
            OUTCOME_VIDEO_MESSAGE,
            INCOME_RATE_MESSAGE,
            OUTCOME_RATE_MESSAGE -> true
            else -> false
        }
    init {
        messageView.changeEventListeners {
            if (!isOperator) {
                set(MessageViewEvent.AuthorEvent.OnAuthorAvatarClicked::class) {
                    // Необходимо для отключения открытия карточки персоны.
                    // Из-за того что в PersonViewControllerImpl.initDefaultClickListener() по умолчанию задаётся слушатель, который открывает карточку.
                }
            }
            set(MessageViewEvent.CRMEvent.OnGreetingClicked::class) {
                actionsListener.onGreetingClicked(it.title)
            }
            set(MessageViewEvent.CRMEvent.OnRateRequestButtonClicked::class) {
                actionsListener.onRateRequestButtonClicked(conversationMessage.uuid, it.rateType, it.disableComment)
            }
            set(MessageViewEvent.CRMEvent.OnChatBotButtonClicked::class) {
                actionsListener.onChatBotButtonClicked(conversationMessage.uuid, it.title)
            }
            set(MessageViewEvent.CRMEvent.ScrollToBottom::class) {
                actionsListener.scrollToBotButtons()
            }
        }
    }
}
