package ru.tensor.sbis.crud.sale.di.repository

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.FiscalRepository
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.FiscalRepositoryImpl
import ru.tensor.sbis.crud.sale.crud.kkm_service_mobile.KkmRegistrationService
import ru.tensor.sbis.sale.mobile.generated.KkmFacade
import ru.tensor.sbis.sale.mobile.generated.SaleMobileService
import ru.tensor.sbis.sale.mobile.generated.ShiftKkmFacade

/** @SelfDocumented */
@Module
class FiscalModule {

    /**@SelfDocumented*/
    @FiscalScope
    @Provides
    internal fun provideFiscalRepository(
        service: DependencyProvider<KkmFacade>,
        shiftKkmFacade: DependencyProvider<ShiftKkmFacade>,
        kkmRegistrationService: KkmRegistrationService
    ): FiscalRepository = FiscalRepositoryImpl(service, shiftKkmFacade, kkmRegistrationService)

    @FiscalScope
    @Provides
    internal fun provideKKmFacade(service: DependencyProvider<SaleMobileService>):
            DependencyProvider<KkmFacade> =
        DependencyProvider.create { service.get().kkm() }

    @FiscalScope
    @Provides
    internal fun provideShiftKKmFacade(service: DependencyProvider<SaleMobileService>): DependencyProvider<ShiftKkmFacade> =
        DependencyProvider.create { service.get().shiftKkm() }

    @FiscalScope
    @Provides
    internal fun provideSaleMobileService(): DependencyProvider<SaleMobileService> =
        DependencyProvider.create { SaleMobileService.instance() }

}