package ru.tensor.sbis.our_organisations

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.our_organisations.domain.OurOrgDataServiceWrapperImpl
import ru.tensor.sbis.our_organisations.feature.data.OurOrgDataServiceWrapper
import ru.tensor.sbis.our_organisations.feature.di.OurOrgFeature
import ru.tensor.sbis.our_organisations.presentation.OurOrgListModule
import ru.tensor.sbis.our_organisations.presentation.OurOrgListModuleImpl
import ru.tensor.sbis.our_organisations.presentation.list.interactor.OurOrgListInteractor
import javax.inject.Singleton

/**
 * DI - модуль для поставки зависимостей.
 *
 * @author mv.ilin
 */
@Module
internal class OurOrgDiModule {

    @Provides
    @Singleton
    fun provideFeature(
        ourOrgListModule: OurOrgListModule,
        ourOrgDataServiceWrapper: OurOrgDataServiceWrapper
    ): OurOrgFeature {
        return OurOrgFeatureImpl(
            ourOrgDataServiceWrapper = ourOrgDataServiceWrapper,
            ourOrgListModule = ourOrgListModule
        )
    }

    @Provides
    @Singleton
    fun provideOurOrgDataServiceWrapper(ourOrgListInteractor: OurOrgListInteractor): OurOrgDataServiceWrapper {
        return OurOrgDataServiceWrapperImpl(ourOrgListInteractor)
    }

    @Provides
    @Singleton
    fun provideOurOrgListModule(): OurOrgListModule {
        return OurOrgListModuleImpl()
    }

    @Provides
    fun provideScrollHelper(commonSingletonComponent: CommonSingletonComponent): ScrollHelper {
        return commonSingletonComponent.scrollHelper
    }
}
