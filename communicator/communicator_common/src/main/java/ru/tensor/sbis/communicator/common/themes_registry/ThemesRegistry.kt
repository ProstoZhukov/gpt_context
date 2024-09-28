package ru.tensor.sbis.communicator.common.themes_registry

import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.*

/**
 * Интерфейс реестров диалогов и чатов
 * @author da.zhukov
 */
interface ThemesRegistry : RegistryDeeplinkActionNode {

    /**
     * Сменить реестр внутри фрагмента
     * @param registryType тип реестра
     */
    fun changeThemesRegistry(registryType: ThemeRegistryType)
}

const val DIALOG_TYPE_ID = "Dialog type"
const val CHAT_TYPE_ID = "Chat type"