package ru.tensor.sbis.communicator.contacts_declaration.model.result

import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile

/**
 * Модель результата запросов списка конткатов
 *
 * @author vv.chekurda
 */
data class ContactListResult(
    val contacts: List<ContactProfile>,
    val hasMore: Boolean = false,
    val metadata: HashMap<String, String>? = null
)