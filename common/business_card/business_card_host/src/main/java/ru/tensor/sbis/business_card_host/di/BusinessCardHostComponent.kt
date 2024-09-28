package ru.tensor.sbis.business_card_host.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_host.contract.BusinessCardHostDependency

/**@SelfDocumented*/
@BusinessCardHostScope
@Component
interface BusinessCardHostComponent {

    /**@SelfDocumented*/
    fun getDependency(): BusinessCardHostDependency

    /**@SelfDocumented*/
    fun getDomainProvider(): BusinessCardDomainCommandProvider

    @Component.Factory
    interface Factory {
        /**@SelfDocumented*/
        fun create(
            @BindsInstance domainProvider: BusinessCardDomainCommandProvider,
            @BindsInstance dependency: BusinessCardHostDependency
        ): BusinessCardHostComponent
    }
}