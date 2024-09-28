package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin6 : BasePlugin<Unit>() {

    internal lateinit var testFeature1: TestFeature1
    internal lateinit var testFeature2: FeatureProvider<TestFeature2>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature1::class.java) {
            testFeature1
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(TestFeature2::class.java) { testFeature2 = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        testFeature1 = object : TestFeature1 {
            init {
                testFeature2.get()
            }
        }
        testFeature2.get()
    }

}