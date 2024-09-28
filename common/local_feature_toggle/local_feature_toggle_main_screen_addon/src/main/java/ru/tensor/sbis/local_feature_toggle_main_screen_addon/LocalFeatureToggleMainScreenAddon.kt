package ru.tensor.sbis.local_feature_toggle_main_screen_addon

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.localfeaturetoggle.presentation.LocalFeatureToggleFragment
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.toolbox_decl.BuildConfig

/**
 * Плагин раздела проверки функциональности локальных фич
 *
 * @author mb.kruglova
 */
internal class LocalFeatureToggleMainScreenAddon(
    private val localFeatureToggleNavItem: NavigationItem = createDefaultLocalFeatureToggleItem(),
    private val visibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> =
        defaultVisibilitySourceProvider(),
    private val fragmentFactory: () -> Fragment = { LocalFeatureToggleFragment() }
) : SimplifiedContentController(),
    MainScreenAddon {

    // region MainScreenAddon
    override fun setup(mainScreen: ConfigurableMainScreen) {
        mainScreen.addItem(
            localFeatureToggleNavItem,
            ConfigurableMainScreen.MenuItemConfiguration(visibilitySource = visibilitySourceProvider(mainScreen)),
            this
        )
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(localFeatureToggleNavItem)
    }
    // endregion

    // region ContainerController
    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        return ContentInfo(fragmentFactory())
    }
    // endregion

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val LOCAL_FEATURE_TOGGLE_ITEM_IDENTIFIER = "LOCAL_FEATURE_TOGGLE"
        const val LOCAL_FEATURE_TOGGLE_NAVX_IDENTIFIER = "local_feature_toggle"

        @JvmStatic
        fun createDefaultLocalFeatureToggleItem(): DefaultNavigationItem {
            return DefaultNavigationItem(
                navigationItemLabel = NavigationItemLabel(
                    default = ru.tensor.sbis.common.R.string.common_navigation_menu_item_local_feature_toggle
                ),
                navigationItemIcon = NavigationItemIcon(
                    default = ru.tensor.sbis.design.R.string.design_nav_icon_setting,
                    selected = ru.tensor.sbis.design.R.string.design_nav_icon_setting_fill
                ),
                ordinal = R.id.local_feature_toggle_main_screen_addon_item_id,
                persistentUniqueIdentifier = LOCAL_FEATURE_TOGGLE_ITEM_IDENTIFIER,
                navxIdentifier = LOCAL_FEATURE_TOGGLE_NAVX_IDENTIFIER
            )
        }

        @JvmStatic
        fun defaultVisibilitySourceProvider(): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return { MutableLiveData(BuildConfig.DEBUG) }
        }
    }
}