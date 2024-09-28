package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data

import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionFilter
import java.util.UUID

/**
 * Холдер фильтра каналов CRM.
 *
 * @author da.zhukov
 */
internal class CRMChannelsFilterHolder(
    private val filter: ChannelHierarchyCollectionFilter
): () -> ChannelHierarchyCollectionFilter {

    override fun invoke(): ChannelHierarchyCollectionFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setQuery(query: String?) {
        filter.searchStr = query
    }

    /** @SelfDocumented */
    fun setParentId(parentIds: ArrayList<UUID>) {
        filter.parentIds = parentIds
    }

    /** @SelfDocumented */
    fun setGroupType(type: ChannelGroupType?) {
        filter.groupType = type
    }
}