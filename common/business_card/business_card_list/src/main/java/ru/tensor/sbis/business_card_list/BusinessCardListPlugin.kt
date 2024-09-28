package ru.tensor.sbis.business_card_list

import ru.tensor.sbis.business_card.contract.BusinessCardFragmentProvider
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_list.contract.BusinessCardListDependency
import ru.tensor.sbis.business_card_list.contract.BusinessCardListFeature
import ru.tensor.sbis.business_card_list.contract.BusinessCardListFragmentProvider
import ru.tensor.sbis.business_card_list.di.BusinessCardListComponent
import ru.tensor.sbis.business_card_list.di.DaggerBusinessCardListComponent
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.link_share.ui.LinkShareFragmentProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/** Плагин для экрана реестра визиток */
object BusinessCardListPlugin : BasePlugin<Unit>() {
    /** @SelfDocumented */
    internal val singletonComponent: BusinessCardListComponent by lazy {
        DaggerBusinessCardListComponent.factory().create(
            resourceProvider.get(),
            businessCardDomainCommandProvider.get(),
            object : BusinessCardListDependency,
                LinkShareFragmentProvider by linkShareFragmentProvider.get(),
                BusinessCardFragmentProvider by businessCardFragmentProvider.get() {}
        )
    }

    private lateinit var resourceProvider: FeatureProvider<ResourceProvider>
    private lateinit var linkShareFragmentProvider: FeatureProvider<LinkShareFragmentProvider>
    private lateinit var businessCardFragmentProvider: FeatureProvider<BusinessCardFragmentProvider>
    private lateinit var businessCardDomainCommandProvider: FeatureProvider<BusinessCardDomainCommandProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(BusinessCardListFragmentProvider::class.java) { BusinessCardListFeature() },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(ResourceProvider::class.java) { resourceProvider = it }
        .require(LinkShareFragmentProvider::class.java) { linkShareFragmentProvider = it }
        .require(BusinessCardDomainCommandProvider::class.java) { businessCardDomainCommandProvider = it }
        .require(BusinessCardFragmentProvider::class.java) { businessCardFragmentProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}