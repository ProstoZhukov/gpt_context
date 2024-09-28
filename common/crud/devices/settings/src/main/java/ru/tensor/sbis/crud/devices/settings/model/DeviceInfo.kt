package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tensor.devices.settings.generated.DeviceInfo as ControllerDeviceInfo

/**
 * Модель с информацией о количестве устройств в точке продажи
 *
 * @param count Int - количество устройств одного типа
 * @param imageRef String - ссылка на картинку устройства
 */
@Parcelize
data class DeviceInfo(
    val count: Int,
    val imageRef: String
) : Parcelable {

    companion object {
        fun stub(): DeviceInfo = DeviceInfo(
            0,
            ""
        )
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerDeviceInfo.map(): DeviceInfo = DeviceInfo(
        count,
        ref)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceInfo.map(): ControllerDeviceInfo = ControllerDeviceInfo(
        count,
        imageRef)
