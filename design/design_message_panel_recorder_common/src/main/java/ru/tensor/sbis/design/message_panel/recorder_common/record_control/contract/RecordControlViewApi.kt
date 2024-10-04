package ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract

import androidx.annotation.FloatRange
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView

/**
 * API компонента панели управления записью аудио/видео сообщений.
 * @see RecordControlView
 *
 * @author vv.chekurda
 */
interface RecordControlViewApi {

    /**
     * Данные для декорирования внешнего вида панели записи под текущий вид панели сообщений.
     */
    var decorData: RecorderDecorData

    /**
     * Признак записи аудио.
     */
    var isAudioRecord: Boolean

    /**
     * Обработчик событий [RecordControlEvent] панели управления.
     */
    var eventsHandler: ControlEventsHandler

    /**
     * Слушатель действий над панелью получателей.
     */
    var recipientsActionListener: RecordControlRecipientsActionListener?

    /**
     * Слушатель действий над панелью цитаты.
     */
    var quoteActionListener: RecordControlQuoteActionListener?

    /**
     * Состояние закрепленной записи.
     */
    val isLocked: Boolean

    /**
     * Текущее время записи в секундах.
     */
    val recordDuration: Int

    /**
     * Исходная позиция кнопки записи, которой управляет пользователь.
     */
    var recordButtonPosition: RecordControlButtonPosition

    /**
     * Признак доступности кнопки отправки.
     */
    var isSendButtonEnabled: Boolean

    /**
     * Установить текущую амплитуду громкости микрофона.
     */
    fun setAmplitude(@FloatRange(from = 0.0, to = 1.0) amplitude: Float)

    /**
     * Анимировать появления панели.
     *
     * @param withLock с закреплением записи.
     */
    fun animateShowing(withLock: Boolean = false)

    /**
     * Анимировать скрытие панели.
     */
    fun animateHiding()

    /**
     * Начать анимацию записи.
     */
    fun startRecordAnimation()

    /**
     * Остановить анимацию записи.
     */
    fun stopRecordAnimation()

    /**
     * Очистить анимацию записи, чтобы вернуть ее в исходное состояние.
     */
    fun clearRecordAnimation()
}

/**
 * Обработчик событий [RecordControlEvent].
 */
typealias ControlEventsHandler = (RecordControlEvent) -> Unit