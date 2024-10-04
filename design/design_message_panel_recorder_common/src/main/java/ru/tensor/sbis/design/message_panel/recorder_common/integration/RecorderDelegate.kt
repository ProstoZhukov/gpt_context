package ru.tensor.sbis.design.message_panel.recorder_common.integration

import ru.tensor.sbis.design.message_panel.recorder_common.contact.BaseRecordViewApi
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordResultListener
import ru.tensor.sbis.message_panel.view.MessagePanel

/**
 * Делегат компонента записи [BaseRecordViewApi] для подключения к панели сообщений [MessagePanel].
 *
 * @author vv.chekurda
 */
interface RecorderDelegate<STATE, RESULT> {

    /**
     * Изменить доступность записи аудиосообщения.
     */
    var isEnabled: Boolean

    /**
     * Текущее состояние записи.
     */
    val state: STATE?

    /**
     * Установить слушателя результата записи.
     * Опционально, если нужна дополнительная реакция.
     */
    fun setRecordResultListener(listener: RecordResultListener<RESULT>?)

    /**
     * Установить слушателя изменения состояния записи.
     * Опционально, если нужна дополнительная реакция.
     */
    fun setRecordStateChangedListener(listener: (STATE) -> Unit)

    /**
     * Установить слашуталя нажатий на кнопку начала записи.
     * Опционально, если нужна дополнительная реакция.
     */
    fun setOnRecordClickListener(listener: () -> Unit)

    /**
     * Остановить запись.
     * Панель останется в состоянии записанного файла, готового к отправке.
     * Останавливается автоматически на onStop.
     */
    fun stopRecording()

    /**
     * Сбросить запись.
     * Сбрасывается автоматически на onDestroyView.
     */
    fun cancelRecording()

    /**
     * Очистить компонент записи.
     * Отменяет текущую запись [cancelRecording].
     * Вызывается автоматически на onDestroyView.
     */
    fun release()
}