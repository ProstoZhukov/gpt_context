package ru.tensor.sbis.message_panel.attachments.viewer

import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.attachmentId
import ru.tensor.sbis.attachments.decl.canonicalLocalUri
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentParams
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentViewerArgs
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.attachments.models.action.AttachmentActionType
import ru.tensor.sbis.attachments.models.validateLocalUriAsPreviewUri
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.viewer.decl.slider.ThumbnailListDisplayArgs
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import ru.tensor.sbis.viewer.decl.viewer.equalsById
import java.util.EnumSet


/**
 * Стандартная реализация фабрики [ViewerSliderArgsFactory] для создрания
 * аргументов просмотрщика вложений при открытии из панели сообщений
 *
 * @author vv.chekurda
 */
object DefaultViewerSliderArgsFactory : ViewerSliderArgsFactory {

    /**
     * Действия, которые доступны пользователю с вложением в просмотрщике
     */
    private val allowedActions =
        EnumSet.of(
            AttachmentActionType.VIEW_DETAILS,
            AttachmentActionType.VIEW_REDACTIONS,
            AttachmentActionType.VIEW_ACCESS_RIGHTS,
            AttachmentActionType.VIEW_LINK,
            AttachmentActionType.OPEN,
            AttachmentActionType.SHARE,
            AttachmentActionType.DOWNLOAD,
            AttachmentActionType.DOWNLOAD_PDF_WITH_STAMP
        )

    override fun createViewerSliderArgs(
        attachmentList: List<FileInfo>,
        selectedAttachment: FileInfo
    ): ViewerSliderArgs {
        val viewerArgsList = ArrayList(attachmentList.map { it.toViewerArgs() })
        val selectedViewerArgs = selectedAttachment.toViewerArgs()
        return ViewerSliderArgs(
            viewerArgsList,
            viewerArgsList.indexOfFirst { it.equalsById(selectedViewerArgs) }.coerceAtLeast(0),
            ThumbnailListDisplayArgs(visible = true)
        )
    }

    private fun FileInfo.toViewerArgs(): ViewerArgs {
        val thumbnailUri: String? =
            validateLocalUriAsPreviewUri(FileUtil.detectFileType(fileName), canonicalLocalUri)
                ?: previewUrls.values.firstOrNull()
        return RegularAttachmentViewerArgs(
            attachmentParams = RegularAttachmentParams(UrlUtils.FILE_SD_OBJECT, attachmentId),
            title = title,
            allowedActionResolver = AllowedActionResolver.FromActions(allowedActions),
            thumbnailPreviewUri = thumbnailUri
        )
    }
}
