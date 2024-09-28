package ru.tensor.sbis.business_card_host

import ru.tensor.sbis.business_card.contract.BusinessCardFragmentProvider
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_host.contract.BusinessCardHostDependency
import ru.tensor.sbis.business_card_host.contract.BusinessCardHostFeatureImpl
import ru.tensor.sbis.business_card_host.di.BusinessCardHostComponent
import ru.tensor.sbis.business_card_host.di.DaggerBusinessCardHostComponent
import ru.tensor.sbis.business_card_host_decl.ui.BusinessCardHostFeature
import ru.tensor.sbis.business_card_host_decl.ui.BusinessCardHostFragmentProvider
import ru.tensor.sbis.business_card_list.contract.BusinessCardListFragmentProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/** Плагин для экрана хоста визиток */
object BusinessCardHostPlugin : BasePlugin<Unit>() {
    /** @SelfDocumented */
    internal val singletonComponent: BusinessCardHostComponent by lazy {
        DaggerBusinessCardHostComponent.factory().create(
            businessCardDomainCommandProvider.get(),
            object : BusinessCardHostDependency,
                BusinessCardListFragmentProvider by businessCardListFragmentProvider.get(),
                BusinessCardFragmentProvider by businessCardFragmentProvider.get() {}
        )
    }

    private lateinit var businessCardFragmentProvider: FeatureProvider<BusinessCardFragmentProvider>
    private lateinit var businessCardListFragmentProvider: FeatureProvider<BusinessCardListFragmentProvider>
    private lateinit var businessCardDomainCommandProvider: FeatureProvider<BusinessCardDomainCommandProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(BusinessCardHostFragmentProvider::class.java) { BusinessCardHostFeatureImpl() },
        FeatureWrapper(BusinessCardHostFeature::class.java) { BusinessCardHostFeatureImpl() },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(BusinessCardDomainCommandProvider::class.java) { businessCardDomainCommandProvider = it }
        .require(BusinessCardFragmentProvider::class.java) { businessCardFragmentProvider = it }
        .require(BusinessCardListFragmentProvider::class.java) { businessCardListFragmentProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}