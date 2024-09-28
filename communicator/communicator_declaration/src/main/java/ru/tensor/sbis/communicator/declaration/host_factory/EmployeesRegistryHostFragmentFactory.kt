package ru.tensor.sbis.communicator.declaration.host_factory

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика хост фрагмента реестра сотрудников.
 *
 * @author da.zhukov
 */
interface EmployeesRegistryHostFragmentFactory : Feature {

    /**
     * Создать фрагмент хост фрагмента реестра сотрудников.
     *
     * @param registryType тип реестра.
     */
    fun createEmployeesHostFragment(): Fragment
}