package ru.tensor.sbis.design.message_panel.recorder_common.contact

import java.lang.Exception

/**
 * Слушатель результата компонента записи.
 *
 * @author vv.chekurda
 */
fun interface RecordResultListener<RESULT> {

    /**
     * Запись успешно завершена завершена.
     *
     * @param resultData результат записи.
     */
    fun onRecordCompleted(resultData: RESULT)

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
}