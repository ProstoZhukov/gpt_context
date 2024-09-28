package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record

import androidx.annotation.FloatRange
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordControlViewEvent

/**
 * API view управления закрепленной записью аудиосообщения.
 *
 * @author vv.chekurda
 */
interface AudioRecordControlViewApi {

    /**
     * Режим работы.
     * По умолчанию [AudioRecordMode.SIMPLE]
     */
    var mode: AudioRecordMode

    /**
     * Установить текущую амплитуду громкости микрофона.
     */
    fun setAmplitude(@FloatRange(from = 0.0, to = 1.0) amplitude: Float)

    /**
     * Показать панель.
     */
    fun show()

    /**
     * Скрыть панель.
     */
    fun hide()

    /**
     * Начать анимацию записи.
     */
    fun startRecordAnimation()

    /**
     * Очистить анимацию записи, чтобы вернуть ее в исходное состояние.
     */
    fun clearRecordAnimation()
}

/**
 * Обработчик событий [AudioRecordControlView].
 */
internal typealias ControlEventsHandler = (AudioRecordControlViewEvent) -> Unit