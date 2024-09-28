package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract

import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordResultData
import java.lang.Exception

/**
 * Слушатель результата компонента записи видео.
 *
 * @author vv.chekurda
 */
interface RoundCameraListener {

    /**
     * Запись успешно завершена завершена.
     *
     * @param resultData результат записи аудиосообщения.
     * @param byTimeOut запись завершилась по таймауту.
     */
    fun onRecordCompleted(resultData: VideoRecordResultData, byTimeOut: Boolean = false)

    /**
     * Запись началась.
     */
    fun onRecordStarted() = Unit

    /**
     * Запись отменена.
     */
    fun onRecordCanceled() = Unit

    /**
     * Произошла ошибка во время записи.
     */
    fun onRecordError(error: Exception) = Unit

    /**
     * Изменилась амплитуда громкости микрофона.
     */
    fun onVolumeAmplitudeChanged(amplitude: Float) = Unit
}