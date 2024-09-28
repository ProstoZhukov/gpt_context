package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin4 : BasePlugin<Unit>() {

    internal lateinit var feature1Provider: Set<FeatureProvider<TestFeature1>>

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .requireSet(TestFeature1::class.java) { feature1Provider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        feature1Provider.first().get()
    }

}