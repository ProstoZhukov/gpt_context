package ru.tensor.sbis.crud.devices.settings.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.*
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.*
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.mapper.DeviceListMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.mapper.DeviceMapper
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommandImpl
import ru.tensor.devices.settings.generated.Device as ControllerDevice

/**@SelfDocumented*/
@Module
internal class DeviceModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideManager(devicesSettings: DependencyProvider<DevicesSettings>):
            DependencyProvider<DeviceFacade> =
            DependencyProvider.create { devicesSettings.get().device() }

    /**@SelfDocumented*/
    @Provides
    internal fun provideFilter(): DeviceListFilter = DeviceListFilter()

    /**@SelfDocumented*/
    @Provides
    @Suppress("ConstantConditionIf")
    internal fun provideRepository(manager: DependencyProvider<DeviceFacade>):
            DeviceRepository = DeviceRepositoryImpl(manager)

    /**@SelfDocumented*/
    @Provides
    internal fun provideDeviceSettingsCommandWrapper(repository: DeviceRepository,
                                                     createCommand: CreateObservableCommand<ControllerDevice>,
                                                     readCommand: ReadObservableCommand<DeviceInside>,
                                                     deleteCommand: DeleteRepositoryCommand<ControllerDevice>,
                                                     listCommand: BaseListObservableCommand<PagedListResult<DeviceInside>, DeviceFilter, DataRefreshedDeviceFacadeCallback>):
            DeviceCommandWrapper =
            DeviceCommandWrapperImpl(repository, createCommand, readCommand, deleteCommand, listCommand)

    /**@SelfDocumented*/
    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<ControllerDevice, DeviceInside> =
            DeviceMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfDeviceMapOfStringString, PagedListResult<DeviceInside>> =
            DeviceListMapper(context)

    /**@SelfDocumented*/
    @Provides
    internal fun provideCreateCommand(repository: DeviceRepository):
            CreateObservableCommand<ControllerDevice> =
            CreateCommand(repository)

    /**@SelfDocumented*/
    @Provides
    internal fun provideReadCommand(repository: DeviceRepository,
                                    mapper: BaseModelMapper<ControllerDevice, DeviceInside>):
            ReadObservableCommand<DeviceInside> =
            ReadCommand(repository, mapper)

    /**@SelfDocumented*/
    @Provides
    internal fun provideUpdateCommand(repository: DeviceRepository):
            UpdateObservableCommand<ControllerDevice> =
            UpdateCommand(repository)

    /**@SelfDocumented*/
    @Provides
    internal fun provideDeleteCommand(repository: DeviceRepository):
            DeleteRepositoryCommand<ControllerDevice> =
            DeleteRepositoryCommandImpl(repository)


    /**@SelfDocumented*/
    @Provides
    internal fun provideListCommand(repository: DeviceRepository,
                                    mapper: BaseModelMapper<ListResultOfDeviceMapOfStringString, PagedListResult<DeviceInside>>):
            BaseListObservableCommand<PagedListResult<DeviceInside>, DeviceFilter, DataRefreshedDeviceFacadeCallback> =
            BaseListCommand(repository, mapper)

}
