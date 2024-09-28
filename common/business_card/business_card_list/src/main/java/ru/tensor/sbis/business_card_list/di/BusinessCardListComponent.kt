package ru.tensor.sbis.business_card_list.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_list.contract.BusinessCardListDependency
import ru.tensor.sbis.common.util.ResourceProvider

/**@SelfDocumented*/
@BusinessCardListScope
@Component
interface BusinessCardListComponent {

    /**@SelfDocumented*/
    fun getDependency(): BusinessCardListDependency

    /**@SelfDocumented*/
    fun getDomainProvider(): BusinessCardDomainCommandProvider

    /**@SelfDocumented*/
    fun getResourceProvider(): ResourceProvider

    @Component.Factory
    interface Factory {
        /**@SelfDocumented*/
        fun create(
            @BindsInstance resourceProvider: ResourceProvider,
            @BindsInstance domainProvider: BusinessCardDomainCommandProvider,
            @BindsInstance dependency: BusinessCardListDependency
        ): BusinessCardListComponent
    }
}