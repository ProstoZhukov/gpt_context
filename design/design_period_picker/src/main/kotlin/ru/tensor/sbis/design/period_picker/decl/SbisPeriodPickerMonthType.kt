package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Тип месяца.
 *
 * @author mb.kruglova
 */
@Parcelize
sealed class SbisPeriodPickerMonthType : Parcelable {
    /** Простой. */
    object Simple : SbisPeriodPickerMonthType()

    /** С пометкой. */
    object Marked : SbisPeriodPickerMonthType()
}