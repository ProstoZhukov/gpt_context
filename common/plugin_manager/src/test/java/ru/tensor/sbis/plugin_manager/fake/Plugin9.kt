package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin9 : BasePlugin<Unit>() {

    internal lateinit var testFeature3: TestFeature3
    internal lateinit var testFeature1: FeatureProvider<TestFeature1>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature3::class.java) {
            testFeature3
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(TestFeature1::class.java) { testFeature1 = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        testFeature3 = object : TestFeature3 {
            init {
                testFeature1.get()
            }
        }
        testFeature1.get()
    }

}