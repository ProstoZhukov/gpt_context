package ru.tensor.sbis.crud.sale.model

import ru.tensor.devices.kkmservice.generated.DeviceInfo as ControllerDeviceInfo
import java.util.*

/**
 * Хардварные и софтварные характеристики ККМ
 * @property fdExpiryDate срок действия фискального накопителя
 */
data class DeviceInfo(val fdExpiryDate: Date?)

/**@SelfDocumented */
fun ControllerDeviceInfo.map(): DeviceInfo {
    return DeviceInfo(registrationParameters.fdValidity.fdExpiryDate)
}