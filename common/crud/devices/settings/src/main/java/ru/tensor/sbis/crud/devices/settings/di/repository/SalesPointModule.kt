package ru.tensor.sbis.crud.devices.settings.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.DataRefreshedSalesPointFacadeCallback
import ru.tensor.devices.settings.generated.DevicesSettings
import ru.tensor.devices.settings.generated.ListResultOfSalesPointMapOfStringString
import ru.tensor.devices.settings.generated.SalesPointFacade
import ru.tensor.devices.settings.generated.SalesPointFilter
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointCommandWrapperImpl
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointListFilter
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointRepository
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.SalesPointRepositoryImpl
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.BreadCrumbsSalesPointListCommand
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.command.SalesPointListCommand
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper.SalesPointFindListMapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper.SalesPointListMapper
import ru.tensor.sbis.crud.devices.settings.crud.sales_point.mapper.SalesPointMapper
import ru.tensor.sbis.crud.devices.settings.model.SalesItem
import ru.tensor.sbis.crud.devices.settings.model.SalesPoint
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.devices.settings.generated.SalesPoint as ControllerSalesPoint

/**@SelfDocumented*/
@Module
internal class SalesPointModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideManager(devicesSettings: DependencyProvider<DevicesSettings>):
            DependencyProvider<SalesPointFacade> =
            DependencyProvider.create { devicesSettings.get().salesPoint() }

    /**@SelfDocumented*/
    @Provides
    internal fun provideFilter(): SalesPointListFilter = SalesPointListFilter()

    /**@SelfDocumented*/
    @Provides
    internal fun provideRepository(manager: DependencyProvider<SalesPointFacade>):
            SalesPointRepository = SalesPointRepositoryImpl(manager)

    /**@SelfDocumented*/
    @Provides
    internal fun provideSalesPointCommandWrapper(repository: SalesPointRepository,
                                                 listCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>,
                                                 breadCommand: BaseListObservableCommand<PagedListResult<BaseItem<SalesItem>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback>):
            SalesPointCommandWrapper =
            SalesPointCommandWrapperImpl(repository, listCommand, breadCommand)

    /**@SelfDocumented*/
    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<ControllerSalesPoint, SalesPoint> =
            SalesPointMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>> =
            SalesPointListMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListCommand(repository: SalesPointRepository,
                                    mapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>>):
            BaseListObservableCommand<PagedListResult<BaseItem<SalesPoint>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback> =
            SalesPointListCommand(repository, mapper)

    /**@SelfDocumented*/
    @Provides
    internal fun provideBreadCrumbsListCommand(repository: SalesPointRepository,
                                    mapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesPoint>>>,
                                    findMapper: BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>>):
            BaseListObservableCommand<PagedListResult<BaseItem<SalesItem>>, SalesPointFilter, DataRefreshedSalesPointFacadeCallback> =
            BreadCrumbsSalesPointListCommand(repository, mapper as BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>>, findMapper)

    /**@SelfDocumented*/
    @Provides
    internal fun provideFindListMapper(context: Context):
            BaseModelMapper<ListResultOfSalesPointMapOfStringString, PagedListResult<BaseItem<SalesItem>>> =
            SalesPointFindListMapper(context)
}
