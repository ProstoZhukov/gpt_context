package ru.tensor.sbis.design.message_panel.recorder_common.contact

import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData

/**
 * Делегат компонента записи панели сообщений.
 *
 * @author vv.chekurda
 */
interface MessagePanelRecordViewDelegate {

    /**
     * Данные для декорирования компонента записи под текущее состояние панели сообщений.
     */
    var decorData: RecorderDecorData

    /**
     * Установить слушателя действий над панелью получателей.
     */
    fun setRecipientsActionListener(listener: RecordRecipientsActionListener?)

    /**
     * Установить слушателя действий над панелью цитаты.
     */
    fun setQuoteActionListener(listener: RecordQuoteActionListener?)
}