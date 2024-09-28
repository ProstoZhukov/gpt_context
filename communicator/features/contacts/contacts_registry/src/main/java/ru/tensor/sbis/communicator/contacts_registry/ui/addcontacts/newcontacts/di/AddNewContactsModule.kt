package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.di.Production
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.adapter.AddContactListAdapter
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper.AddContactModelMapper
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsContract
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsInteractor
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsInteractorProdImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsPresenter
import java.util.UUID

/**
 * DI модуль добавления контакта в реестр контактов.
 *
 * @author da.zhukov
 */
@Module
internal class AddNewContactsModule {
    @Provides
    @AddNewContactsScope
    fun provideAddNewContactsPresenter(
        @Production interactor: AddNewContactsInteractor,
        networkUtils: NetworkUtils,
        folderUuid: UUID?,
    ): AddNewContactsContract.Presenter {
        return AddNewContactsPresenter(interactor, networkUtils, folderUuid)
    }

    @Provides
    @AddNewContactsScope
    fun provideAddContactListAdapter(): AddContactListAdapter = AddContactListAdapter()

    @Production
    @Provides
    @AddNewContactsScope
    fun provideAddNewContactsInteractor(
        contactsControllerWrapper: ContactsControllerWrapper,
        addContactModelMapper: AddContactModelMapper,
        subscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer
    ): AddNewContactsInteractor =
        AddNewContactsInteractorProdImpl(contactsControllerWrapper, addContactModelMapper, subscriptionsInitializer)

    @Provides
    @AddNewContactsScope
    fun providerAddContactModelMapper(resourceProvider: ResourceProvider): AddContactModelMapper =
        AddContactModelMapper(resourceProvider)
}