package ru.tensor.sbis.plugin_manager.fake

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

class Plugin11 : BasePlugin<Unit>() {

    internal lateinit var testFeature4: FeatureProvider<TestFeature4>

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .require(TestFeature4::class.java) { testFeature4 = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun initialize() {
        testFeature4.get()
    }

}