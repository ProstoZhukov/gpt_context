package ru.tensor.sbis.design.video_message_view.player.data

import ru.tensor.sbis.attachments.ui.viewmodel.base.preview.AttachmentPreviewVM
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.VideoSource
import ru.tensor.sbis.design.video_message_view.message.VideoMessageView

/**
 * Модель с данными для отображения и проигрывания видеофайлов в [VideoMessageView].
 *
 * @property videoSource источник для получения видеофайла.
 * @property durationSeconds продолжительность в секундах.
 * @property previewVM превью видеофайла.
 *
 * @author da.zhukov
 */
data class VideoPlayerViewData internal constructor(
    val videoSource: VideoSource,
    val durationSeconds: Int,
    internal val previewVM: AttachmentPreviewVM?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoPlayerViewData

        if (videoSource != other.videoSource) return false
        if (durationSeconds != other.durationSeconds) return false
        if (!previewVM.equalsOther(other.previewVM)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = videoSource.hashCode()
        result = 31 * result + durationSeconds
        result = 31 * result + previewVM.hashCode()
        return result
    }

    private fun AttachmentPreviewVM?.equalsOther(other: AttachmentPreviewVM?): Boolean =
        (this == null && other == null) ||
            this != null &&
            other != null &&
            attachmentPrimaryId == other.attachmentPrimaryId &&
            attachmentSecondaryId == other.attachmentSecondaryId &&
            sources == other.sources &&
            autoPlayGif == other.autoPlayGif &&
            aspectRatio == other.aspectRatio
}