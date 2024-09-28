package ru.tensor.sbis.communicator.contacts_declaration.controller

import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.contacts_declaration.model.result.ContactListResult
import ru.tensor.sbis.communicator.contacts_declaration.model.ContactsDataRefreshCallback
import ru.tensor.sbis.communicator.contacts_declaration.model.result.EmployeeListResult
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.ContactListFilter
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.ImportContactData
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*
import kotlin.collections.ArrayList

/**
 * UI обертка контроллера реестра контактов
 *
 * @author vv.chekurda
 */
interface ContactsControllerWrapper {

    /**
     * Выборка записей по фильтру, возможно с запросом в облако.
     * Данный метод предназначен для первичного набора данных, отображаемых в реестре.
     * Для перенабора данных должен использоваться метод Refresh.
     *
     * @param filter фильтр выборки
     * @return набор данных выборки в виде структуры ListResult<Model>, плюс признак наличия ещё данных (mHasMore)
     */
    fun list(filter: ContactListFilter): ContactListResult

    /**
     * Выборка записей по фильтру f без запроса в облако
     *
     * @param filter фильтр выборки
     * @return набор данных выборки в виде структуры ListResult<Model>, плюс признак наличия ещё данных (mHasMore)
     */
    fun refresh(filter: ContactListFilter): ContactListResult

    /**
     * Зарегистрировать коллбэк
     *
     * @param callback колбэк [ContactsDataRefreshCallback]
     * @return подписка для управления
     */
    fun setDataRefreshCallback(callback: ContactsDataRefreshCallback): Subscription

    /**
     * Добавление профиля в список контактов.
     * Метод вызывается синхронно на облаке, потом синхронно обновляется структура папок контактов.
     * Может вернуть CONTACT_ALREADY_IN_FOLDER, OTHER_ERROR, NETWORK_ERROR, CONTACT_ALREADY_IN_ANOTHER_FOLDER.
     * При успехе рассылает сообщение PROFILES_UPDATED(по старой шине) и "ContactSyncEvent"
     *
     * @param contactUuid идентификатор персоны
     * @param groupUuid   идентификатор папки для добавления, если не указан то имееться в виду корневая папка.
     * @return статус операции
     */
    fun addContact(contactUuid: UUID, groupUuid: UUID?): CommandStatus

    /**
     * Перенос контакта (одного или нескольких) в директорию.
     * Метод вызывается синхронно на облаке, потом может вернуть CONTACT_ALREADY_IN_FOLDER.
     * В любом случае рассылает сообщение PROFILES_UPDATED(по старой шине) и "ContactSyncEvent".
     *
     * @param contactUuid  идентификатор персоны
     * @param groupUuid    старый идентификатор папки, если не указан то имееться в виду корневая папка.
     * @param newGroupUuid идентификатор папки для перемещения, если не указан то имееться в виду корневая папка.
     * @return статус операции
     */
    fun moveContact(contactUuid: UUID, groupUuid: UUID?, newGroupUuid: UUID?): CommandStatus

    /**
     * Перенос списка контактов в директорию.
     * @see moveContact
     *
     * @param contactUuids список идентификаторов персон
     * @return статус операции
     */
    fun moveContacts(contactUuids: ArrayList<UUID>, groupUuid: UUID?, newGroupUuid: UUID?): CommandStatus

    /**
     * Удаление персоны из списка контактов.
     * Метод вызывается синхронно на облаке и в кэше, потом синхронно обновляется структура папок контактов.
     *
     * @param contactUuid - идентификатор профиля
     * @param groupUuid - идентификатор папки, если не указан то имееться в виду корневая папка.
     * @return статус операции
     */
    fun removeContact(contactUuid: UUID, groupUuid: UUID?): CommandStatus

    /**
     * Удаление списка персон из списка контактов.
     * @see removeContact
     *
     * @param contactUuids список идентификаторов персон
     * @return статус операции
     */
    fun removeContacts(contactUuids: ArrayList<UUID>, groupUuid: UUID?): CommandStatus

    /**
     * Заблокировать список контактов.
     *
     * @param contactUuids список идентификаторов персон.
     * @return статус операции
     */
    fun blockContacts(contactUuids: ArrayList<UUID>): CommandStatus

    /**
     * Импортирование контактов по номерам телефонов [ из телефонной книжки, например ].
     * Вызывается синхронно на облаке, при успехе запускает синхронизацию контактов.
     *
     * @param phones список строковых литералов с телефонными номерами.
     * @return статус операции
     */
    fun importContactsByPhones(phones: ArrayList<String>): CommandStatus

    /**
     * Проверяем можем ли мы добавлять новые контакты/демо-режим или нет
     */
    fun canAddNewContacts(): Boolean

    /**
     * Поиск профилей не в своей компании через облако с помощью метода БЛ Персона.НайтиКонтакт
     *
     * name - строка поиска по имени, фамилии либо отчеству
     * phone - строка поиска по телефону
     * email - строка поиска по телефону электронной почте
     * from - начала диапазона выборки
     * count - количество запращиваемых элементов
     */
    fun searchNewContacts(name: String?, phone: String?, email: String?, from: Int, count: Int): EmployeeListResult

    /**
     * Поиск профилей в своей компании через облако
     * с помощью методов БЛ "Персонал.СписокПерсонала"и "ProfileServiceMobile.PersonList"
     *
     * @param searchQuery строка поиска по имени, фамилии либо отчеству
     * @param from        начала диапазона выборки
     * @param count       количество запращиваемых элементов
     * @return набор данных выборки в виде структуры ListResult<Model>, плюс признак наличия ещё данных (mHasMore)
     */
    fun searchInternalEmployees(searchQuery: String?, from: Int, count: Int): EmployeeListResult

    /**
     * Поиск профилей в своей компании [searchInternalEmployees], но с другим типом навигации.
     * Поскольку происходит дополнительная фильтрация на стороне облака,
     * может вернуться результатов меньше, чем передали в count.
     * Инкрементировать page_number нужно при каждом следующем запросе (если предыдущий вернул has_more = true)
     *
     * @param searchQuery строка поиска по имени, фамилии либо отчеству
     * @param from        с позиции элемента
     * @param count       количество запращиваемых элементов
     * @return набор данных выборки в виде структуры ListResult<Model>, плюс признак наличия ещё данных (mHasMore)
     */
    fun loadInternalEmployeesPage(searchQuery: String?, from: Int, count: Int): EmployeeListResult

    /**
     * Импортировать телефонную книгу, сгрупированную по телефонам контактов [[111], [123, 456],...,[333, 444, 555]]
     */
    fun importPhoneBook(contacts: ArrayList<ImportContactData>): CommandStatus

    /**
     * Отменить асинхронные операций контроллера контактов, которые были вызваны CRUD методом list().
     */
    fun cancelContactsControllerSynchronizations()

    /**
     * Поставщик UI обертки контроллера
     */
    interface Provider : Feature {

        /**
         * @return поставщик зависимости UI обертки контроллера
         */
        val contactsControllerWrapper: ContactsControllerWrapper
    }
}