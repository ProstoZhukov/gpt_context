package ru.tensor.sbis.design.message_panel.recorder_common.audio_record

import java.io.File

/**
 * Слушатель компонента записи аудио.
 * @see AudioRecorder
 *
 * @author vv.chekurda
 */
interface AudioRecordListener {

    /**
     * Запись завершена.
     *
     * @param audioFile записанный аудиофайл.
     * @param duration продолжительность записи в секундах.
     * @param waveform осциллограмма.
     */
    fun onRecordCompleted(audioFile: File, duration: Int, waveform: ByteArray)

    /**
     * Запись началась.
     */
    fun onRecordStarted() = Unit

    /**
     * Запись отменена.
     */
    fun onRecordCanceled() = Unit

    /**
     * Ошибка процесса записи.
     */
    fun onError(error: AudioRecordError) = Unit

    /**
     * Изменилась амплитуда громкости микрофона.
     */
    fun onVolumeAmplitudeChanged(amplitude: Float) = Unit
}
