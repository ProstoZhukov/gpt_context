package ru.tensor.sbis.communicator.contacts_registry

import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryFeature
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListFragment

/**
 * Фасад модуля реестра контактов.
 * Предоставляет фичи [ContactsRegistryFeature] и зависимости [ContactsRegistryDependency] модуля.
 *
 * @author vv.chekurda
 */
internal object ContactsRegistryFeatureFacade : ContactsRegistryFeature,
    ContactsRegistryDependency.Provider,
    ContactsRegistryFragmentFactory by ContactListFragment.Companion {

    val importContactsFeatureEnabled
        get() = contactsDependency.importContactsHelperProvider != null

    override lateinit var contactsDependency: ContactsRegistryDependency

    fun configure(dependency: ContactsRegistryDependency) {
        contactsDependency = dependency
    }
}