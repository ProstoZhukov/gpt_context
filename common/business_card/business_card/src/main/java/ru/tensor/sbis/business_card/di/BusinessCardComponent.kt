package ru.tensor.sbis.business_card.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.business_card.contract.BusinessCardDependency

/**@SelfDocumented*/
@BusinessCardScope
@Component
interface BusinessCardComponent {

    /**@SelfDocumented*/
    fun getDependency(): BusinessCardDependency

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        /**@SelfDocumented*/
        fun create(
            @BindsInstance dependency: BusinessCardDependency
        ): BusinessCardComponent
    }
}