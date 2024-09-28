package ru.tensor.sbis.design.link_share.di.view

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareRouter
import ru.tensor.sbis.design.link_share.presentation.adapter.LinkShareAdapter
import ru.tensor.sbis.design.link_share.presentation.router.LinkShareRouterImpl

/**@SelfDocumented*/
@Module
internal class LinkShareViewModule {

    /** @SelfDocumented */
    @LinkShareViewScope
    @Provides
    fun provideShareAdapter() = LinkShareAdapter()

    /** @SelfDocumented */
    @LinkShareViewScope
    @Provides
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    /**@SelfDocumented*/
    @LinkShareViewScope
    @Provides
    fun provideRouter(): LinkShareRouter = LinkShareRouterImpl()
}