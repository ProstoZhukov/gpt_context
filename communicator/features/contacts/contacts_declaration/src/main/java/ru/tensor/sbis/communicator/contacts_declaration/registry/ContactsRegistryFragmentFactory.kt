package ru.tensor.sbis.communicator.contacts_declaration.registry

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания фрагмента реестра контактов
 *
 * @author vv.chekurda
 */
interface ContactsRegistryFragmentFactory : Feature {

    /**
     * Создать фрагмент реестра контактов.
     */
    fun createContactsRegistryFragment(mode: ContactsRegistryMode = ContactsRegistryMode.REGISTRY): Fragment
}

enum class ContactsRegistryMode {
    REGISTRY,
    CONTENT
}