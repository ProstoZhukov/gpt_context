package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

import android.view.HapticFeedbackConstants
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.common.util.header_date.DateViewHolder
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.container.locator.watcher.ItemIdProvider
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.DEFAULT_HIGHLIGHT_DURATION_MS
import ru.tensor.sbis.design.message_view.utils.HighlightDrawable
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Базовая реализация холдера сообщений.
 *
 * @author vv.chekurda
 */
abstract class BaseMessageViewHolder<
    CONVERSATION_MESSAGE : BaseConversationMessage,
    LISTENER : BaseMessageActionsListener<CONVERSATION_MESSAGE>
    > constructor(
    protected val messageView: MessageView,
    protected val actionsListener: LISTENER
) : AbstractViewHolder<CONVERSATION_MESSAGE>(messageView),
    MessageSwipeToQuoteBehavior by messageView,
    ItemIdProvider,
    DateViewHolder {

    protected lateinit var conversationMessage: CONVERSATION_MESSAGE

    private val highlightDrawable = HighlightDrawable(
        messageView.context.getThemeColorInt(R.attr.secondaryActiveBackgroundColor)
    )

    /** Показывается ли меню действий при долгом нажатии. */
    abstract val CONVERSATION_MESSAGE.hasLongClickMenu: Boolean

    init {
        messageView.background = highlightDrawable
        messageView.changeEventListeners {
            clear()
            set(MessageViewEvent.BaseEvent.OnStatusClicked::class) {
                if (conversationMessage.getSyncStatus() == SyncStatus.ERROR) {
                    actionsListener.onMessageErrorStatusClicked(conversationMessage)
                }
            }
            set(MessageViewEvent.BaseEvent.OnLinkClicked::class) {
                actionsListener.onLinkClicked()
            }
            set(MessageViewEvent.QuoteEvent.OnSwipedToQuote::class) {
                actionsListener.onMessageQuotedBySwipe(conversationMessage)
            }
            set(MessageViewEvent.QuoteEvent.OnQuoteClicked::class) {
                actionsListener.onQuoteClicked(it.quotedMessageUuid)
            }
            set(MessageViewEvent.QuoteEvent.OnQuoteLongClicked::class) {
                itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                actionsListener.onQuoteLongClicked(it.quotedMessageUuid)
            }
            set(MessageViewEvent.AttachmentEvent.OnAttachmentClicked::class) {
                actionsListener.onMessageAttachmentClicked(conversationMessage.message!!, it.attachment)
            }
            set(MessageViewEvent.AttachmentEvent.OnAttachmentRetryUploadClicked::class) {
                actionsListener.onRetryUploadClicked(conversationMessage, it.attachmentModel)
            }
            set(MessageViewEvent.AttachmentEvent.OnAttachmentDeleteClicked::class) {
                actionsListener.onDeleteUploadClicked(conversationMessage, it.attachmentModel)
            }
            set(MessageViewEvent.AttachmentEvent.OnAttachmentErrorUploadClicked::class) {
                actionsListener.onErrorUploadClicked(conversationMessage, it.attachmentModel, it.errorMessage)
            }
        }
    }

    override fun bind(dataModel: CONVERSATION_MESSAGE) {
        super.bind(dataModel)
        conversationMessage = dataModel
        updateClickListeners()
        messageView.viewData = dataModel.viewData
    }

    override fun recycle() {
        super.recycle()
        messageView.recycleViews()
    }

    override fun setFormattedDateTime(formattedDateTime: FormattedDateTime) {
        messageView.setFormattedDateTime(formattedDateTime)
    }

    /**
     * Обновить view с состоянием доставки [sendingState].
     */
    fun updateSendingState(sendingState: SendingState) {
        messageView.updateSendingState(sendingState)
    }

    /**
     * Подсветить продолжительностью [durationMs].
     * @param durationMs продолжительность статичной подсветки.
     */
    fun highlight(durationMs: Long = DEFAULT_HIGHLIGHT_DURATION_MS) {
        highlightDrawable.highlight(durationMs)
    }

    override fun getId(): String = UUIDUtils.toString(conversationMessage.uuid)

    private fun updateClickListeners() {
        messageView.changeEventListeners {
            val longClickEvent = MessageViewEvent.BaseEvent.OnLongClicked::class
            if (conversationMessage.hasLongClickMenu) {
                set(longClickEvent) { actionsListener.onMessageSelected(conversationMessage) }
            } else {
                remove(longClickEvent)
            }
            set(MessageViewEvent.BaseEvent.OnClicked::class) { actionsListener.onMessageClicked(conversationMessage) }
        }
    }
}
