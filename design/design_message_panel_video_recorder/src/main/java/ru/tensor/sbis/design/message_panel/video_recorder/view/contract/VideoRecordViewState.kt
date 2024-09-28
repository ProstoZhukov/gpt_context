package ru.tensor.sbis.design.message_panel.video_recorder.view.contract

/**
 * Состояние панели записи видеосообщения.
 *
 * @property isRecording признак процесса записи видеосообщения.
 * @property isSendPreparing признак процесса подготовки к отправке видеосообщения.
 *
 * @author vv.chekurda
 */
data class VideoRecordViewState(
    val isRecording: Boolean = false,
    val isSendPreparing: Boolean = false
) {
    /**
     * Признак видимости панели записи.
     */
    val isVisible: Boolean
        get() = isRecording || isSendPreparing
}