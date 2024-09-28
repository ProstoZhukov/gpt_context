package ru.tensor.sbis.crud.devices.settings.crud.device_type

import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFacade
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription

/** @see DeviceTypeRepository */
internal class DeviceTypeRepositoryImpl(private val controller: DependencyProvider<DeviceTypeFacade>) :
        DeviceTypeRepository {

    override fun list(filter: DeviceTypeFilter): ListResultOfDeviceTypeMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: DeviceTypeFilter): ListResultOfDeviceTypeMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedDeviceTypeFacadeCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)
}
