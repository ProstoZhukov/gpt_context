package ru.tensor.sbis.communicator.send_message.interactor

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ru.tensor.sbis.attachments.decl.attachment_list.data.CloudObject
import ru.tensor.sbis.attachments.decl.attachment_list.data.DocumentParams
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.attachments.generated.AttachmentFilter
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.communicator.send_message.SendMessagePlugin.sendMessageComponent
import ru.tensor.sbis.message_panel.interactor.attachments.model.AttachmentCatalogParams
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Реализация интерактора для отправки сообщения.
 *
 * @author dv.baranov
 */
class SendMessageInteractorImpl : SendMessageInteractor {

    private val messageController by lazy { MessageController.instance() }
    private val dialogController by lazy { DialogController.instance() }
    private val attachmentController by lazy { Attachment.instance() }
    private val catalogParams = AttachmentCatalogParams(UrlUtils.FILE_SD_OBJECT)

    override suspend fun createDraftDialog(participants: List<UUID>): UUID =
        withContext(Dispatchers.IO) {
            dialogController.createDraftDialog(
                null,
                null,
                null,
                null,
                participants.asArrayList(),
                null
            ).dialog!!.uuid
        }

    override suspend fun updateDraftDialog(conversationUuid: UUID, participants: List<UUID>) {
        withContext(Dispatchers.IO) {
            dialogController.updateDialog(conversationUuid, participants.asArrayList())
        }
    }

    override suspend fun addAttachments(conversationUuid: UUID, attachments: List<Uri>) {
        withContext(Dispatchers.IO) {
            val draftMessage = messageController.clearDraft(conversationUuid)
            messageController.saveDraft(conversationUuid, draftMessage)
            if (attachments.isNotEmpty()) {
                suspendCancellableCoroutine { cont ->
                    val disposable = sendMessageComponent.dependency.addAttachments(
                        params = DocumentParams(
                            catalogParams.blObjectName,
                            draftMessage.id,
                            catalogParams.cloudObjectId?.let { CloudObject(it) }
                        ),
                        uriList = attachments.map { it.toString() },
                        isNeedShowNotification = false
                    ).subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
                    cont.invokeOnCancellation { disposable.dispose() }
                }
            }
        }
    }

    override suspend fun sendMessage(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        messageText: String,
        recipients: List<UUID>
    ): SendMessageResult =
        withContext(Dispatchers.IO) {
            val attachments = getMessageAttachments(conversationUuid, documentUuid)
            messageController.enqueueMessage2(
                conversationUuid,
                null,
                messageText,
                documentUuid,
                recipients.asArrayList(),
                attachments.asArrayList(),
                null,
                null,
                null,
                null
            )
        }

    private suspend fun getMessageAttachments(
        conversationUuid: UUID?,
        documentUuid: UUID?
    ): List<FileInfo> {
        val targetConversationUuid = conversationUuid ?: documentUuid!!
        val draftMessageUuid = getDraftMessageUUID(targetConversationUuid)
        val filter = AttachmentFilter().apply {
            catalogIds = arrayListOf(draftMessageUuid)
        }
        val refreshResult = attachmentController.refresh(filter)
        return refreshResult.result
    }

    override suspend fun clearDraft(conversationUuid: UUID) {
        withContext(Dispatchers.IO) {
            messageController.clearDraft(conversationUuid)
        }
    }

    override suspend fun cancelAddAttachments(conversationUuid: UUID) {
        withContext(Dispatchers.IO) {
            val draft = messageController.getDraft(conversationUuid)
            for (uuid in draft.attachments) {
                suspendCancellableCoroutine { cont ->
                    val disposable = sendMessageComponent.dependency.cancelAdding(uuid)
                        .subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
                    cont.invokeOnCancellation { disposable.dispose() }
                }
            }
        }
    }

    override suspend fun getDraftMessageUUID(conversationUuid: UUID): UUID =
        withContext(Dispatchers.IO) {
            messageController.getDraft(conversationUuid).id
        }
}
