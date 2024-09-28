package ru.tensor.sbis.our_organisations

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.our_organisations.feature.di.OurOrgFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

object OurOrgPlugin : BasePlugin<Unit>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(OurOrgFeature::class.java) { ourOrgComponent.getFeature() }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    internal val ourOrgComponent: OurOrgDiComponent by lazy {
        OurOrgComponentInitializer().init(commonSingletonComponentProvider.get())
    }
}
