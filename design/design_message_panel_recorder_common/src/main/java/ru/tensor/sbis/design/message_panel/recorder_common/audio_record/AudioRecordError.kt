package ru.tensor.sbis.design.message_panel.recorder_common.audio_record

/**
 * Ошибка процесса записи аудио.
 *
 * @author vv.chekurda
 */
sealed class AudioRecordError {

    abstract val exception: Exception

    /**
     * Ошибка подготовки компонента записи.
     */
    data class PreparingError(override val exception: Exception) : AudioRecordError()

    /**
     * Ошибка процесса записи.
     */
    data class RecordingError(override val exception: Exception) : AudioRecordError()
}