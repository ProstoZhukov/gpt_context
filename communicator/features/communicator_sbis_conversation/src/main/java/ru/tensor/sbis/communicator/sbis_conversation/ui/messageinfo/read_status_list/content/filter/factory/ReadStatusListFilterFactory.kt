package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.factory

import ru.tensor.sbis.communicator.generated.AnchorReadStatus
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter

/**
 * Фабрика фильтров для запросов списка с микросервиса списка статусов прочитанности сообщения
 * @see [MessageReceiverReadStatusFilter]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListFilterFactory {

    /**
     * Создать фильтр
     *
     * @param searchFilter поисковый фильтр
     * @param searchQuery  поисковый запрос
     * @param anchor       якорь, от которой запрашивать страницу
     * @param count        количество элементов запрашиваемой страницы
     */
    fun createFilter(
        searchFilter: MessageReadStatus,
        searchQuery: String,
        anchor: AnchorReadStatus?,
        count: Long
    ): MessageReceiverReadStatusFilter
}