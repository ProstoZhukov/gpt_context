package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Настройки для сканеров.
 * Используется на карточке создания/редактирования сканнера
 * */
@Parcelize
data class ScannerDeviceSettings(
    val scannerTimeout: ScannerTimeout?,
    val prefix: String?,
    val suffix: String?
) : Parcelable