package ru.tensor.sbis.main_screen_basic

import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.counters.CountersSubscriptionProvider
import ru.tensor.sbis.verification_decl.permission.PermissionFeature
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider

/**
 * Плагин компонента "Раскладка" (главный экран без ННП и аккордеона).
 *
 * @author us.bessonov
 */
object BasicMainScreenPlugin : BasePlugin<Unit>() {
    internal var navigationServiceProvider: FeatureProvider<NavigationService>? = null
    internal var permissionFeature: FeatureProvider<PermissionFeature>? = null
    internal var countersSubscriptionProvider: FeatureProvider<CountersSubscriptionProvider>? = null
    internal val startupPermissions: MutableSet<FeatureProvider<StartupPermissionProvider>> = mutableSetOf()

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .optional(NavigationService::class.java) { navigationServiceProvider = it }
        .optional(PermissionFeature::class.java) { permissionFeature = it }
        .optional(CountersSubscriptionProvider::class.java) { countersSubscriptionProvider = it }
        .optionalSet(StartupPermissionProvider::class.java) { if (it != null) startupPermissions.addAll(it) }
        .build()

    override val customizationOptions: Unit = Unit
}