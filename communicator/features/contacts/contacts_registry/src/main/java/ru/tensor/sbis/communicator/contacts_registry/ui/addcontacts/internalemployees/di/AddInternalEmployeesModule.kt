package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.di.Production
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.adapter.AddContactListAdapter
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesContract
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesInteractor
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesInteractorProdImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesPresenter
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper.AddContactModelMapper
import java.util.UUID

/**
 * DI модуль добавления сотрудников внутри компании в реестр контактов.
 *
 * @author da.zhukov
 */
@Module
internal class AddInternalEmployeesModule {
    @Provides
    @AddInternalEmployeesScope
    fun provideAddInternalEmployeesPresenter(
        @Production interactor: AddInternalEmployeesInteractor,
        networkUtils: NetworkUtils,
        folderUuid: UUID?,
        scrollHelper: ScrollHelper
    ): AddInternalEmployeesContract.Presenter {
        return AddInternalEmployeesPresenter(
            interactor,
            networkUtils,
            folderUuid,
            scrollHelper
        )
    }

    @Provides
    @AddInternalEmployeesScope
    fun provideAddContactListAdapter(): AddContactListAdapter {
        return AddContactListAdapter()
    }

    //region Interactors
    @Production
    @Provides
    @AddInternalEmployeesScope
    fun provideAddContactsInteractor(
        contactsControllerWrapper: ContactsControllerWrapper,
        addContactModelMapper: AddContactModelMapper,
        subscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): AddInternalEmployeesInteractor {
        return AddInternalEmployeesInteractorProdImpl(
            contactsControllerWrapper,
            addContactModelMapper,
            subscriptionsInitializer
        )
    }

    @Provides
    @AddInternalEmployeesScope
    fun providerAddContactModelMapper(resourceProvider: ResourceProvider): AddContactModelMapper {
        return AddContactModelMapper(resourceProvider)
    }
}