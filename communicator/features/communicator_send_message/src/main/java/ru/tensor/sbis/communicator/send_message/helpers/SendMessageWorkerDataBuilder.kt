package ru.tensor.sbis.communicator.send_message.helpers

import android.net.Uri
import androidx.work.Data
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import timber.log.Timber
import java.util.*

/**
 * Получить входные данные для работы внутри [SendMessageWorker].
 */
internal fun buildInputData(
    conversationUUID: UUID? = null,
    documentUUID: UUID? = null,
    recipients: List<UUID> = emptyList(),
    messageText: String = StringUtils.EMPTY,
    attachments: List<Uri> = emptyList()
): Data = try {
    Data.Builder()
        .putString(CONVERSATION_UUID, conversationUUID?.toString())
        .putString(DOCUMENT_UUID, documentUUID?.toString())
        .putStringArray(RECIPIENTS, recipients.map { it.toString() }.toTypedArray())
        .putString(MESSAGE_TEXT, messageText)
        .putStringArray(ATTACHMENTS, attachments.map { it.toString() }.toTypedArray())
        .build()
} catch (ex: IllegalStateException) {
    // Попытка отловить редкую ошибку (https://online.sbis.ru/opendoc.html?guid=096e91e3-2eea-428b-84de-df41d80dc5b9&client=3)
    Timber.e(
        ex,
        "Количество получателей: %s, количество вложений: %s, длина текста: %s. Сам текст: %s",
        recipients.size,
        attachments.size,
        messageText.length,
        messageText
    )
    Data.Builder()
        .putString(CONVERSATION_UUID, conversationUUID?.toString())
        .putString(DOCUMENT_UUID, documentUUID?.toString())
        .putStringArray(
            RECIPIENTS,
            recipients.take(MAX_RECIPIENTS_COUNT).map { it.toString() }.toTypedArray()
        )
        .putString(MESSAGE_TEXT, StringUtils.EMPTY)
        .putStringArray(
            ATTACHMENTS,
            attachments.take(MAX_ATTACHMENTS_COUNT).map { it.toString() }.toTypedArray()
        )
        .build()
}

/**
 * Получить данные для сохранения uuid отправляемого сообщения.
 * Использовать только для [SendMessageWorker].
 */
internal fun buildMessageUUIDData(messageUUID: UUID): Data = try {
    Data.Builder()
        .putString(DATA_MESSAGE_UUID_KEY, UUIDUtils.toString(messageUUID))
        .build()
} catch (ex: IllegalStateException) {
    Timber.e(ex)
    Data.EMPTY
}

/**
 * Получить данные для сохранения прогресса работы после получения "sent" события от контроллера или
 * имитации получения этого события (для случая перезапуска работы системой).
 * Использовать только для [SendMessageWorker].
 */
internal fun buildMessageSentData(messageUUID: UUID): Data = try {
    Data.Builder()
        .putString(DATA_MESSAGE_UUID_KEY, UUIDUtils.toString(messageUUID))
        .putBoolean(DATA_MESSAGE_SENT_KEY, true)
        .build()
} catch (ex: IllegalStateException) {
    Timber.e(ex)
    Data.EMPTY
}

private const val MAX_RECIPIENTS_COUNT = 200
private const val MAX_ATTACHMENTS_COUNT = 25

internal const val CONVERSATION_UUID = "convUUID"
internal const val DOCUMENT_UUID = "docUUID"
internal const val RECIPIENTS = "recipients"
internal const val MESSAGE_TEXT = "messageText"
internal const val ATTACHMENTS = "attachments"

internal const val DATA_MESSAGE_UUID_KEY = "DATA_MESSAGE_UUID_KEY"
internal const val DATA_MESSAGE_SENT_KEY = "DATA_MESSAGE_SENT_KEY"
