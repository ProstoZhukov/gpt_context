package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data

import ru.tensor.sbis.communicator.generated.LinkFilter

/**
 * Холдер фильтра экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListFilterHolder(private val filter: LinkFilter) : () -> LinkFilter {

    override fun invoke(): LinkFilter {
        return filter
    }

    /** @SelfDocumented */
    fun setSearchQuery(query: String) {
        filter.link = query
    }
}
