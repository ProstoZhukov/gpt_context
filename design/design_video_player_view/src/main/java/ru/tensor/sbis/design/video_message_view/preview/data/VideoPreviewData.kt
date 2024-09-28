package ru.tensor.sbis.design.video_message_view.preview.data

import ru.tensor.sbis.attachments.ui.viewmodel.base.preview.AttachmentPreviewVM

/**
 * Модель с данными для отображения превью видеосообщения.
 *
 * @property previewVM вью-модель превью вложения.
 * @property durationSeconds время записи видео.
 * @property recognizedText распознанное сообщение.
 *
 * @author dv.baranov
 */
data class VideoPreviewData(
    val previewVM: AttachmentPreviewVM,
    val durationSeconds: Int,
    val recognizedText: String? = null
)