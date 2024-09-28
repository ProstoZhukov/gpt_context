package ru.tensor.sbis.communicator.contacts_declaration.model.filter

import java.util.*

/**
 * Фильтр запросов списка реестра контактов.
 *
 * @author vv.chekurda
 */
data class ContactListFilter(
    val searchQuery: String? = null,
    val sort: SortContacts = SortContacts.BY_DATE,
    val folderUuid: UUID? = null,
    val hasMessages: Boolean = false,
    val excludeCurrentUserIfNoSearch: Boolean = true,
    val from: Int = 0,
    val count: Int = 0
)