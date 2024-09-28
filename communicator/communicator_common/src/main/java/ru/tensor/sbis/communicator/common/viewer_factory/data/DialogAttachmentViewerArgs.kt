package ru.tensor.sbis.communicator.common.viewer_factory.data

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.viewer.AttachmentFlagsProvider
import ru.tensor.sbis.attachments.decl.viewer.AttachmentViewerArgs
import ru.tensor.sbis.attachments.decl.viewer.RegularAttachmentParams
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.communicator.generated.DialogAttachment
import java.util.*

/**
 * Аргументы вложения диалога для компонента просмотрщика
 * @see AttachmentViewerArgs
 *
 * @property attachmentUuid идентификатор вложения из модели [DialogAttachment]
 *
 * @author vv.chekurda
 */
@Parcelize
data class DialogAttachmentViewerArgs(
    val attachmentUuid: UUID,
    override val attachmentParams: RegularAttachmentParams,
    override var title: String?,
    override val allowedActionResolver: AllowedActionResolver,
    override val previewUri: String?,
    override val thumbnailPreviewUri: String?,
    override val isImmutableTitle: Boolean = false,
    override val fileInfo: FileInfoViewModel? = null,
    override var flagsProvider: AttachmentFlagsProvider? = fileInfo?.let(AttachmentFlagsProvider::ByFileInfo)
) : AttachmentViewerArgs<RegularAttachmentParams> {

    override val id: String get() = attachmentUuid.toString()
}
