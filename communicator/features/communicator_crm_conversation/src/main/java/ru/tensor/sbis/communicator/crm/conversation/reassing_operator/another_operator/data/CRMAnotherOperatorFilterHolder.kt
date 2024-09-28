package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data

import ru.tensor.sbis.consultations.generated.OperatorCollectionFilter
import java.util.UUID

/**
 * Холдер фильтра переназначения оператору.
 *
 * @author da.zhukov
 */
class CRMAnotherOperatorFilterHolder(
    private val filter: OperatorCollectionFilter
): () -> OperatorCollectionFilter {

    override fun invoke(): OperatorCollectionFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setQuery(query: String?) {
        filter.searchQuery = query
    }

    /** @SelfDocumented */
    fun setChannel(channelId: UUID?) {
        filter.channelId = channelId
    }
}