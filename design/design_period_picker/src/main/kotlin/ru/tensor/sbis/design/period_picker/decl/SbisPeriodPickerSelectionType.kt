package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Режим выделения диапазона дат.
 *
 * @author mb.kruglova
 */
sealed class SbisPeriodPickerSelectionType : Parcelable {

    /** Выделение диапазона дат. */
    @Parcelize
    object Range : SbisPeriodPickerSelectionType()

    /** Выделение только одной даты. */
    @Parcelize
    object Single : SbisPeriodPickerSelectionType()
}