package ru.tensor.sbis.dashboard_builder

import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenProvider
import ru.tensor.sbis.widget_player.contract.WidgetPlayerStoreInitializer

/**
 * Плагин для конструктора дашбордов.
 *
 * @author am.boldinov
 */
object DashboardPlugin : BasePlugin<Unit>() {

    private val dashboardFeature = DashboardFeatureImpl()

    private lateinit var navigationServiceFeatureProvider: FeatureProvider<NavigationService>

    internal val navigationService get() = navigationServiceFeatureProvider.get()

    override val api = setOf(
        FeatureWrapper(WidgetPlayerStoreInitializer::class.java) { DashboardWidgetDefaultInitializer() },
        FeatureWrapper(DashboardScreenProvider::class.java) { dashboardFeature }
    )

    override val dependency = Dependency.Builder()
        .require(NavigationService::class.java) { navigationServiceFeatureProvider = it }
        .build()

    override val customizationOptions = Unit
}