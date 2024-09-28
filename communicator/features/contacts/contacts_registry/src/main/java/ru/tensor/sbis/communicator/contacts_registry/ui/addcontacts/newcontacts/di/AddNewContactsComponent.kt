package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_registry.di.list.ContactListComponent
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsContract
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsFragment
import java.util.UUID

/**
 * DI компонент добавления контакта в реестр контактов.
 *
 * @author da.zhukov
 */
@AddNewContactsScope
@Component(dependencies = [ContactListComponent::class], modules = [AddNewContactsModule::class])
internal interface AddNewContactsComponent {
    fun inject(fragment: AddNewContactsFragment)
    val addNewContactsPresenter: AddNewContactsContract.Presenter
    val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun folderUuid(folderUuid: UUID?): Builder
        fun contactListComponent(contactListComponent: ContactListComponent): Builder
        fun build(): AddNewContactsComponent
    }
}