/**
 * Слушатели действий над элементами панели управления записью.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract

import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView

/**
 * Слушатель действий над панелью получателей на панели управления записью [RecordControlView].
 */
interface RecordControlRecipientsActionListener {

    /**
     * Клик по панели получателей.
     */
    fun onRecipientsClicked()

    /**
     * Клик по кнопке очистки получателей.
     */
    fun onClearButtonClicked()
}

/**
 * Слушатель действий над панелью цитаты на панели управления записью [RecordControlView].
 */
interface RecordControlQuoteActionListener {

    /**
     * Клик по кнопке очистки цитаты.
     */
    fun onClearButtonClicked()
}