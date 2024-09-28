package ru.tensor.sbis.communicator.declaration.send_message

import android.net.Uri
import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Use-case для отправки сообщения в новый или существующий диалог/канал.
 *
 * @author dv.baranov
 */
interface SendMessageUseCase : Feature {

    /**
     * Отправить сообщение в новый диалог.
     *
     * @param recipients список идентификаторов получателей сообщения.
     * @param messageText текст сообщения.
     * @param attachments список вложений.
     * @return Пара UUID: нового диалога и сообщения.
     */
    suspend fun sendNewDialogMessage(
        recipients: List<UUID>,
        messageText: String,
        attachments: List<Uri> = emptyList()
    ): Pair<UUID?, UUID?>

    /**
     * Отправить сообщение в диалог/канал.
     *
     * @param conversationUuid идентификатор переписки.
     * @param documentUuid идентификатор документа, к которму прикреплен диалог.
     * @param messageText текст сообщения.
     * @param attachments список вложений.
     * @param recipients список идентификаторов получателей сообщения.
     * @return Пара UUID: диалога и сообщения.
     */
    suspend fun sendConversationMessage(
        conversationUuid: UUID?,
        documentUuid: UUID? = null,
        messageText: String,
        attachments: List<Uri> = emptyList(),
        recipients: List<UUID> = emptyList()
    ): Pair<UUID?, UUID?>

    /**
     * Добавить вложения к драфтовому сообщению нового диалога.
     *
     * @param recipients список получателей и участников нового диалога.
     * @param attachments список вложений, которые будут прикреплены к сообщению.
     * @return идентификатор нового драфтового диалога.
     */
    suspend fun addNewDialogAttachments(
        recipients: List<UUID>,
        attachments: List<Uri>
    ): UUID

    /**
     * Добавить вложения к новому драфтовому сообщению в существующей переписке.
     *
     * @param conversationUuid идентификатор переписки.
     * @param attachments список вложений, которые будут прикреплены к сообщению.
     */
    suspend fun addConversationAttachments(
        conversationUuid: UUID,
        attachments: List<Uri>
    )

    /**
     * Обновить драфт диалога.
     */
    suspend fun updateDraftDialog(conversationUuid: UUID, participants: List<UUID>)

    /**
     * Очистить драфтовое сообщение для переписки [conversationUuid].
     */
    suspend fun clearDraft(conversationUuid: UUID)

    /**
     * Отменить прикрепление вложений для драфтового сообщения у диалога с [conversationUuid].
     */
    suspend fun cancelAddAttachments(conversationUuid: UUID)

    /**
     * Получить UUID драфтового сообщения для переписки [conversationUuid].
     */
    @WorkerThread
    suspend fun getDraftMessageUUID(conversationUuid: UUID): UUID
}
