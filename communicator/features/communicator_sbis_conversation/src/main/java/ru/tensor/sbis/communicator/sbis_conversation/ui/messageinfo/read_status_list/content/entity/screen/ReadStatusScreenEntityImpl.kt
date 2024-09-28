package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen

import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusFilterAndPageProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListPagingEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity

/**
 * Реализация бизнес модели экрана списка статусов прочитанности сообщения
 * @see [ReadStatusScreenEntity]
 *
 * @property listPagingEntity бизнес модель для обработки данных страницы [PagingEntity]
 * @param    filterCreator    создатель фильтров для запросов
 *
 * @author vv.chekurda
 */
internal class ReadStatusScreenEntityImpl(
    private val listPagingEntity: ReadStatusListPagingEntity,
    filterCreator: ReadStatusFilterAndPageProvider
) : ReadStatusScreenEntity,
    PagingListScreenEntity<MessageReceiverReadStatusFilter> by listPagingEntity,
    ReadStatusFilterAndPageProvider by filterCreator {

    override fun update(page: Int, result: ReadStatusListResult) {
        listPagingEntity.update(page, result)
    }
}