package ru.tensor.sbis.crud.devices.settings.crud.device_facade.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.devices.settings.model.DeviceInside
import ru.tensor.sbis.crud.devices.settings.model.toAndroidType
import ru.tensor.devices.settings.generated.Device as ControllerDevice

/**
 * Маппер для преобразования одного устройства, пришедшего с контроллера, в Android модель
 */
internal class DeviceMapper(context: Context) :
        BaseModelMapper<ControllerDevice, DeviceInside>(context) {

    override fun apply(rawData: ControllerDevice): DeviceInside =
            rawData.toAndroidType()
}
