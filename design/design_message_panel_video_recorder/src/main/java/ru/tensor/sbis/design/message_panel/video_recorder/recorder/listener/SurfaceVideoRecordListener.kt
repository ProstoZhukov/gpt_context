package ru.tensor.sbis.design.message_panel.video_recorder.recorder.listener

import ru.tensor.sbis.design.message_panel.video_recorder.recorder.SurfaceVideoRecorder
import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer
import java.io.File

/**
 * Слушатель процесса записи видео с [SurfaceVideoRecorder].
 *
 * @author vv.chekurda
 */
internal interface SurfaceVideoRecordListener {

    /**
     * Обработать событие, когда [SurfaceVideoRecorder] готов к записи.
     */
    fun onPrepared(surfaceDrawer: CodecSurfaceDrawer)

    /**
     * Обработать событие, когда началась запись видео.
     */
    fun onStarted()

    /**
     * Обработать событие, когда запись видео закончилась.
     */
    fun onFinished(file: File, durationSeconds: Int)

    /**
     * Обработать событие, когда запись видео была остановлена.
     */
    fun onCancelled()

    /**
     * Обработать событие, когда произошла ошибка во время записи видео.
     */
    fun onError(error: Exception)

    /**
     * Изменилась амплитуда громкости микрофона.
     */
    fun onVolumeAmplitudeChanged(amplitude: Float) = Unit
}