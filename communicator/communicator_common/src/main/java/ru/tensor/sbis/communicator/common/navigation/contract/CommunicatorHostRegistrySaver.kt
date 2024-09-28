package ru.tensor.sbis.communicator.common.navigation.contract

import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType

/**
 * Интерфейс сохранения текущего фрагмента для хоста при кешировании реестров
 * @author da.zhukov
 */
interface CommunicatorHostRegistrySaver {
    /**
     * Сохранить реестр
     *
     * @param registryType тип реестра, который нужно сохранить в хосте
     */
    fun saveRegistry(registryType: CommunicatorRegistryType)
}