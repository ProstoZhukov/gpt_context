package ru.tensor.sbis.communicator.common.contacts

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.ContactsDataRefreshCallback
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.ContactListFilter
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.ImportContactData
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.mapToPhoneBookCard
import ru.tensor.sbis.communicator.contacts_declaration.model.result.ContactListResult
import ru.tensor.sbis.communicator.contacts_declaration.model.result.EmployeeListResult
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.communicator.generated.ContactsController
import ru.tensor.sbis.communicator.generated.NotContactFilter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Реализация обертки контроллера контактов
 *
 * @property contactsControllerProvider поставщик контроллера контактов
 *
 * @author vv.chekurda
 */
internal class ContactsControllerWrapperImpl(
    private val contactsControllerProvider: DependencyProvider<ContactsController>,
) : ContactsControllerWrapper {

    private val contactsController: ContactsController
        get() = contactsControllerProvider.get()


    /**
     * Это число необходимо для работоспособности метода контроллера(работает на ios = 100)
     * Подробности в обсуждении ошибки https://online.sbis.ru/opendoc.html?guid=8e9ddea2-a416-468b-b63a-142c3845411b
     */
    private val contactsCount = 100

    override fun list(filter: ContactListFilter): ContactListResult =
        contactsController.list(filter.asController).asNative

    override fun refresh(filter: ContactListFilter): ContactListResult =
        contactsController.refresh(filter.asController).asNative

    override fun setDataRefreshCallback(callback: ContactsDataRefreshCallback): Subscription =
        contactsController.dataRefreshed().subscribe(callback.asContactsCallback)

    override fun addContact(contactUuid: UUID, groupUuid: UUID?): CommandStatus =
        contactsController.addContact(contactUuid, groupUuid)

    override fun moveContact(contactUuid: UUID, groupUuid: UUID?, newGroupUuid: UUID?): CommandStatus =
        contactsController.moveContact(contactUuid, groupUuid, newGroupUuid)

    override fun moveContacts(contactUuids: ArrayList<UUID>, groupUuid: UUID?, newGroupUuid: UUID?): CommandStatus =
        contactsController.moveContacts(contactUuids, groupUuid, newGroupUuid)

    override fun removeContact(contactUuid: UUID, groupUuid: UUID?): CommandStatus =
        contactsController.removeContact(contactUuid, groupUuid)

    override fun removeContacts(contactUuids: ArrayList<UUID>, groupUuid: UUID?): CommandStatus =
        contactsController.removeContacts(contactUuids, groupUuid)

    override fun blockContacts(contactUuids: ArrayList<UUID>): CommandStatus =
        contactsController.moveContacts(contactUuids, null, UUIDUtils.fromString(BlACK_LIST_FOLDER_UUID))

    override fun importContactsByPhones(phones: ArrayList<String>): CommandStatus =
        contactsController.importContactsByPhones(phones)

    override fun canAddNewContacts(): Boolean =
        contactsController.canAddNewContacts()

    override fun searchNewContacts(
        name: String?,
        phone: String?,
        email: String?,
        from: Int,
        count: Int
    ): EmployeeListResult =
        contactsController.searchNotContacts(NotContactFilter(name, email, phone, false, from, count)).asNative

    override fun searchInternalEmployees(searchQuery: String?, from: Int, count: Int): EmployeeListResult =
        contactsController.searchNotContacts(NotContactFilter(searchQuery, null, null, true, from, contactsCount)).asNative

    override fun loadInternalEmployeesPage(searchQuery: String?, from: Int, count: Int): EmployeeListResult =
        contactsController.searchNotContacts(NotContactFilter(searchQuery, null, null, true, from, count)).asNative

    override fun importPhoneBook(contacts: ArrayList<ImportContactData>): CommandStatus {
        return contactsController.importPhoneBookCards(contacts.map { it.mapToPhoneBookCard() }.asArrayList())
    }

    override fun cancelContactsControllerSynchronizations() {
        contactsController.cancelAll()
    }
}

// TODO Переехать на метод blockContacts, не дождался в хф
private const val BlACK_LIST_FOLDER_UUID = "eb1bfd2e-bf6d-415d-8c2f-563e251dc8ab"