package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import ru.tensor.sbis.communicator.generated.ThemeFilter
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.persons.ConversationRegistryItem

/** @SelfDocumented */
internal class ThemePagedListResult(
    dataList: List<ConversationRegistryItem>,
    hasMore: Boolean,
    metadata: HashMap<String, String>,
    val requestFilter: ThemeFilter?
) : PagedListResult<ConversationRegistryItem?>(dataList, hasMore, metadata)