package ru.tensor.sbis.appdesign.cloudview.data

import ru.tensor.sbis.attachments.models.AttachmentType
import ru.tensor.sbis.attachments.models.id.AttachmentId
import ru.tensor.sbis.attachments.models.property.AttachmentFlag
import ru.tensor.sbis.attachments.models.property.AttachmentPreviewMap
import ru.tensor.sbis.design.cloud_view.content.attachments.model.MessageAttachment

private const val UNDEFINED_PREVIEW_HEIGHT = -1

/**
 * @author us.bessonov
 */
data class DemoMessageAttachment(
    override val id: AttachmentId = AttachmentId(0L),
    override val type: AttachmentType = AttachmentType.IMAGE,
    override val fileName: String = "",
    override val name: String = fileName,
    override val flags: Set<AttachmentFlag> = emptySet(),
    override val foreignSignsCount: Int = 0,
    override val localUri: String? = null,
    val previewUri: String? = null,
    override val size: Long = 0,
    override val previewUris: AttachmentPreviewMap? = previewUri?.let { mapOf(UNDEFINED_PREVIEW_HEIGHT to it) },
    override val previewWidth: Int = 20,
    override val previewHeight: Int = 20,
) : MessageAttachment {

    constructor(
        id: Long,
        url: String? = null,
        type: AttachmentType = AttachmentType.IMAGE,
        fileName: String = "",
        isSignedByMe: Boolean = false,
        foreignSignsCount: Int = 0
    ) : this(
        AttachmentId(id),
        type,
        fileName,
        flags = getFlags(isSignedByMe, foreignSignsCount),
        foreignSignsCount = foreignSignsCount,
        previewUri = url
    )
}

private fun getFlags(isSignedByMe: Boolean, foreignSignsCount: Int) = mutableSetOf<AttachmentFlag>().apply {
    if (isSignedByMe) add(AttachmentFlag.SIGNED_BY_ME)
    if (foreignSignsCount > 0) add(AttachmentFlag.SIGNED_BY_OTHERS)
}