package ru.tensor.sbis.communicator.declaration.send_message

import android.net.Uri
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Интерфейс механизма для отправки сообщений в фоне при помощи WorkManager.
 *
 * @author dv.baranov
 */
interface SendMessageManager {

    /**
     * Отправить сообщение в диалог/канал в фоне.
     *
     * @param recipients список идентификаторов получателей сообщения.
     * @param messageText текст сообщения.
     * @param attachments список вложений.
     * @return true, когда работа по отправке сообщения выполнена.
     */
    fun sendNewDialogMessage(
        recipients: List<UUID>,
        messageText: String,
        attachments: List<Uri> = emptyList()
    )

    /**
     * Отправить сообщение в диалог/канал в фоне.
     *
     * @param conversationUUID идентификатор переписки.
     * @param documentUUID идентификатор документа, к которму прикреплен диалог.
     * @param messageText текст сообщения.
     * @param attachments список вложений.
     * @param recipients список идентификаторов получателей сообщения.
     * @return true, когда работа по отправке сообщения выполнена.
     */
    fun sendConversationMessage(
        conversationUUID: UUID?,
        documentUUID: UUID? = null,
        messageText: String,
        attachments: List<Uri> = emptyList(),
        recipients: List<UUID> = emptyList()
    )

    /**
     * Отменить все текущие отправки сообщений.
     */
    fun cancelAllSendingMessage()

    /**
     * Отменить отправку сообщения и скрыть пуш, если сообщение удалили из реестра сообщений напрямую.
     */
    fun cancelSendMessageByMessageActionDelete(deletedMessageUUID: UUID)

    fun interface Provider : Feature {

        fun getSendMessageManager(): SendMessageManager
    }
}
