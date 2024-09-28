package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип дня.
 *
 * @author mb.kruglova
 */
sealed class SbisPeriodPickerDayType : Parcelable {

    /** Простой. */
    @Parcelize
    object Simple : SbisPeriodPickerDayType()

    /** С пометкой. */
    @Parcelize
    class Marked(val markedDayType: SbisPeriodPickerMarkedDayType) : SbisPeriodPickerDayType()
}