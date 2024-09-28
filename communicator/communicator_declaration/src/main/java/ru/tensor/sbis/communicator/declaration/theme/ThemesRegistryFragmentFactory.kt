package ru.tensor.sbis.communicator.declaration.theme

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.*
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента реестров диалогов и чатов.
 *
 * @author da.zhukov
 */
interface ThemesRegistryFragmentFactory : Feature {

    /**
     * Создать фрагмент реестра чатов или диалогов.
     * @return [Fragment] фрагмент реестра чатов или диалогов.
     */
    fun createThemeFragment(type: CommunicatorRegistryType? = null): Fragment

    /**
     * Создать фрагмент реестра чатов или диалогов для шаринга.
     * @return [Fragment] фрагмент реестра чатов или диалогов для шаринга.
     */
    fun createShareThemeFragment(type: ThemeRegistryType): Fragment
}