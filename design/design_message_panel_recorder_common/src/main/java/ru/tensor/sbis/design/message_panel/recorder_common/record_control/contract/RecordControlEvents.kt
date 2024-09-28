package ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract

import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView

/**
 * События панели управления [RecordControlView].
 *
 * @author vv.chekurda
 */
sealed interface RecordControlEvent {

    /**
     * Панель закончила анимацию появления и готова к началу записи.
     */
    object OnReady : RecordControlEvent

    /**
     * Панель записи закончила анимацию скрытия.
     */
    object OnHidingEnd : RecordControlEvent

    /**
     * Запись остановлена.
     */
    object OnRecordStopped : RecordControlEvent

    /**
     * Запись отменена.
     */
    object OnRecordCanceled : RecordControlEvent

    /**
     * Произошел клик по кнопке отправки после остановки всех анимаций записи.
     */
    object OnSendClicked : RecordControlEvent
}