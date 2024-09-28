package ru.tensor.sbis.application_tools

import ru.tensor.sbis.events_tracker.EventsTracker
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.settings_screen_decl.AppToolsSettingsFragmentProvider
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsLaunchStatusProvider
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsParametersProvider

/**
 * Плагин с базовыми настройками для приложения
 *
 * @author kv.martyshenko
 */
object AppToolsPlugin : BasePlugin<Unit>() {
    private lateinit var eventsTracker: FeatureProvider<EventsTracker>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(AppToolsSettingsFragmentProvider::class.java) { AppToolsSettingsFragmentProviderImpl() },
        FeatureWrapper(AutotestsLaunchStatusProvider::class.java) { AutotestLaunchConfigurationHolder },
        FeatureWrapper(AutotestsParametersProvider::class.java) { AutotestLaunchConfigurationHolder }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(EventsTracker::class.java) { eventsTracker = it }
        .build()

    override val customizationOptions: Unit = Unit
}