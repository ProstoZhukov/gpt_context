package ru.tensor.sbis.design.message_panel.decl.record

/**
 * Слушатель зажатия кнопки записи аудио/видео сообщения.
 *
 * @author vv.chekurda
 */
fun interface MessagePanelRecordButtonListener {

    /**
     * Кнопка записи нажата.
     *
     * @param isLongPressed кнопка зажата.
     */
    fun onClick(isLongPressed: Boolean)
}