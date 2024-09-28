package ru.tensor.sbis.communicator.send_message.manager

import android.content.Context
import android.net.Uri
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import java.util.UUID

/**
 * Реализация механизма для отправки сообщений в фоне при помощи WorkManager.
 *
 * @author dv.baranov
 */
internal class SendMessageManagerImpl(
    private val context: Context
) : SendMessageManager {

    override fun sendNewDialogMessage(
        recipients: List<UUID>,
        messageText: String,
        attachments: List<Uri>
    ) = SendMessageWorker.sendMessage(
        context = context,
        recipients = recipients,
        messageText = messageText,
        attachments = attachments
    )

    override fun sendConversationMessage(
        conversationUUID: UUID?,
        documentUUID: UUID?,
        messageText: String,
        attachments: List<Uri>,
        recipients: List<UUID>
    ) = SendMessageWorker.sendMessage(
        context = context,
        conversationUUID = conversationUUID,
        documentUUID = documentUUID,
        recipients = recipients,
        messageText = messageText,
        attachments = attachments
    )

    override fun cancelAllSendingMessage() {
        SendMessageWorker.cancelAllMessagesSending(context)
    }

    override fun cancelSendMessageByMessageActionDelete(deletedMessageUUID: UUID) {
        SendMessageWorker.cancelMessageSendingByUUID(context, deletedMessageUUID)
    }
}
