package ru.tensor.sbis.crud.sale.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.DeviceFacade
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.crud.kkm.*
import ru.tensor.sbis.crud.sale.crud.kkm.command.KkmListCommand
import ru.tensor.sbis.crud.sale.crud.kkm.mappers.KkmListMapper
import ru.tensor.sbis.crud.sale.crud.kkm.mappers.KkmMapper
import ru.tensor.sbis.crud.sale.model.CashRegister
import ru.tensor.sbis.sale.mobile.generated.*

/**@SelfDocumented */
@Module
class KkmModule {

    @Provides
    internal fun provideFilter(): KkmListFilter = KkmListFilter()

    @Provides
    internal fun provideManager(service: DependencyProvider<SaleMobileService>):
            DependencyProvider<KkmFacade> =
            DependencyProvider.create { service.get().kkm() }

    @Provides
    internal fun provideShiftKKmFacade(service: DependencyProvider<SaleMobileService>): DependencyProvider<ShiftKkmFacade> =
        DependencyProvider.create { service.get().shiftKkm() }

    @Provides
    internal fun provideRepository(
        manager: DependencyProvider<KkmFacade>,
        deviceManager: DependencyProvider<DeviceFacade>
    ): KkmRepository = KkmRepositoryImpl(manager, deviceManager)

    @Provides
    internal fun provideCommandWrapper(
        repository: KkmRepository,
        listCommand: ListObservableCommand<PagedListResult<CashRegister>, KkmFilter>
    ): KkmCommandWrapper = KkmCommandWrapperImpl(repository, listCommand)

    @Provides
    internal fun provideMapper(context: Context): BaseModelMapper<KkmModel, CashRegister> = KkmMapper(context)

    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<KkmListResult, PagedListResult<CashRegister>> =
            KkmListMapper(context)

    @Provides
    internal fun provideListCommand(repository: KkmRepository):
            ListObservableCommand<PagedListResult<CashRegister>, KkmFilter> =
            KkmListCommand(repository)

    @Provides
    internal fun provideDeviceFacade() = DependencyProvider.create { DeviceFacade.instance() }
}