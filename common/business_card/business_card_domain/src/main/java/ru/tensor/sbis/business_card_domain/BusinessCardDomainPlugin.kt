package ru.tensor.sbis.business_card_domain

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainCommandWrapper
import ru.tensor.sbis.business_card_domain.di.BusinessCardDomainComponent
import ru.tensor.sbis.business_card_domain.di.DaggerBusinessCardDomainComponent
import ru.tensor.sbis.viper.crud.BaseCommandWrapperProvider

/**
 * Плагин для экрана визиток.
 */
object BusinessCardDomainPlugin : BasePlugin<Unit>() {
    private val singletonComponent: BusinessCardDomainComponent by lazy {
        DaggerBusinessCardDomainComponent.builder()
            .build()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(BusinessCardDomainCommandProvider::class.java) {
            object : BusinessCardDomainComponent {
                override fun getWrapper(): BusinessCardDomainCommandWrapper = singletonComponent.getWrapper()
            }
        }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit
}

/**@SelfDocumented*/
interface BusinessCardDomainCommandProvider : BaseCommandWrapperProvider<BusinessCardDomainCommandWrapper>