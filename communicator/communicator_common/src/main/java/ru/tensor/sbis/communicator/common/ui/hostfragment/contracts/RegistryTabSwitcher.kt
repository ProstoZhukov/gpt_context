package ru.tensor.sbis.communicator.common.ui.hostfragment.contracts

import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType

/**
 * Переключатель реестров через вкладки
 *
 * @author vv.chekurda
 */
interface RegistryTabSwitcher {

    /**
     * Переключить вкладку
     *
     * @param registryType тип реестра, на который необходимо переключиться
     */
    fun switchTab(registryType: CommunicatorRegistryType)
}