package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts

import io.reactivex.Observable
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.result.EmployeeListResult
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactsInteractorProdImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper.AddContactModelMapper
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Реализация интерактора добавления нового контакта в реестр контактов.
 * @see AddNewContactsInteractor
 *
 *
 * @author da.zhukov
 */
internal class AddNewContactsInteractorProdImpl(
    contactsControllerWrapper: ContactsControllerWrapper,
    addContactModelMapper: AddContactModelMapper,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : AddContactsInteractorProdImpl(contactsControllerWrapper, addContactModelMapper), AddNewContactsInteractor {
    override fun searchNewContacts(
        nameQuery: String,
        phoneQuery: String,
        emailQuery: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>> {
        return Observable.fromCallable {
            mContactsControllerWrapper.searchNewContacts(
                nameQuery,
                phoneQuery,
                emailQuery,
                from,
                count
            )
        }
            .doOnNext {
                activityStatusSubscriptionsInitializer.initialize(
                    it.employees.map { profile ->
                        profile.uuid
                    }
                )
            }
            .compose(getObservableComputationScheduler())
            .map { (employees, hasMore, nameHighlight): EmployeeListResult ->
                PagedListResult(
                    mModelMapper.apply(employees, nameHighlight),
                    hasMore
                )
            }
            .compose(getObservableBackgroundSchedulers())
    }
}