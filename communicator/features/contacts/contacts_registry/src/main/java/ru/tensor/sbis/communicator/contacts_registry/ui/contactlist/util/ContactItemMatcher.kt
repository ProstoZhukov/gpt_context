package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.util

import ru.tensor.sbis.base_components.autoscroll.BaseAutoScroller
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactFoldersModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsStubModel

/**
 * Реализация сравнения элементов списка реестра контактов.
 *
 * @author vv.chekurda
 */
internal class ContactItemMatcher : BaseAutoScroller.Matcher {

    override fun areItemsTheSame(item1: Any?, item2: Any?): Boolean =
        (item1 is ContactsModel && item2 is ContactsModel && item1.contact.uuid == item2.contact.uuid)
                || (item1 is ContactFoldersModel && item2 is ContactFoldersModel)
                || (item1 is ContactsStubModel && item2 is ContactsStubModel)
}