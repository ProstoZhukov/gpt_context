package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**@SelfDocumented*/
@Parcelize
data class UsbPathItem(
    var value: UsbDeviceId,
    var isActive: Boolean
) : Parcelable {
    companion object {
        fun stub() = UsbPathItem(
            UsbDeviceId(),
            false
        )
    }
}