package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_registry.di.list.ContactListComponent
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesContract
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesFragment
import java.util.UUID

/**
 * DI компонент добавления сотрудников внутри компании в реестр контактов.
 *
 * @author da.zhukov
 */
@AddInternalEmployeesScope
@Component(dependencies = [ContactListComponent::class], modules = [AddInternalEmployeesModule::class])
internal interface AddInternalEmployeesComponent {
    fun inject(fragment: AddInternalEmployeesFragment)
    val addInternalEmployeesPresenter: AddInternalEmployeesContract.Presenter
    val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun folderUuid(folderUuid: UUID?): Builder
        fun contactListComponent(contactListComponent: ContactListComponent): Builder
        fun build(): AddInternalEmployeesComponent
    }
}