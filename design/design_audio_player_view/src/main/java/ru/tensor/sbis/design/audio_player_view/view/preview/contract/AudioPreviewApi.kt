package ru.tensor.sbis.design.audio_player_view.view.preview.contract

import ru.tensor.sbis.design.audio_player_view.view.preview.data.AudioPreviewData

/**
 * API компонента превью аудиосообщения [AudioPreview].
 *
 * @author dv.baranov
 */
interface AudioPreviewApi {

    /**
     * Данные для отображения превью.
     */
    var data: AudioPreviewData?
}