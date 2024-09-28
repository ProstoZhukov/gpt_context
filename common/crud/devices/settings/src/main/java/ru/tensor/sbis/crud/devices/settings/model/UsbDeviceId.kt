package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель, описывающая USB устройство (path, vid/pid)
 *
 * @param usbId Модель, описывающая идентификатор USB устройства (VID/PID)
 * @param devicePath Путь к устройству.
 * */
@Parcelize
data class UsbDeviceId(val usbId: UsbId = UsbId(), var devicePath: String? = null) : Parcelable