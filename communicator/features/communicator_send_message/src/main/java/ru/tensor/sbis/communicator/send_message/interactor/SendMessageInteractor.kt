package ru.tensor.sbis.communicator.send_message.interactor

import android.net.Uri
import androidx.annotation.WorkerThread
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.communicator.generated.SendMessageResult
import java.util.*

/**
 * Интерфейс интерактора для отправки сообщения.
 *
 * @author dv.baranov
 */
interface SendMessageInteractor {

    /**
     * Создать драфтовый диалог с участниками [participants].
     *
     * @return идентификатор диалога.
     */
    suspend fun createDraftDialog(participants: List<UUID>): UUID

    /**
     * Обновить драфт диалога.
     */
    suspend fun updateDraftDialog(conversationUuid: UUID, participants: List<UUID>)

    /**
     * Добавить вложения [attachments] к новому сообщению в переписке [conversationUuid].
     */
    suspend fun addAttachments(conversationUuid: UUID, attachments: List<Uri>)

    /**
     * Отправить сообщение.
     *
     * @param conversationUuid идентификатор переписки.
     * @param documentUuid идентификатор документа.
     * @param messageText текст сообщения.
     * @param recipients список идентификаторов получетелей сообщения.
     * @return результат отправки сообщения.
     */
    suspend fun sendMessage(
        conversationUuid: UUID?,
        documentUuid: UUID? = null,
        messageText: String,
        recipients: List<UUID>
    ): SendMessageResult

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
