package ru.tensor.sbis.business_card_domain.di

import dagger.Component
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider

/**@SelfDocumented*/
@BusinessCardDomainScope
@Component(modules = [BusinessCardDomainModule::class])
interface BusinessCardDomainComponent : Feature, BusinessCardDomainCommandProvider {

    @Component.Builder
    interface Builder {

        /**@SelfDocumented*/
        fun build(): BusinessCardDomainComponent
    }
}