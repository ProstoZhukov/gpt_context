package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter

import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusFilterAndPageProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListPagingEntity
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import javax.inject.Inject

/**
 * Делегат предоставления провайдеров фильров для запросов списка с микросервиса списка статусов прочитанности сообщения
 * @see [FilterProvider]
 * @see [FilterProvider]
 *
 * @property listPagingEntity бизнес модель для обработки данных страницы [PagingEntity]
 * @property filterCreator    создатель фильтров для запросов
 *
 * @author vv.chekurda
 */
internal class ReadStatusFilterAndPageProviderDelegate @Inject constructor(
    private val listPagingEntity: ReadStatusListPagingEntity,
    private val filterCreator: ReadStatusListFilterCreator
) : ReadStatusFilterAndPageProvider {

    override fun provideFilterForNextPage(): FilterAndPageProvider<MessageReceiverReadStatusFilter> =
        listPagingEntity.filterForNext(filterCreator.createFilterForNextPage)

    override fun provideFilterForPreviousPage(): FilterAndPageProvider<MessageReceiverReadStatusFilter> =
        listPagingEntity.filterForPrevious(filterCreator.createFilterForPreviousPage)
}