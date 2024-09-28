package ru.tensor.sbis.communicator.contacts_registry.di.list

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListContract
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListFragment
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.plugin_struct.feature.Feature
import javax.inject.Named

/**
 * DI компонент реестра контактов.
 *
 * @author da.zhukov
 */
@ContactListScope
@Component(
    dependencies = [CommunicatorCommonComponent::class, ContactsRegistryDependency::class],
    modules = [ContactListModule::class]
)
internal interface ContactListComponent : Feature {
    fun inject(fragment: ContactListFragment?)

    val presenter: ContactListContract.Presenter
    val contactsDependency: ContactsRegistryDependency
    val networkUtils: NetworkUtils
    val scrollHelper: ScrollHelper
    val contactsControllerWrapper: ContactsControllerWrapper
    val context: Context
    val listDateViewUpdater: ListDateViewUpdater
    val importContactsHelper: ImportContactsHelper.Provider?
    val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Builder
    interface Builder {
        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder
        fun contactsDependency(dependency: ContactsRegistryDependency): Builder
        @BindsInstance fun isViewHidden(@Named(IS_VIEW_HIDDEN_NAME) hidden: Boolean): Builder
        fun build(): ContactListComponent
    }
}