package ru.tensor.sbis.design.cloud_view.content.attachments

import android.content.Context
import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentParams
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentViewerArgs
import ru.tensor.sbis.attachments.models.action.AttachmentActionType
import ru.tensor.sbis.attachments.models.action.defaultViewerAllowedActions
import ru.tensor.sbis.attachments.models.id.equalsByLocalIds
import ru.tensor.sbis.attachments.models.originalPreviewUri
import ru.tensor.sbis.attachments.models.previewUri
import ru.tensor.sbis.attachments.models.previewUris
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment
import ru.tensor.sbis.viewer.decl.slider.ThumbnailListDisplayArgs
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.util.EnumSet

/**
 * Стандартный обработчик кликов по вложениям в облачке.
 *
 * @author vv.chekurda
 */
internal class DefaultCloudViewAttachmentClickListener(
    private val viewerSliderIntentFactory: ViewerSliderIntentFactory?
) : AttachmentClickListener {

    override fun onAttachmentClicked(
        context: Context,
        attachment: MessageAttachment,
        attachments: List<MessageAttachment>
    ) {
        viewerSliderIntentFactory ?: return
        val selectedIndex = attachments.indexOfFirst { it.id.equalsByLocalIds(attachment.id) }
            .coerceAtLeast(0)
        val resolver: AllowedActionResolver = AllowedActionResolver.FromActions(
            EnumSet.copyOf(defaultViewerAllowedActions - AttachmentActionType.DISCUSS)
        )
        val viewerArgsList = ArrayList<ViewerArgs>(
            attachments.map { file ->
                RegularAttachmentViewerArgs(
                    attachmentParams = RegularAttachmentParams(
                        blObjectName = UrlUtils.FILE_SD_OBJECT,
                        attachmentId = file.id
                    ),
                    title = file.name,
                    allowedActionResolver = resolver,
                    thumbnailPreviewUri = file.let {
                        it.previewUri() ?: it.originalPreviewUri() ?: it.previewUris()?.values?.firstOrNull()
                    }
                )
            }
        )
        val args = ViewerSliderArgs(
            viewerArgsList = viewerArgsList,
            selectedPagePosition = selectedIndex,
            thumbnailListDisplayArgs = ThumbnailListDisplayArgs(true)
        )
        val intent = viewerSliderIntentFactory.createViewerSliderIntent(context, args)
        context.startActivity(intent)
    }
}