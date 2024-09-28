package ru.tensor.sbis.communicator.declaration.model

import java.io.Serializable

/**
 * Типы реестров модуля коммуникатор.
 *
 * @author vv.chekurda
 */
sealed class CommunicatorRegistryType : Serializable {

    /**
     * Реестр диалогов/каналов.
     */
    sealed class ThemeRegistryType : CommunicatorRegistryType()

    /**
     * Реестр диалогов.
     */
    data class DialogsRegistry(
        val dialogType: DialogType? = null,
        val tryRestoreTab: Boolean = false
    ) : ThemeRegistryType() {
        override fun isSupportsOverlayDetailContainer(isTablet: Boolean): Boolean =
            !isTablet
    }

    /**
     * Реестр чатов.
     */
    data class ChatsRegistry(val chatType: ChatType? = null) : ThemeRegistryType() {
        override fun isSupportsOverlayDetailContainer(isTablet: Boolean): Boolean =
            !isTablet
    }

    open fun isSupportsOverlayDetailContainer(isTablet: Boolean) = false
}