package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar

/**
 * Доступный для отображения период.
 *
 * @author mb.kruglova
 */
@Parcelize
data class SbisPeriodPickerRange(
    /** Начало периода. */
    val startDate: Calendar? = MIN_DATE,
    /** Конец периода. */
    val endDate: Calendar? = MAX_DATE
) : Parcelable {

    /** Дата начала доступного периода. */
    val start: Calendar
        get() = startDate?.removeTime() ?: MIN_DATE

    /** Год начала доступного периода. */
    val startYear: Int
        get() = start.year

    /** Месяц начала доступного периода. */
    val startMonth: Int
        get() = start.month

    /** День месяца начала доступного периода. */
    val startDayOfMonth: Int
        get() = start.dayOfMonth

    /** Дата окончания доступного периода. */
    val end: Calendar
        get() = endDate?.removeTime() ?: MAX_DATE

    /** Год окончания доступного периода. */
    val endYear: Int
        get() = end.year

    /** Месяц окончания доступного периода. */
    val endMonth: Int
        get() = end.month

    /** День месяца окончания доступного периода. */
    val endDayOfMonth: Int
        get() = end.dayOfMonth
}