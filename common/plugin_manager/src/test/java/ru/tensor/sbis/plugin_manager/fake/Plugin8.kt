package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin8 : BasePlugin<Unit>() {

    internal lateinit var testFeature2: TestFeature2

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature2::class.java) {
            testFeature2
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        testFeature2 = object : TestFeature2 {}
    }

}