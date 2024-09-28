package ru.tensor.sbis.business_card

import ru.tensor.sbis.business_card.contract.BusinessCardDependency
import ru.tensor.sbis.business_card.contract.BusinessCardFeature
import ru.tensor.sbis.business_card.contract.BusinessCardFragmentProvider
import ru.tensor.sbis.business_card.di.BusinessCardComponent
import ru.tensor.sbis.business_card.di.DaggerBusinessCardComponent
import ru.tensor.sbis.link_share.ui.LinkShareFragmentProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/** Плагин для экрана визитки */
object BusinessCardPlugin : BasePlugin<Unit>() {
    /** @SelfDocumented */
    internal val singletonComponent: BusinessCardComponent by lazy {
        DaggerBusinessCardComponent.factory().create(
            object : BusinessCardDependency,
                LinkShareFragmentProvider by linkShareFragmentProvider.get(){}
        )
    }

    private lateinit var linkShareFragmentProvider: FeatureProvider<LinkShareFragmentProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(BusinessCardFragmentProvider::class.java) { BusinessCardFeature() },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(LinkShareFragmentProvider::class.java) { linkShareFragmentProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}