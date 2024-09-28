package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts

import io.reactivex.Observable
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactsInteractor
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Интерактор добавления нового контакта в реестр контактов.
 * @see AddContactsInteractor
 *
 *
 * @author da.zhukov
 */
interface AddNewContactsInteractor : AddContactsInteractor {
    fun searchNewContacts(
        nameQuery: String,
        phoneQuery: String,
        emailQuery: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>>
}