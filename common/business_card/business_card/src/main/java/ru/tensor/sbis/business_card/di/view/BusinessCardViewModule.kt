package ru.tensor.sbis.business_card.di.view

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.business_card.contract.BusinessCardDependency
import ru.tensor.sbis.business_card.contract.internal.BusinessCardRouter
import ru.tensor.sbis.business_card.presentation.router.BusinessCardRouterImpl

/**@SelfDocumented*/
@Module
internal class BusinessCardViewModule {

    /**@SelfDocumented*/
    @BusinessCardViewScope
    @Provides
    internal fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    /**@SelfDocumented*/
    @BusinessCardViewScope
    @Provides
    internal fun provideRouter(
        androidComponent: AndroidComponent,
        dependency: BusinessCardDependency,
        containerId: Int
    ): BusinessCardRouter = BusinessCardRouterImpl(androidComponent, dependency, containerId)
}