/**
 * Мапперы контроллеровских моделей контактов в UI
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.communicator.common.contacts

import ru.tensor.sbis.common.util.map
import ru.tensor.sbis.communicator.common.data.mapper.asNative
import ru.tensor.sbis.communicator.contacts_declaration.model.ContactsDataRefreshCallback
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.model.ContactProfileModel
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.ContactListFilter
import ru.tensor.sbis.communicator.generated.ContactFilter as CppContactFilter
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.SortContacts
import ru.tensor.sbis.communicator.contacts_declaration.model.result.ContactListResult
import ru.tensor.sbis.communicator.contacts_declaration.model.result.EmployeeListResult
import ru.tensor.sbis.communicator.contacts_declaration.model.result.GetStatus
import ru.tensor.sbis.communicator.generated.DataRefreshedContactsControllerCallback
import ru.tensor.sbis.communicator.generated.NotContactsResult
import ru.tensor.sbis.communicator.generated.GetStatus as CppGetStatus
import java.util.HashMap
import ru.tensor.sbis.communicator.generated.Contact as CppContactModel
import ru.tensor.sbis.communicator.generated.ListResultOfContactMapOfStringString as CppContactListResult
import ru.tensor.sbis.communicator.generated.SortContacts as CppSortContacts
// region Native -> Cpp

/**
 * Маппер UI колбэка в модель контроллера контактов
 */
internal val ContactsDataRefreshCallback.asContactsCallback: DataRefreshedContactsControllerCallback
    get() = object : DataRefreshedContactsControllerCallback() {
        override fun onEvent(param: HashMap<String, String>) {
            param.let(::execute)
        }
    }

/**
 * Маппер UI фильтра сортировки контактов в модель контроллера
 */
internal val SortContacts.asController: CppSortContacts
    get() = if (this == SortContacts.BY_DATE) {
        CppSortContacts.BY_DATE
    } else {
        CppSortContacts.BY_NAME
    }

/**
 * Маппер UI фильтра списка контактов в модель контроллера
 */
internal val ContactListFilter.asController: CppContactFilter
    get() = CppContactFilter(
        folderUuid,
        searchQuery,
        sort.asController,
        hasMessages,
        excludeCurrentUserIfNoSearch,
        from,
        count
    )

// region Cpp -> Native

/**
 * Маппер результата списка контактов контроллера в UI модель
 */
internal val CppContactListResult.asNative: ContactListResult
    get() = ContactListResult(
        result.map { it.asNative },
        haveMore,
        metadata
    )

/**
 * Маппер результата списка профилей контроллера в UI модель
 */
internal val NotContactsResult.asNative: EmployeeListResult
    get() = EmployeeListResult(
        employees = result.map { it.profile.asNative },
        hasMore = haveMore,
        nameHighlight = result.map { it.nameHighlight }
    )

/**
 * Маппер модели контакта контроллера в UI модель
 */
internal val CppContactModel.asNative: ContactProfile
    get() = ContactProfileModel(
        employee = profile.asNative,
        isInMyContacts = isContact,
        lastMessageDate = lastMessageDate,
        isMyAccountManager = isAccountManager,
        nameHighlight = nameHighlight,
        folderUuid = folder,
        comment = comment,
        changesIsForbidden = changesIsForbidden
    )

/**
 * Маппер enum статуса активности контроллера в UI enum
 */
@Suppress("UNUSED")
internal val CppGetStatus.asNative: GetStatus
    get() = when (this) {
        CppGetStatus.SUCCES_LOCAL_CACHE -> GetStatus.SUCCESS_LOCAL_CACHE
        CppGetStatus.SUCCES_CLOUD       -> GetStatus.SUCCESS_CLOUD
        CppGetStatus.DB_ERROR           -> GetStatus.DB_ERROR
        CppGetStatus.CLOUD_ERROR        -> GetStatus.CLOUD_ERROR
    }