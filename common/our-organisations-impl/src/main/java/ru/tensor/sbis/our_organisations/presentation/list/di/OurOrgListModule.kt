package ru.tensor.sbis.our_organisations.presentation.list.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListRouter
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListRouterImpl
import ru.tensor.sbis.ourorg.generated.OurorgController
import ru.tensor.sbis.ourorg.generated.OurorgService
import ru.tensor.sbis.platform.sync.generated.AreaSyncInformer

/**
 * Модуль для предоставления зависимостей [OurOrgListComponent].
 *
 * @author mv.ilin
 */
@Module
internal class OurOrgListModule {

    @Provides
    fun provideOurOrgListRouter(): OurOrgListRouter = OurOrgListRouterImpl()

    @Provides
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    @Provides
    fun provideOurOrgListController(): Lazy<@JvmSuppressWildcards OurorgController> = lazy {
        OurorgService.instance().getOurorgController()
    }

    @Provides
    fun provideAreaSyncInformer(): AreaSyncInformer = AreaSyncInformer.instance()

}