package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**@SelfDocumented*/
@Parcelize
data class SeparatorItem(
    var value: ScannerSeparator,
    var isActive: Boolean
) : Parcelable {
    companion object {
        fun stub() = SeparatorItem(
            ScannerSeparator.CR_LF,
            false
        )
    }
}