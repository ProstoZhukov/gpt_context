package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter

import ru.tensor.sbis.communicator.generated.AnchorReadStatus
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.factory.ReadStatusListFilterFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.base.domain.entity.paging.CreateFilter
import javax.inject.Inject

/**
 * Создатель фильтров для запросов списка с микросервиса списка статусов прочитанности сообщения
 * @see [ReadStatusListFilterFactory]
 *
 * @property filterFactory фабрика фильтров для запросов микросервиса
 * @property liveData      параметры состояния вью-модели
 *
 * @author vv.chekurda
 */
internal class ReadStatusListFilterCreator @Inject constructor(
    private val filterFactory: ReadStatusListFilterFactory,
    private val liveData: ReadStatusListVMLiveData
) {

    /**
     * Поисковый фильтр
     */
    private val searchFilter: MessageReadStatus
        get() = liveData.searchInputFilter.value!!

    /**
     * Поисковый запрос
     */
    private val searchQuery: String
        get() = liveData.searchQuery.value!!

    /**
     * Создает фильтр для следующей страницы
     */
    val createFilterForNextPage: CreateReadStatusFilter = { anchor: AnchorReadStatus?, _: Boolean, itemsOnPage: Long, _: Int ->
        filterFactory.createFilter(searchFilter, searchQuery, anchor, itemsOnPage)
    }

    /**
     * Создает фильтр для предыдущей страницы страницы
     */
    val createFilterForPreviousPage: CreateReadStatusFilter = { anchor: AnchorReadStatus?, _, itemsOnPage, _ ->
        filterFactory.createFilter(searchFilter, searchQuery, anchor, itemsOnPage)
    }
}

private typealias CreateReadStatusFilter = CreateFilter<AnchorReadStatus, MessageReceiverReadStatusFilter>