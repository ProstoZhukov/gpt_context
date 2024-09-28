package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin10 : BasePlugin<Unit>() {

    internal lateinit var testFeature4: TestFeature4
    internal lateinit var testFeature3: FeatureProvider<TestFeature3>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature4::class.java) {
            testFeature4
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(TestFeature3::class.java) { testFeature3 = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        testFeature4 = object : TestFeature4 {
            init {
                testFeature3.get()
            }
        }
        testFeature3.get()
    }

}