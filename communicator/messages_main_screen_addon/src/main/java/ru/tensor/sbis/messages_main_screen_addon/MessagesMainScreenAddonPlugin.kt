package ru.tensor.sbis.messages_main_screen_addon

import CommunicatorPushKeyboardHelper
import androidx.lifecycle.LiveData
import ru.tensor.sbis.communicator.declaration.counter.factory.CommunicatorCounterProviderFactory
import ru.tensor.sbis.communicator.declaration.host_factory.ThemesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.tab_history.ThemeTabHistory
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин аддона раздела сообщений на главном экране
 *
 * @author us.bessonov
 */
object MessagesMainScreenAddonPlugin : BasePlugin<MessagesMainScreenAddonPlugin.CustomizationOptions>() {

    /** @SelfDocumented */
    internal lateinit var themesRegistryHostFragmentFactoryProvider: FeatureProvider<ThemesRegistryHostFragmentFactory>

    /** @SelfDocumented */
    internal lateinit var communicatorCounterProviderFactoryProvider: FeatureProvider<CommunicatorCounterProviderFactory>

    /** @SelfDocumented */
    internal lateinit var communicatorPushKeyboardHelperProvider: FeatureProvider<CommunicatorPushKeyboardHelper.Provider>

    /** @SelfDocumented */
    internal lateinit var themeTabHistory: FeatureProvider<ThemeTabHistory>

    override val api: Set<FeatureWrapper<out Feature>> = mutableSetOf<FeatureWrapper<out Feature>>().apply {
        val entries = MessagesMainScreenEntry.createDefault()
        for (entry in entries) add(FeatureWrapper(MainScreenEntry::class.java) { entry })
    }

    override val customizationOptions = CustomizationOptions()

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ThemesRegistryHostFragmentFactory::class.java) { themesRegistryHostFragmentFactoryProvider = it }
            .require(CommunicatorCounterProviderFactory::class.java) { communicatorCounterProviderFactoryProvider = it }
            .require(CommunicatorPushKeyboardHelper.Provider::class.java) { communicatorPushKeyboardHelperProvider = it }
            .require(ThemeTabHistory::class.java) { themeTabHistory = it }
            .build()
    }

    /**
     * Опции плагина модуля аддона реестров диалогов и чатов.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Идентификатор navx, который необходимо использовать для регистрации адона.
         */
        var navxIdentifier = "communicator"
    }
}

/**
 * Создать аддон раздела сообщений на главном экране
 */
fun MessagesMainScreenAddonPlugin.createAddon(
    messagesNavItem: NavigationItem =
        MessagesMainScreenAddon.createDefaultMessagesItem(),
    visibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> = MessagesMainScreenAddon.defaultVisibilitySourceProvider()
): MainScreenAddon = MessagesMainScreenAddon(
    messagesNavItem,
    visibilitySourceProvider,
    themesRegistryHostFragmentFactoryProvider.get()
)