package ru.tensor.sbis.design.message_panel.recorder_common.contact

/**
 * Слушатель действий над панелью получателей в панелях записи аудио и видео сообщений.
 *
 * @author vv.chekurda
 */
interface RecordRecipientsActionListener {

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
 * Слушатель действий над панелью цитаты в панелях записи аудио и видео сообщений.
 *
 * @author vv.chekurda
 */
interface RecordQuoteActionListener {

    /**
     * Клик по кнопке очистки цитаты.
     */
    fun onClearButtonClicked()
}