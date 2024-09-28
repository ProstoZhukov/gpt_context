package ru.tensor.sbis.communicator.communicator_files.data

import android.content.Context
import android.util.Size
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.attachments.generated.AttachmentsUtils
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.attachments.generated.ImageParams
import ru.tensor.sbis.attachments.generated.OperationState
import ru.tensor.sbis.attachments.models.AttachmentType
import ru.tensor.sbis.attachments.models.id.AttachmentId
import ru.tensor.sbis.attachments.models.property.AttachmentFlag
import ru.tensor.sbis.attachments.models.property.AttachmentPreviewMap
import ru.tensor.sbis.attachments.models.property.MAX_PROGRESS
import ru.tensor.sbis.attachments.models.property.UploadState
import ru.tensor.sbis.attachments.ui.mapper.AttachmentPreviewSourcesFactory
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.attachments.decl.canonicalLocalUri
import ru.tensor.sbis.attachments.decl.fullName
import ru.tensor.sbis.attachments.decl.isAccessDenied
import ru.tensor.sbis.attachments.decl.isDeleted
import ru.tensor.sbis.attachments.decl.isEncrypted
import ru.tensor.sbis.attachments.decl.isFolder
import ru.tensor.sbis.attachments.decl.isMalware
import ru.tensor.sbis.attachments.decl.isSignedByMe
import ru.tensor.sbis.attachments.decl.isSignedByOthers
import ru.tensor.sbis.attachments.decl.isSignedPreviousRedaction
import ru.tensor.sbis.attachments.decl.oldAttachmentId
import ru.tensor.sbis.attachments.models.AttachmentUploadModel
import ru.tensor.sbis.attachments.models.property.AttachmentFlagsModel
import ru.tensor.sbis.attachments.models.property.AttachmentForeignSignsCountModel
import ru.tensor.sbis.attachments.models.property.AttachmentPreviewMapModel
import ru.tensor.sbis.attachments.models.property.AttachmentPreviewSizeModel
import ru.tensor.sbis.attachments.ui.viewmodel.base.AttachmentVM
import ru.tensor.sbis.communicator.communicator_files.utils.calculateViewWidthForFullScreen
import ru.tensor.sbis.communicator.generated.AttachmentOrigin
import java.util.ArrayList
import java.util.EnumSet
import java.util.UUID


/**
 * Интерфейс модели для вложения по переписке.
 *
 * @author da.zhukov
 */
interface CommunicatorFileAttachment :
    AttachmentUploadModel,
    AttachmentFlagsModel,
    AttachmentForeignSignsCountModel,
    AttachmentPreviewMapModel,
    AttachmentPreviewSizeModel

/**
 * Модель вложения по переписке.
 */
@Parcelize
internal class CommunicatorFileAttachmentImpl(
    override val id: AttachmentId,
    override val type: AttachmentType,
    override val name: String,
    override val fileName: String,
    override val flags: Set<AttachmentFlag>,
    override val foreignSignsCount: Int,
    override val localUri: String?,
    override val previewUris: AttachmentPreviewMap,
    override val size: Long,
    override val previewWidth: Int,
    override val previewHeight: Int,
    override val state: UploadState
) : CommunicatorFileAttachment

/** @SelfDocumented **/
internal fun FileInfoViewModel.flags(ignoredFlags: Set<AttachmentFlag> = emptySet()): Set<AttachmentFlag> =
    EnumSet.noneOf(AttachmentFlag::class.java).apply {
        if (isAccessDenied) add(AttachmentFlag.ACCESS_DENIED)
        if (isMalware) add(AttachmentFlag.MALWARE)
        if (isEncrypted) add(AttachmentFlag.ENCRYPTED)
        if (isSignedByOthers) add(AttachmentFlag.SIGNED_BY_OTHERS)
        if (isSignedByMe) add(AttachmentFlag.SIGNED_BY_ME)
        if (isSignedPreviousRedaction) add(AttachmentFlag.SIGNED_PREVIOUS_REDACTION)
        if (isDeleted) add(AttachmentFlag.DELETED)
        removeAll(ignoredFlags)
    }

private var viewWidth: Int = 0

/** @SelfDocumented **/
internal fun FileInfoViewModel.asCommunicatorFileAttachment(context: Context): CommunicatorFileAttachment {
    val previewSize = if (viewWidth == 0) {
        viewWidth = context.applicationContext.calculateViewWidthForFullScreen()
        viewWidth
    } else viewWidth
    return CommunicatorFileAttachmentImpl(
        id = oldAttachmentId,
        type = if (isFolder) FileUtil.FileType.FOLDER else FileUtil.detectFileType(fullName),
        name = titleWithExtension,
        fileName = fullName,
        flags = flags(),
        foreignSignsCount = signsCount ?: 0,
        localUri = canonicalLocalUri,
        previewUris = if (isFolder) emptyMap() else getPreviewUrls(context),
        size = this@asCommunicatorFileAttachment.size ?: 0,
        previewWidth = previewSize,
        previewHeight = previewSize,
        state = contentOperation?.let { content ->
            if (content.status == OperationState.FAILURE_FATAL) {
                UploadState.Error(
                    content.error?.errorMessage ?: context.getString(R.string.common_unknown_error)
                )
            } else {
                UploadState.InProcessing(content.progress ?: 0)
            }
        } ?: UploadState.InProcessing(MAX_PROGRESS)
    )
}

/** @SelfDocumented **/
internal fun FileInfoViewModel.getPreviewUrls(context: Context): AttachmentPreviewMap {
    val defaultSizes = AttachmentPreviewSourcesFactory.createDefaultSizes(context)
    val previewHeightsAndWidths = defaultSizes.map(Size::getHeight)
    val previewSizes: ArrayList<ImageParams> =
        previewHeightsAndWidths.mapTo(arrayListOf()) { ImageParams(width = it, height = it) }
    return previewParams?.let { previewParams ->
        AttachmentsUtils.getPreviewUrls(params = previewParams, extParams = null, imgParams = previewSizes)
    } ?: emptyMap()
}

private val FileInfoViewModel.titleWithExtension: String
    get() = fileExtension?.let { ext -> "$title.$ext" } ?: title

/**
 * Данные для представления файла.
 *
 * @param attachments Список объектов типа [AttachmentVM], представляющих вложения.
 * @param actionData Список объектов типа [CommunicatorFileActionData], содержащих данные для действий, связанных с файлами.
 */
internal data class CommunicatorFileData(
    val attachments: List<AttachmentVM>,
    val actionData: List<CommunicatorFileActionData>
)

/**
 * Данные для действий, связанных с файлом.
 *
 * @param fileId Уникальный идентификатор файла.
 * @param messageId Уникальный идентификатор сообщения, с которым связан файл (может быть `null`).
 * @param attachmentLink Ссылка на вложение.
 * @param attachmentOrigin Происхождение вложения, определяющее контекст его прикрепления ([AttachmentOrigin]).
 * @param fileInfoViewModel Модель представления информации о файле ([FileInfoViewModel]), содержащая данные для отображения в просмотрщике.
 */
internal data class CommunicatorFileActionData(
    val fileId: UUID,
    val messageId: UUID?,
    val attachmentLink: String,
    val attachmentOrigin: AttachmentOrigin,
    val fileInfoViewModel: FileInfoViewModel
)