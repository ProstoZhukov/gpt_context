package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель дополнительной информации об устройстве
 *
 * @param fetchedWithRemotes Boolean? - синхронизировано ли с удаленными (true - да, иначе - нет)
 * @param multipleKkm Boolean? - можно ли использовать устройство на нескольких рабочих местах (true - да, иначе - нет)
 * @param remoteKkm Boolean? - является ли устройство удаленным (true - да, иначе - нет)
 * @param workplaceName название рабочего места
 */
@Parcelize
data class DeviceMetainfoInside(
    val fetchedWithRemotes: Boolean? = false,
    val multipleKkm: Boolean? = false,
    val remoteKkm: Boolean? = false,
    val workplaceName: String? = null
) : Parcelable