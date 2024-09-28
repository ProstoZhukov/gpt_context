package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data

import ru.tensor.sbis.consultations.generated.QuickReplyFilter
import java.util.UUID

/**
 * Холдер фильтра быстрых ответов.
 *
 * @author dv.baranov
 */
internal class QuickReplyFilterHolder(private val filter: QuickReplyFilter) : () -> QuickReplyFilter {

    override fun invoke(): QuickReplyFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setSearchQuery(query: String?) {
        filter.searchQuery = query
    }

    /** @SelfDocumented */
    fun setParentId(uuid: UUID?) {
        filter.parentId = uuid
    }

    /** @SelfDocumented */
    fun setChannelId(uuid: UUID?) {
        filter.channelId = uuid
    }
}
