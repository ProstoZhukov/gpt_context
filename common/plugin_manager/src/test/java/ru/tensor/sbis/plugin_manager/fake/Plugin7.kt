package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin7 : BasePlugin<Unit>() {

    internal lateinit var testFeature1: FeatureProvider<TestFeature1>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature2::class.java) {
            object : TestFeature2 {
                init {
                    testFeature1.get()
                }
            }
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(TestFeature1::class.java) { testFeature1 = it }
        .build()

    override val customizationOptions: Unit = Unit

}