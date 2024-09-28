package ru.tensor.sbis.business_card_domain.di

import dagger.Module
import dagger.Provides
import ru.tensor.business.card.mobile.generated.BusinessCardManager
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainCommandWrapper
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainCommandWrapperImpl
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainRepository
import ru.tensor.sbis.business_card_domain.command.BusinessCardDomainRepositoryImpl

/**@SelfDocumented*/
@Module
internal class BusinessCardDomainModule {

    /**@SelfDocumented*/
    @Provides
    @BusinessCardDomainScope
    internal fun provideManager(): DependencyProvider<BusinessCardManager> =
        DependencyProvider.create { BusinessCardManager.instance() }

    /**@SelfDocumented*/
    @Provides
    @BusinessCardDomainScope
    internal fun provideRepository(manager: DependencyProvider<BusinessCardManager>): BusinessCardDomainRepository =
        BusinessCardDomainRepositoryImpl(manager)

    /**@SelfDocumented*/
    @Provides
    @BusinessCardDomainScope
    internal fun provideBusinessCardDomainCommandWrapper(repository: BusinessCardDomainRepository): BusinessCardDomainCommandWrapper =
        BusinessCardDomainCommandWrapperImpl(repository)
}
