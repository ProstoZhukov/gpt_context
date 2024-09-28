package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.generated.ConversationType
import ru.tensor.sbis.communicator.generated.ThemeFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.persons.ConversationRegistryItem
import java.util.*

/** @SelfDocumented */
internal class ThemeListCache {

    @get:Synchronized @set:Synchronized
    private var dialogsCache: PagedListResult<ConversationRegistryItem>? = null
    @get:Synchronized @set:Synchronized
    private var channelsCache: PagedListResult<ConversationRegistryItem>? = null

    /** @SelfDocumented */
    fun addToCache(filter: ThemeFilter, result: PagedListResult<ConversationRegistryItem>) {
        if (filter.direction == QueryDirection.TO_OLDER && filter.searchString.isEmpty() && filter.fromUuid == null &&
            filter.person == null
        ) {
            setCache(filter.themeType == ConversationType.CHAT, result)
        }
    }

    /** @SelfDocumented */
    fun getCache(forChannels: Boolean): PagedListResult<ConversationRegistryItem>? {
        return if (forChannels) channelsCache else dialogsCache
    }

    /** @SelfDocumented */
    fun deleteFromCache(forChannels: Boolean, uuid: UUID) =
        if (forChannels) channelsCache?.findModelAndDelete(uuid)
        else dialogsCache?.findModelAndDelete(uuid)

    private fun setCache(forChannels: Boolean, value: PagedListResult<ConversationRegistryItem>) {
        if (forChannels) {
            channelsCache = PagedListResult(value.dataList, value.hasMore(), value.metaData)
        } else {
            dialogsCache = PagedListResult(value.dataList, value.hasMore(), value.metaData)
        }
    }

    private fun PagedListResult<ConversationRegistryItem>?.findModelAndDelete(uuid: UUID) {
        val model = this?.dataList?.find { it is ConversationModel && it.uuid == uuid }
        this?.dataList?.remove(model)
    }
}