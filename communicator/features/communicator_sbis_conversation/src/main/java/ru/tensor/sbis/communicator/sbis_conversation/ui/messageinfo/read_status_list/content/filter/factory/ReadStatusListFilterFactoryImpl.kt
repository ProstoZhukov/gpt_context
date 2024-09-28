package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.factory

import ru.tensor.sbis.communicator.generated.AnchorReadStatus
import ru.tensor.sbis.communicator.generated.MessageReadStatus
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import java.util.*
import javax.inject.Inject

/**
 * Реализация фабрики фильтров для запросов списка с микросервиса списка статусов прочитанности сообщения
 * @see [MessageReceiverReadStatusFilter]
 *
 * @property messageUuid идентификатор сообщения
 *
 * @author vv.chekurda
 */
internal class ReadStatusListFilterFactoryImpl @Inject constructor(
    private val messageUuid: UUID
) : ReadStatusListFilterFactory {

    override fun createFilter(
        searchFilter: MessageReadStatus,
        searchQuery: String,
        anchor: AnchorReadStatus?,
        count: Long
    ): MessageReceiverReadStatusFilter =
        MessageReceiverReadStatusFilter(
            messageUuid,
            searchFilter,
            searchQuery,
            anchor ?: AnchorReadStatus(),
            count.toInt()
        )
}