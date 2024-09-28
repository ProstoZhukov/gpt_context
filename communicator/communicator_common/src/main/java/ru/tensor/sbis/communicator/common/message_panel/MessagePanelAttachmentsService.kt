package ru.tensor.sbis.communicator.common.message_panel

import kotlinx.coroutines.*
import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.action.DeleteAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.attachment_list.data.CloudObject
import ru.tensor.sbis.attachments.decl.attachment_list.data.DocumentParams
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.attachments.generated.AttachmentFilter
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsService
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsServiceEvents
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.message_panel.interactor.attachments.model.AttachmentCatalogParams
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * @author vv.chekurda
 */
internal class MessagePanelAttachmentsService(
    private val attachmentController: DependencyProvider<Attachment>,
    private val addAttachmentsUseCase: AddAttachmentsUseCase,
    private val deleteAttachmentsUseCase: DeleteAttachmentsUseCase,
    uploadEventsDelegate: AttachmentsServiceEvents,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AttachmentsService, AttachmentsServiceEvents by uploadEventsDelegate {

    private val catalogParams = AttachmentCatalogParams(UrlUtils.FILE_SD_OBJECT)

    override suspend fun setAttachmentListRefreshCallback(
        refreshCallback: DataRefreshedAttachmentCallback
    ) = withContext(dispatcher) {
        attachmentController.get().dataRefreshed().subscribe(refreshCallback)
    }

    override suspend fun addAttachments(
        messageUuid: UUID,
        uriList: List<String>,
        diskDocumentParamsList: List<DiskDocumentParams>
    ) = withContext(dispatcher) {
        suspendCancellableCoroutine<Unit> { cont ->
            val disposable = addAttachmentsUseCase.addAttachments(
                DocumentParams(
                    catalogParams.blObjectName,
                    messageUuid,
                    catalogParams.cloudObjectId?.let { CloudObject(it) }
                ),
                uriList,
                diskDocumentParamsList
            ).subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
            cont.invokeOnCancellation { disposable.dispose() }
        }
    }

    override suspend fun deleteAttachment(attachment: FileInfo) = withContext(dispatcher) {
        listOf(
            async {
                val localUuid = attachment.attachId ?: return@async
                cancelAdding(localUuid)
            },
            async {
                /*
                Если файл являтся ссылкой на документ, то его не нужно удалять с облака,
                т.к удалится оригинал
                 */
                if (attachment.isLink) {
                    deleteRef(attachment.id)
                } else {
                    deleteFile(attachment.id)
                }
            }
        ).awaitAll()
        Unit
    }

    override suspend fun loadAttachments(messageUuid: UUID) = withContext(dispatcher) {
        val filter = AttachmentFilter().apply { catalogIds = arrayListOf(messageUuid) }
        attachmentController.get().list(filter).result
    }

    private suspend fun cancelAdding(localUuid: UUID) = suspendCancellableCoroutine<Unit> { cont ->
        val disposable = addAttachmentsUseCase.cancelAdding(localUuid)
            .subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
        cont.invokeOnCancellation { disposable.dispose() }
    }

    private suspend fun deleteRef(id: Long) = suspendCancellableCoroutine<Unit> { cont ->
        val disposable = deleteAttachmentsUseCase.deleteLocal(listOf(id))
            .subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
        cont.invokeOnCancellation { disposable.dispose() }
    }

    private suspend fun deleteFile(id: Long) = suspendCancellableCoroutine<Unit> { cont ->
        val disposable = deleteAttachmentsUseCase.delete(catalogParams.blObjectName, listOf(id))
            .subscribe({ cont.resume(Unit) }, { ex -> cont.resumeWithException(ex) })
        cont.invokeOnCancellation { disposable.dispose() }
    }
}