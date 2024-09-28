package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.result.EmployeeListResult
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactsInteractorProdImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper.AddContactModelMapper
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Реализация интерактора добавления сотрудников внутри компании в реестре контактов.
 * @see AddInternalEmployeesInteractor
 *
 *
 * @author da.zhukov
 */
@WorkerThread
internal class AddInternalEmployeesInteractorProdImpl(
    contactsControllerWrapper: ContactsControllerWrapper,
    addContactModelMapper: AddContactModelMapper,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : AddContactsInteractorProdImpl(contactsControllerWrapper, addContactModelMapper), AddInternalEmployeesInteractor {
    override fun searchInternalEmployees(
        searchString: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>> {
        return Observable.fromCallable {
            mContactsControllerWrapper.searchInternalEmployees(
                searchString,
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

    override fun loadInternalEmployeesPage(
        searchString: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>> {
        return Observable.fromCallable {
            mContactsControllerWrapper.loadInternalEmployeesPage(
                searchString,
                from,
                count
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