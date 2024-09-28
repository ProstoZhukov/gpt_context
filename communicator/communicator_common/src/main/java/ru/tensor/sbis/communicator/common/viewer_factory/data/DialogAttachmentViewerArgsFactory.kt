package ru.tensor.sbis.communicator.common.viewer_factory.data

import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.canonicalUri
import ru.tensor.sbis.attachments.decl.oldAttachmentId
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentParams
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.attachments.models.action.AttachmentActionType
import ru.tensor.sbis.common.util.UrlUtils
import java.util.EnumSet
import java.util.UUID

/**
 * Фабрика аргументов вложений диалога для свкозного просмотрщика
 *
 * @author vv.chekurda
 */
object DialogAttachmentViewerArgsFactory {

    /**
     * Перечень доступных действий над вложением
     */
    private val allowedActions by lazy {
        EnumSet.of(
            AttachmentActionType.VIEW_DETAILS,
            AttachmentActionType.VIEW_REDACTIONS,
            AttachmentActionType.VIEW_ACCESS_RIGHTS,
            AttachmentActionType.VIEW_LINK,
            AttachmentActionType.OPEN,
            AttachmentActionType.SHARE,
            AttachmentActionType.DOWNLOAD,
            AttachmentActionType.DOWNLOAD_PDF_WITH_STAMP,
            AttachmentActionType.SIGN,
            AttachmentActionType.RENAME
        )
    }

    /**
     * Создать аргументы просмотрщика из контроллеровской модели вложения диалога
     */
    fun FileInfoViewModel.createArgs(uuid: UUID): DialogAttachmentViewerArgs =
        DialogAttachmentViewerArgs(
            attachmentUuid = uuid,
            attachmentParams = RegularAttachmentParams(UrlUtils.FILE_SD_OBJECT, oldAttachmentId),
            title = "$title.$fileExtension",
            allowedActionResolver = AllowedActionResolver.FromActions(allowedActions),
            previewUri = null,
            thumbnailPreviewUri = canonicalUri(localPath) ?: previewUrl,
            fileInfo = this
        )
}