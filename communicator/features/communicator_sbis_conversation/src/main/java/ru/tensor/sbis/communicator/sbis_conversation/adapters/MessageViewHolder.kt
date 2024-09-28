package ru.tensor.sbis.communicator.sbis_conversation.adapters

import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageViewHolder
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.model.MessageType.INCOME_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.INCOME_VIDEO_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.OUTCOME_MESSAGE
import ru.tensor.sbis.design.message_view.model.MessageType.OUTCOME_VIDEO_MESSAGE
import ru.tensor.sbis.design.message_view.ui.MessageView

/**
 * ViewHolder входящего/исходящего сообщения.
 *
 * @author vv.chekurda
 */
internal class MessageViewHolder(
    messageView: MessageView,
    actionsListener: MessageActionsListener,
    private val messageCanBeSwiped: Boolean = true
) : BaseMessageViewHolder<ConversationMessage, MessageActionsListener>(
    messageView,
    actionsListener
) {
    override val ConversationMessage.hasLongClickMenu: Boolean
        get() = when (viewData.type) {
            INCOME_MESSAGE,
            OUTCOME_MESSAGE,
            INCOME_VIDEO_MESSAGE,
            OUTCOME_VIDEO_MESSAGE -> true
            else -> false
        }

    init {
        messageView.changeEventListeners {
            set(MessageViewEvent.AuthorEvent.OnAuthorNameClicked::class) {
                actionsListener.onSenderNameClicked(it.model.personData.uuid ?: return@set)
            }
            set(MessageViewEvent.AuthorEvent.OnAuthorAvatarClicked::class) {
                actionsListener.onPhotoClicked(it.model.personData.uuid ?: return@set)
            }
            set(MessageViewEvent.ButtonsEvent.OnSigningButtonClicked::class) {
                if (it.isAccepted) {
                    actionsListener.onAcceptSigningButtonClicked(conversationMessage)
                } else {
                    actionsListener.onRejectSigningButtonClicked(conversationMessage)
                }
            }
            set(MessageViewEvent.ButtonsEvent.OnGrantAccessButtonClicked::class) {
                if (it.isAccepted) {
                    actionsListener.onGrantAccessButtonClicked(conversationMessage, it.button!!)
                } else {
                    actionsListener.onDenyAccessButtonClicked(conversationMessage)
                }
            }
            set(MessageViewEvent.PhoneNumberEvent.OnPhoneNumberClicked::class) {
                actionsListener.onPhoneNumberClicked(it.phoneNumber)
            }
            set(MessageViewEvent.PhoneNumberEvent.OnPhoneNumberLongClicked::class) {
                actionsListener.onPhoneNumberLongClicked(it.phoneNumber)
            }
            set(MessageViewEvent.MediaEvent.OnMediaRecognizedTextClicked::class) {
                actionsListener.onMediaMessageExpandClicked(conversationMessage, it.isExpanded)
            }
            set(MessageViewEvent.MediaEvent.OnMediaPlaybackError::class) {
                actionsListener.onMediaPlaybackError(it.error)
            }
            set(MessageViewEvent.ServiceEvent.OnServiceMessageClicked::class) {
                actionsListener.onServiceMessageClicked(absoluteAdapterPosition)
            }
            set(MessageViewEvent.ThreadEvent.OnThreadMessageClicked::class) {
                actionsListener.onThreadMessageClicked(conversationMessage)
            }
            set(MessageViewEvent.ThreadEvent.OnThreadCreationServiceClicked::class) {
                actionsListener.onThreadCreationServiceClicked(conversationMessage)
            }
        }
    }

    override fun bind(dataModel: ConversationMessage) {
        super.bind(dataModel)
        if (!messageCanBeSwiped) {
            messageView.canBeQuoted = false
        }
    }

    /**
     * Сменить прогресс (крутилку) отклонения подписи / доступа к файлу.
     */
    fun changeRejectProgress(show: Boolean) {
        messageView.changeRejectProgress(show)
    }

    /**
     * Сменить прогресс (крутилку) предоставления доступа к файлу.
     */
    fun changeAcceptProgress(show: Boolean) {
        messageView.changeAcceptProgress(show)
    }
}
