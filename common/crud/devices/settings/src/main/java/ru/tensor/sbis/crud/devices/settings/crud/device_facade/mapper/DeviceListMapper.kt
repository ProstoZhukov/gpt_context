package ru.tensor.sbis.crud.devices.settings.crud.device_facade.mapper

import android.content.Context
import ru.tensor.devices.settings.generated.Device
import ru.tensor.devices.settings.generated.ListResultOfDeviceMapOfStringString
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.exception.UnsupportedConnectionTypeException
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.crud.devices.settings.model.toAndroidType
import timber.log.Timber

/**
 * Маппер для преобразования списка устройств, пришедших с контроллера, в список Android моделей
 */
internal class DeviceListMapper(context: Context) :
        BaseModelMapper<ListResultOfDeviceMapOfStringString, PagedListResult<DeviceInside>>(context) {

    override fun apply(rawList: ListResultOfDeviceMapOfStringString): PagedListResult<DeviceInside> {
        val mappedList = rawList.result.mapNotNull(::tryToMapDevice)
        return PagedListResult(mappedList, rawList.haveMore)
    }

    private fun tryToMapDevice(device: Device): DeviceInside? {
        return try {
            device.toAndroidType()
        } catch (e: UnsupportedConnectionTypeException) {
            Timber.w(e)
            null
        }
    }
}
