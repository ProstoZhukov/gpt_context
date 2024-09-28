package ru.tensor.sbis.message_panel.interactor.attachments

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.action.DeleteAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.attachment_list.data.CloudObject
import ru.tensor.sbis.attachments.decl.attachment_list.data.DocumentParams
import ru.tensor.sbis.attachments.generated.Attachment
import ru.tensor.sbis.attachments.generated.AttachmentController
import ru.tensor.sbis.attachments.generated.AttachmentFilter
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.message_panel.interactor.attachments.model.AttachmentCatalogParams
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*

/**
 * @author vv.chekurda
 */
internal class DefaultMessagePanelAttachmentsInteractor(
    private val attachmentController: DependencyProvider<Attachment>,
    private val singleAttachmentController: DependencyProvider<AttachmentController>,
    private val addAttachmentsUseCase: AddAttachmentsUseCase,
    private val deleteAttachmentsUseCase: DeleteAttachmentsUseCase,
    private val catalogParams: AttachmentCatalogParams
) : BaseInteractor(), MessagePanelAttachmentsInteractor {

    override fun setAttachmentListRefreshCallback(refreshCallback: DataRefreshedAttachmentCallback): Observable<Subscription> =
        Observable.fromCallable {
            attachmentController.get().dataRefreshed().subscribe(refreshCallback)
        }.compose(getObservableBackgroundSchedulers())

    override fun addAttachments(
        messageUuid: UUID,
        uriList: List<String>,
        diskDocumentParamsList: List<DiskDocumentParams>,
        compressImages: Boolean
    ): Completable =
        addAttachmentsUseCase.addAttachments(
            params = DocumentParams(
                catalogParams.blObjectName,
                messageUuid,
                catalogParams.cloudObjectId?.let { CloudObject(it) }
            ),
            uriList = uriList,
            diskDocumentParamsList = diskDocumentParamsList,
            isNeedShowNotification = false,
            compressImages = compressImages
        )

    override fun deleteAttachment(attachment: FileInfo): Completable =
        deleteAttachment(attachment, byTransaction = false)

    override fun deleteAttachmentByTransaction(attachment: FileInfo): Completable =
        deleteAttachment(attachment, byTransaction = true)

    private fun deleteAttachment(attachment: FileInfo, byTransaction: Boolean): Completable {
        val localUuid: UUID? = attachment.attachId
        return Completable.concatArray(
            if (localUuid != null) {
                addAttachmentsUseCase.cancelAdding(localUuid).onErrorComplete(errorHandlingPredicate)
            } else {
                Completable.complete()
            },
            // Если файл являтся ссылкой на документ, то его не нужно удалять с облака, т.к удалится оригинал
            if (byTransaction || localUuid != null || attachment.isLink) {
                deleteAttachmentsUseCase.deleteLocal(listOf(attachment.id)).onErrorComplete(errorHandlingPredicate)
            } else {
                deleteAttachmentsUseCase.delete(catalogParams.blObjectName, listOf(attachment.id))
                    .onErrorComplete(errorHandlingPredicate)
            }
        )
    }

    override fun loadAttachments(messageUuid: UUID): Single<List<FileInfo>> =
        loadAttachments(catalogId = messageUuid, byTransaction = false)

    override fun loadAttachmentsByTransaction(messageUuid: UUID): Single<List<FileInfo>> =
        loadAttachments(catalogId = messageUuid, byTransaction = true)

    private fun loadAttachments(catalogId: UUID, byTransaction: Boolean): Single<List<FileInfo>> =
        Single.fromCallable {
            val filter = AttachmentFilter().apply {
                catalogIds = arrayListOf(catalogId)
                underTransaction = byTransaction
            }
            @Suppress("USELESS_CAST")
            attachmentController.get().refresh(filter).result as List<FileInfo>
        }.subscribeOn(Schedulers.io())

    override fun restartUploadAttachment(attachment: FileInfo): Completable =
        Completable.fromCallable {
            singleAttachmentController.get().restartBind(attachment.id)
        }.subscribeOn(Schedulers.io())

    private val errorHandlingPredicate: Predicate<Throwable> by lazy {
        Predicate<Throwable> { throwable ->
            Timber.e(throwable)
            true
        }
    }
}
