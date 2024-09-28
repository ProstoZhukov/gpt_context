package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

/**
 * Слушатель количества получателей сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusMessageReceiversListener {

    /**
     * Обновлено количество получателей сообщения
     * @param count текущее количество получателей сообщения
     */
    fun onMessageReceiversCountChanged(count: Int)
}