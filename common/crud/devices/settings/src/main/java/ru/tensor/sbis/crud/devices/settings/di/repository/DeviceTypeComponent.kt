package ru.tensor.sbis.crud.devices.settings.di.repository

import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFacade
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeCommandWrapper
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeListFilter
import ru.tensor.sbis.crud.devices.settings.crud.device_type.DeviceTypeRepository
import ru.tensor.sbis.crud.devices.settings.model.DeviceType
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
interface DeviceTypeComponent : Feature {

    /**@SelfDocumented */
    fun getDeviceTypeManager(): DependencyProvider<DeviceTypeFacade>

    /**@SelfDocumented */
    fun getDeviceTypeListFilter(): DeviceTypeListFilter

    /**@SelfDocumented */
    fun getDeviceTypeRepository(): DeviceTypeRepository
    /**@SelfDocumented */
    fun getDeviceTypeCommandWrapper(): DeviceTypeCommandWrapper

    /**@SelfDocumented */
    fun getDeviceTypeMapper(): BaseModelMapper<ListResultOfDeviceTypeMapOfStringString, PagedListResult<BaseItem<DeviceType>>>

    /**@SelfDocumented */
    fun getDeviceTypeListCommand(): BaseListObservableCommand<PagedListResult<BaseItem<DeviceType>>, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback>
}
