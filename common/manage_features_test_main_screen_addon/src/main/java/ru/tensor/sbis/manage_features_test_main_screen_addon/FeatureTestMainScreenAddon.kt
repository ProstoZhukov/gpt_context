package ru.tensor.sbis.manage_features_test_main_screen_addon

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.manage_features.domain.ManageFeaturesFeature
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Плагин раздела проверки функционала на главном экране
 *
 * @author kv.martyshenko
 */
class FeatureTestMainScreenAddon(
    private val manageFeaturesFeature: ManageFeaturesFeature,
    private val sbisFeatureServiceProvider: SbisFeatureServiceProvider,
    private val manageFeatureNavItem: NavigationItem = createDefaultManageFeaturesItem(),
    private val manageFeatureItemVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> =
        createDefaultManageFeatureItemVisibilitySourceProvider(sbisFeatureServiceProvider),
    private val fragmentFactory: () -> Fragment = {
        manageFeaturesFeature.getManageFeatureFragment()
    }
) : SimplifiedContentController(),
    MainScreenAddon {

    // region MainScreenAddon
    override fun setup(mainScreen: ConfigurableMainScreen) {
        mainScreen.addItem(
            manageFeatureNavItem, ConfigurableMainScreen.MenuItemConfiguration(
                visibilitySource = manageFeatureItemVisibilitySourceProvider(mainScreen)
            ), this
        )
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(manageFeatureNavItem)
    }
    // endregion

    // region ContainerController
    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        return ContentInfo(fragmentFactory())
    }
    // endregion

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val FEATURES_TEST_ITEM_IDENTIFIER = "FEATURE_TEST"

        @JvmStatic
        fun createDefaultManageFeaturesItem(): DefaultNavigationItem {
            return DefaultNavigationItem(
                navigationItemLabel = NavigationItemLabel(
                    default = RCommon.string.common_navigation_menu_item_feature_manage
                ),
                navigationItemIcon = NavigationItemIcon(
                    default = RDesign.string.design_nav_icon_setting,
                    selected = RDesign.string.design_nav_icon_setting_fill,
                ),
                ordinal = R.id.manage_features_test_main_screen_addon_item_id,
                persistentUniqueIdentifier = FEATURES_TEST_ITEM_IDENTIFIER,
                navxIdentifier = FEATURES_TEST_ITEM_IDENTIFIER
            )
        }

        @JvmStatic
        fun createDefaultManageFeatureItemVisibilitySourceProvider(
            sbisFeatureServiceProvider: SbisFeatureServiceProvider
        ): (ConfigurableMainScreen) -> LiveData<Boolean> = {
            FeatureTestAvailabilityChecker.getAndUpdateAvailability(sbisFeatureServiceProvider.sbisFeatureService)
        }
    }

}