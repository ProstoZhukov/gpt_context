package ru.tensor.sbis.design.message_view.utils

import android.content.Context
import android.util.Size
import kotlinx.parcelize.Parcelize
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
import ru.tensor.sbis.attachments.ui.mapper.AttachmentPreviewSourcesFactory.Companion.createDefaultSizes
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import java.util.*

/**
 * Расширения для получения модели [MessageAttachment] из [FileInfoViewModel].
 *
 * @author dv.baranov
 */

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

/** @SelfDocumented **/
fun FileInfoViewModel.asMessageAttachment(context: Context): MessageAttachment = MessageAttachmentImpl(
    id = oldAttachmentId,
    type = if (isFolder) FileUtil.FileType.FOLDER else FileUtil.detectFileType(fullName),
    name = titleWithExtension,
    fileName = fullName,
    flags = flags(),
    foreignSignsCount = signsCount ?: 0,
    localUri = canonicalLocalUri,
    previewUris = if (isFolder) emptyMap() else getPreviewUrls(context),
    size = this@asMessageAttachment.size ?: 0,
    previewWidth = imageParams?.width ?: 0,
    previewHeight = imageParams?.height ?: 0,
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

/**
 * Модель вложения.
 */
@Parcelize
private class MessageAttachmentImpl(
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
) : MessageAttachment

/** @SelfDocumented **/
internal fun FileInfoViewModel.getPreviewUrls(context: Context): AttachmentPreviewMap {
    val defaultSizes = createDefaultSizes(context)
    val previewHeights = defaultSizes.map(Size::getHeight)
    val previewSizes: ArrayList<ImageParams> =
        previewHeights.mapTo(arrayListOf()) { ImageParams(width = 0, height = it) }
    return previewParams?.let { previewParams ->
        AttachmentsUtils.getPreviewUrls(params = previewParams, extParams = null, imgParams = previewSizes)
    } ?: emptyMap()
}

private val FileInfoViewModel.titleWithExtension: String
    get() = fileExtension?.let { ext -> "$title.$ext" } ?: title