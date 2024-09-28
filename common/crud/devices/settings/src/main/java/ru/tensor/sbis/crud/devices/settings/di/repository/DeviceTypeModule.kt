package ru.tensor.sbis.crud.devices.settings.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFacade
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.devices.settings.generated.DevicesSettings
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeCommandWrapperImpl
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeListFilter
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeRepository
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeRepositoryImpl
import ru.tensor.sbis.crud.devices.settings.crud.device_type.command.CatalogDeviceTypeListCommand
import ru.tensor.sbis.crud.devices.settings.crud.device_type.mapper.DeviceTypeListMapper
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand

/**@SelfDocumented*/
@Module
internal class DeviceTypeModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideFilter(): DeviceTypeListFilter = DeviceTypeListFilter()

    /**@SelfDocumented*/
    @Provides
    internal fun provideManager(devicesSettings: DependencyProvider<DevicesSettings>):
            DependencyProvider<DeviceTypeFacade> =
            DependencyProvider.create { devicesSettings.get().deviceType() }

    /**@SelfDocumented*/
    @Provides
    internal fun provideRepository(manager: DependencyProvider<DeviceTypeFacade>):
            DeviceTypeRepository = DeviceTypeRepositoryImpl(manager)

    /**@SelfDocumented*/
    @Provides
    internal fun provideCommandWrapper(listCommand: BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback>):
            DeviceTypeCommandWrapper =
            DeviceTypeCommandWrapperImpl(listCommand)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfDeviceTypeMapOfStringString, PagedListResult<BaseItem<DeviceType>>> =
            DeviceTypeListMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListCommand(repository: DeviceTypeRepository,
                                    mapper: BaseModelMapper<ListResultOfDeviceTypeMapOfStringString, PagedListResult<BaseItem<DeviceType>>>):
            BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback> =
            CatalogDeviceTypeListCommand(repository, mapper)
}
