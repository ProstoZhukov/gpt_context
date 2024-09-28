package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract

import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordResultData
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record.AudioRecordControlView

/**
 * События компонента отправки записанного аудиосообщения.
 *
 * @author vv.chekurda
 */
internal sealed interface AudioRecordSendViewEvent {

    /**
     * Аудиосообщение готово к отправке.
     *
     * @param resultData результат записи аудиосообщения.
     */
    class OnComplete(val resultData: AudioRecordResultData) : AudioRecordSendViewEvent

    /**
     * Отмена отправки аудиосообщения.
     */
    object OnSendCanceled : AudioRecordSendViewEvent

    /**
     * Нажата кнопка отправки, но запись еще не завершилась.
     *
     * @param emotion эмоция аудиосообщения.
     */
    class OnSendClicked(val emotion: AudioMessageEmotion) : AudioRecordSendViewEvent
}

/**
 * События компонента управления закрепленной записи аудиосообщения.
 * @see AudioRecordControlView
 */
internal sealed interface AudioRecordControlViewEvent : AudioRecordSendViewEvent {

    /**
     * Запись остановлена.
     */
    object OnRecordStopped : AudioRecordControlViewEvent

    /**
     * Отмена записи аудиосообщения.
     */
    object OnRecordCanceled : AudioRecordControlViewEvent

    /**
     * Анимация скрытия завершилась.
     */
    object OnHidingEnd : AudioRecordControlViewEvent
}
