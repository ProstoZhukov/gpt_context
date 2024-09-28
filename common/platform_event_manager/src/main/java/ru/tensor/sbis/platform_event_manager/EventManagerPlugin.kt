package ru.tensor.sbis.platform_event_manager

import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerProvider
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

object EventManagerPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(EventManagerProvider::class.java) { eventManagerComponent }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit

    internal val eventManagerComponent: EventManagerComponent by lazy {
        build()
    }

}