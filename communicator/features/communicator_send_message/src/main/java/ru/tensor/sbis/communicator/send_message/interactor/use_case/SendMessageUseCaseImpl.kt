package ru.tensor.sbis.communicator.send_message.interactor.use_case

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.send_message.interactor.SendMessageInteractorImpl
import java.util.UUID

/**
 * Реализация use-case для отправки сообщения в новый или существующий диалог/канал.
 *
 * @author dv.baranov
 */
internal class SendMessageUseCaseImpl : SendMessageUseCase {

    private val interactor = SendMessageInteractorImpl()

    override suspend fun sendNewDialogMessage(
        recipients: List<UUID>,
        messageText: String,
        attachments: List<Uri>
    ): Pair<UUID?, UUID?> =
        withContext(Dispatchers.IO) {
            val conversationUuid = interactor.createDraftDialog(recipients)
            return@withContext sendConversationMessage(
                conversationUuid,
                null,
                messageText,
                attachments,
                recipients
            )
        }

    override suspend fun sendConversationMessage(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        messageText: String,
        attachments: List<Uri>,
        recipients: List<UUID>
    ): Pair<UUID?, UUID?> =
        withContext(Dispatchers.IO) {
            if (attachments.isNotEmpty()) {
                val targetConversationUuid = conversationUuid ?: documentUuid!!
                interactor.addAttachments(
                    targetConversationUuid,
                    attachments
                )
            }
            val sendMessageResult = interactor.sendMessage(conversationUuid, documentUuid, messageText, recipients)
            return@withContext Pair(sendMessageResult.dialogUuid, sendMessageResult.messageUuid)
        }

    override suspend fun addNewDialogAttachments(
        recipients: List<UUID>,
        attachments: List<Uri>
    ): UUID =
        withContext(Dispatchers.IO) {
            val conversationUuid = interactor.createDraftDialog(recipients)
            interactor.addAttachments(conversationUuid, attachments)
            conversationUuid
        }

    override suspend fun addConversationAttachments(
        conversationUuid: UUID,
        attachments: List<Uri>
    ) {
        withContext(Dispatchers.IO) {
            interactor.addAttachments(conversationUuid, attachments)
        }
    }

    override suspend fun updateDraftDialog(conversationUuid: UUID, participants: List<UUID>) {
        withContext(Dispatchers.IO) {
            interactor.updateDraftDialog(conversationUuid, participants)
        }
    }

    override suspend fun clearDraft(conversationUuid: UUID) {
        withContext(Dispatchers.IO) {
            interactor.clearDraft(conversationUuid)
        }
    }

    override suspend fun cancelAddAttachments(conversationUuid: UUID) {
        withContext(Dispatchers.IO) {
            interactor.cancelAddAttachments(conversationUuid)
        }
    }

    override suspend fun getDraftMessageUUID(conversationUuid: UUID): UUID =
        withContext(Dispatchers.IO) {
            interactor.getDraftMessageUUID(conversationUuid)
        }
}
