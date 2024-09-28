package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.DeviceImage as ControllerDeviceImage

/**
 * Модель со ссылками на картинки устройства
 *
 * @param smallRef String - ссылка на маленькую картинку устройства
 * @param bigRef String - ссылка на большую картинку устройства
 */
@Parcelize
data class DeviceImage(
    val smallRef: String,
    val bigRef: String
) : Parcelable {

    companion object {
        fun stub(): DeviceImage = DeviceImage(
                "",
                "")
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerDeviceImage.map(): DeviceImage = DeviceImage(small, big)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun DeviceImage.map(): ControllerDeviceImage = ControllerDeviceImage(smallRef, bigRef)
