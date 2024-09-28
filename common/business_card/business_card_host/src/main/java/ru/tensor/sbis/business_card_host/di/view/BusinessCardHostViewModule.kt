package ru.tensor.sbis.business_card_host.di.view

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.business_card_domain.BusinessCardDomainCommandProvider
import ru.tensor.sbis.business_card_host.contract.BusinessCardHostDependency
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostInteractor
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostRouter
import ru.tensor.sbis.business_card_host.domain.BusinessCardHostInteractorImpl
import ru.tensor.sbis.business_card_host.presentation.router.BusinessCardHostRouterImpl

/**@SelfDocumented*/
@Module
internal class BusinessCardHostViewModule {

    /**@SelfDocumented*/
    @BusinessCardHostViewScope
    @Provides
    internal fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    /**@SelfDocumented*/
    @BusinessCardHostViewScope
    @Provides
    internal fun provideRouter(
        dependency: BusinessCardHostDependency,
        containerId: Int
    ): BusinessCardHostRouter = BusinessCardHostRouterImpl(dependency, containerId)

    /**@SelfDocumented*/
    @BusinessCardHostViewScope
    @Provides
    fun provideInteractor(provider: BusinessCardDomainCommandProvider): BusinessCardHostInteractor =
        BusinessCardHostInteractorImpl(provider.getWrapper())
}