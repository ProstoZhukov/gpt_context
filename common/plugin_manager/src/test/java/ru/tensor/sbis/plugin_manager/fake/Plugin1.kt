package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin1 : BasePlugin<Unit>() {
    internal val testFeature1 = object : TestFeature1 {}

    internal var feature2Provider: FeatureProvider<TestFeature2>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature1::class.java) {
            testFeature1
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .optional(TestFeature2::class.java) { feature2Provider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        feature2Provider?.get()
    }

}