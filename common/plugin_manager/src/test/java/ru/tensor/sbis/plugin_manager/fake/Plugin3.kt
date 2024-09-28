package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin3 : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(TestFeature1::class.java) {
            object : TestFeature1 {}
        },
        FeatureWrapper(TestFeature1::class.java) {
            object : TestFeature1 {}
        },
        FeatureWrapper(TestFeature2::class.java) {
            object : TestFeature2 {}
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit

}