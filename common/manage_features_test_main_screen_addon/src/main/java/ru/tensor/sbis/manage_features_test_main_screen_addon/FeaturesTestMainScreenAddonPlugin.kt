package ru.tensor.sbis.manage_features_test_main_screen_addon

import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.manage_features.domain.ManageFeaturesFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин аддона
 */
object FeaturesTestMainScreenAddonPlugin : BasePlugin<Unit>() {

    internal lateinit var manageFeaturesFeatureProvider: FeatureProvider<ManageFeaturesFeature>
    internal lateinit var sbisFeatureServiceProvider: FeatureProvider<SbisFeatureServiceProvider>
    internal lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface>

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val customizationOptions = Unit

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ManageFeaturesFeature::class.java) { manageFeaturesFeatureProvider = it }
            .require(SbisFeatureServiceProvider::class.java) { sbisFeatureServiceProvider = it }
            .require(LoginInterface::class.java) { loginInterfaceProvider = it }
            .build()
    }
}

/**
 * Создать аддон раздела базы знаний на главном экране
 */
fun FeaturesTestMainScreenAddonPlugin.createAddon(
    manageFeatureNavItem: NavigationItem = FeatureTestMainScreenAddon.createDefaultManageFeaturesItem(),
): MainScreenAddon = FeatureTestMainScreenAddon(
    manageFeaturesFeature = manageFeaturesFeatureProvider.get(),
    sbisFeatureServiceProvider = sbisFeatureServiceProvider.get(),
    manageFeatureNavItem = manageFeatureNavItem
)