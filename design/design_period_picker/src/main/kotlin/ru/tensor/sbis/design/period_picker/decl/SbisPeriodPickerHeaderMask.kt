package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Маска ввода даты.
 *
 * @author mb.kruglova
 */
@Parcelize
enum class SbisPeriodPickerHeaderMask(val mask: String) : Parcelable {
    DEFAULT("00.00.00"),
    FULL_YEAR("00.00.0000")
}