package ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec

import ru.tensor.sbis.design.message_panel.video_recorder.recorder.codec.surface.CodecSurfaceDrawer

/**
 * Слушатель кодека [SurfaceVideoCodec].
 *
 * @author vv.chekurda
 */
internal interface SurfaceVideoCodecListener {

    /**
     * Обработать событие, когда кодек готов.
     */
    fun onPrepared(surfaceDrawer: CodecSurfaceDrawer)

    /**
     * Обработать событие, когда началась запись видео.
     */
    fun onStarted() = Unit

    /**
     * Обработать событие, когда запись видео остановилась.
     */
    fun onStopped() = Unit

    /**
     * Обработать событие, когда произошла ошибка во время записи видео.
     */
    fun onError(error: Exception) = Unit
}