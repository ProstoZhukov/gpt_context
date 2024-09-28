package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input

import ru.tensor.sbis.communicator.generated.MessageReadStatus

/**
 * Селектор фильтра списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListFilterSelector {

    /**
     * Выбрать фильтр
     *
     * @param filter фильтр списка статусов
     */
    fun selectFilter(filter: MessageReadStatus)
}