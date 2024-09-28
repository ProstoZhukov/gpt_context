package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель типа подключения
 */
@Parcelize
data class ConnectionTypeItem(
    var value: ConnectionTypeInside,
    var driver: String,
    var isActive: Boolean
) : Parcelable {
    companion object {
        fun stub(): ConnectionTypeItem = ConnectionTypeItem(
            ConnectionTypeInside.KEYBOARD,
            "",
            false
        )
    }
}