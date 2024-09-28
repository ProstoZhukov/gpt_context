package ru.tensor.sbis.crud.devices.settings.crud.device_type

import ru.tensor.devices.settings.generated.DataRefreshedDeviceTypeFacadeCallback
import ru.tensor.devices.settings.generated.DeviceTypeFilter
import ru.tensor.devices.settings.generated.ListResultOfDeviceTypeMapOfStringString
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository

/**
 * Интерфейс для связи с контроллером.
 */
interface DeviceTypeRepository :
        BaseListRepository<ListResultOfDeviceTypeMapOfStringString, DeviceTypeFilter, DataRefreshedDeviceTypeFacadeCallback>
