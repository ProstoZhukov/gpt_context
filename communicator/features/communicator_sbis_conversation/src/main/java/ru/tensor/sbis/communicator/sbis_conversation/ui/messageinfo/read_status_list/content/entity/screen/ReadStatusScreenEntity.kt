package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen

import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusFilterAndPageProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity

/**
 * Интерфейс бизнес модели экрана списка статусов прочитанности сообщения
 * @see [ListScreenEntity]
 * @see [ReadStatusFilterAndPageProvider]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusScreenEntity
    : PagingListScreenEntity<MessageReceiverReadStatusFilter>,
    ReadStatusFilterAndPageProvider {

    /**
     * Обновить страницу
     *
     * @param page   номер страницы
     * @param result результат запроса микросервиса
     */
    fun update(page: Int, result: ReadStatusListResult)
}