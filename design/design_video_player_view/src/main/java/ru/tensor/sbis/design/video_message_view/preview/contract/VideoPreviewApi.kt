package ru.tensor.sbis.design.video_message_view.preview.contract

import ru.tensor.sbis.design.video_message_view.preview.data.VideoPreviewData

/**
 * API компонента превью видеосообщения [VideoPreview].
 *
 * @author dv.baranov
 */
interface VideoPreviewApi {

    /**
     * Данные для отображения превью.
     */
    var data: VideoPreviewData?
}