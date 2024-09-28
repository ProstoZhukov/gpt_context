package ru.tensor.sbis.communicator.declaration.host_factory

import androidx.fragment.app.Fragment
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика хост фрагмента реестра контактов.
 *
 * @author da.zhukov
 */
interface ContactsRegistryHostFragmentFactory : Feature {

    /**
     * Создать фрагмент хост фрагмента реестра сотрудников.
     *
     * @param registryType тип реестра.
     */
    fun createContactsHostFragment(
        action: DeeplinkAction? = null
    ): Fragment
}