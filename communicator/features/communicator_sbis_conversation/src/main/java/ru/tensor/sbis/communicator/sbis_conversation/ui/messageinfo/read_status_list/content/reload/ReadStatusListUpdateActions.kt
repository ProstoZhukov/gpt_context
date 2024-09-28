package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload

/**
 * Действия обновления списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListUpdateActions {

    /**
     * Перезагрузка списка с текущими параметрами фильтра
     */
    fun reloadList()

    /**
     * Обновление списка при поиске
     */
    fun refreshList()

    /**
     * Обновить список при получении сети
     */
    fun onNetworkConnected()
}