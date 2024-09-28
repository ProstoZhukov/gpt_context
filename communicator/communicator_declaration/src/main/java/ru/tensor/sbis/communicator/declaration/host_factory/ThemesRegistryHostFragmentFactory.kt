package ru.tensor.sbis.communicator.declaration.host_factory

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика хост фрагмента реестра диалогов/каналов.
 *
 * @author da.zhukov
 */
interface ThemesRegistryHostFragmentFactory : Feature {

    /**
     * Создать фрагмент хост фрагмента реестра диалогов/каналов.
     *
     * @param registryType тип реестра диалоги или каналы.
     */
    fun createThemeHostFragment(
        registryType: CommunicatorRegistryType = CommunicatorRegistryType.DialogsRegistry(),
        action: DeeplinkAction? = null
    ): Fragment
}