package ru.tensor.sbis.crud.devices.settings.di.repository

import ru.tensor.devices.settings.generated.DataRefreshedDeviceFacadeCallback
import ru.tensor.devices.settings.generated.DeviceFacade
import ru.tensor.devices.settings.generated.DeviceFilter
import ru.tensor.devices.settings.generated.ListResultOfDeviceMapOfStringString
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.DeviceCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.DeviceListFilter
import ru.tensor.sbis.crud.devices.settings.crud.device_facade.DeviceRepository
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.devices.settings.generated.Device as ControllerDevice

/**@SelfDocumented*/
interface DeviceComponent : Feature {

    /**@SelfDocumented */
    fun getDeviceManager(): DependencyProvider<DeviceFacade>

    /**@SelfDocumented */
    fun getDeviceListFilter(): DeviceListFilter

    /**@SelfDocumented */
    fun getDeviceRepository(): DeviceRepository
    /**@SelfDocumented */
    fun getDeviceCommandWrapper(): DeviceCommandWrapper

    /**@SelfDocumented */
    fun getDeviceMapper(): BaseModelMapper<ControllerDevice, DeviceInside>
    /**@SelfDocumented */
    fun getDeviceListMapper(): BaseModelMapper<ListResultOfDeviceMapOfStringString, PagedListResult<DeviceInside>>

    /**@SelfDocumented */
    fun getDeviceCreateCommand(): CreateObservableCommand<ControllerDevice>
    /**@SelfDocumented */
    fun getDeviceReadCommand(): ReadObservableCommand<DeviceInside>
    /**@SelfDocumented */
    fun getDeviceUpdateCommand(): UpdateObservableCommand<ControllerDevice>
    /**@SelfDocumented */
    fun getDeviceDeleteCommand(): DeleteRepositoryCommand<ControllerDevice>
    /**@SelfDocumented */
    fun getDeviceListCommand(): BaseListObservableCommand<PagedListResult<DeviceInside>, DeviceFilter, DataRefreshedDeviceFacadeCallback>
}
