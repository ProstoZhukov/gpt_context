package ru.tensor.sbis.communicator.sbis_conversation.preview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageActionsListener
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import java.util.UUID

/**
 * Реализация слушателя [MessageActionsListener]
 * для возможности подмены в режиме "предпросмотра" переписки.
 *
 * @autor da.zhukov
 */
internal class ConversationPreviewActionListener(
    private val fragment: Fragment,
    private val messageActionsListener: MessageActionsListener
) : MessageActionsListener {

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        messageActionsListener.onQuoteClicked(quotedMessageUuid)
    }

    override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) {
        setPreviewModeResult()
    }

    override fun onMessageClicked(conversationMessage: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onMessageSelected(conversationMessage: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onServiceMessageClicked(position: Int) {
        setPreviewModeResult()
    }

    override fun onAcceptSigningButtonClicked(data: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onRejectSigningButtonClicked(data: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View) {
        setPreviewModeResult()
    }

    override fun onDenyAccessButtonClicked(data: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean): Boolean {
        setPreviewModeResult()
        return false
    }

    override fun onMediaPlaybackError(error: Throwable) {
        setPreviewModeResult()
    }

    override fun onThreadMessageClicked(data: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onThreadCreationServiceClicked(data: ConversationMessage) {
        setPreviewModeResult()
    }

    override fun onPhotoClicked(senderUuid: UUID) {
        setPreviewModeResult()
    }

    override fun onSenderNameClicked(senderUuid: UUID) {
        setPreviewModeResult()
    }

    override fun onLinkClicked() {
        setPreviewModeResult()
    }

    override fun onPhoneNumberClicked(phoneNumber: String) {
        setPreviewModeResult()
    }

    override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) {
        setPreviewModeResult()
    }

    private fun setPreviewModeResult() {
        fragment.parentFragmentManager.setFragmentResult(CONVERSATION_CLICKED, Bundle())
    }
}

internal const val CONVERSATION_CLICKED = "CONVERSATION_CLICKED"