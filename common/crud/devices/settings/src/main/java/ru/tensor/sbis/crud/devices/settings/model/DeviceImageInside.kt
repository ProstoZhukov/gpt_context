package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель со ссылками на картинки устройства
 *
 * @param smallImage String? - ссылка на маленькую картинку устройства (опционально)
 * @param bigImage String - ссылка на большую картинку устройства
 */
@Parcelize
data class DeviceImageInside(
    val smallImage: String?,
    val bigImage: String?
) : Parcelable