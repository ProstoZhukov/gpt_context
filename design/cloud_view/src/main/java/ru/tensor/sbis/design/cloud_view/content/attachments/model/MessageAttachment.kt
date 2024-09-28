package ru.tensor.sbis.design.cloud_view.content.attachments.model

import ru.tensor.sbis.attachments.models.AttachmentUploadModel
import ru.tensor.sbis.attachments.models.property.*

/**
 * Временная модель для вложения
 *
 * @author ma.kolpakov
 */
interface MessageAttachment :
    AttachmentUploadModel,
    AttachmentFlagsModel,
    AttachmentForeignSignsCountModel,
    AttachmentPreviewMapModel,
    AttachmentPreviewSizeModel