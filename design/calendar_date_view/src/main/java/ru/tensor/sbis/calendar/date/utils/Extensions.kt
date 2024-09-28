/**
 * Extensions функции для форматирования дат в календаре.
 *
 * @author ae.noskov
 */
package ru.tensor.sbis.calendar.date.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.design.utils.LocaleUtils
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import java.text.SimpleDateFormat

private val simpleDateFormatYear = SimpleDateFormat("dd MMMM yyyy", LocaleUtils.getDefaultLocale)
private val simpleDateFormatMonth = SimpleDateFormat("dd MMMM", LocaleUtils.getDefaultLocale)

private val simpleMonthFormatYear = SimpleDateFormat("LLLL''yy", LocaleUtils.getDefaultLocale)
private val simpleMonthFormat = SimpleDateFormat("LLLL", LocaleUtils.getDefaultLocale)

internal fun LocalDate.asDateString(): String =
    (if (this.year == LocalDate.now().year) simpleDateFormatMonth.format(this.toDate()) else simpleDateFormatYear.format(
        this.toDate()
    ))

internal fun LocalDate.asMonthString(now: LocalDate = LocalDate.now()): String =
    (if (this.year == now.year) simpleMonthFormat.format(this.toDate()) else simpleMonthFormatYear.format(this.toDate()))

/**
 * Представление пары дат в строку периода дат.
 */
fun Pair<LocalDate?, LocalDate?>?.asDateString(): String {
    if (this == null)
        return ""

    val (startDate, finDate) = this
    return when {
        startDate == null -> ""
        startDate == finDate || finDate == null ->
            startDate.asDateString()

        else -> {
            if (startDate.year == LocalDate().year && startDate.year == finDate.year)
                "%s - %s".format(
                    simpleDateFormatMonth.format(startDate.toDate()),
                    simpleDateFormatMonth.format(finDate.toDate())
                )
            else
                "%s - %s".format(
                    simpleDateFormatYear.format(startDate.toDate()),
                    simpleDateFormatYear.format(finDate.toDate())
                )
        }
    }
}

internal operator fun DisposableContainer.plusAssign(d: Disposable) {
    add(d)
}

fun Context.getCalendarDateViewStyleId(): Int {
    val tempStyleId = this.getDataFromAttrOrNull(R.attr.CalendarDateViewStyle, false)
    return tempStyleId ?: R.style.CalendarDateViewBaseStyle
}


fun View.getDimensionVisibleForPreview(unit: Int, value: Float, themedValue: Float) = if (isInEditMode) {
    TypedValue.applyDimension(unit, value, resources.displayMetrics)
} else {
    themedValue
}

@Suppress("unused")
fun View.getDimensionVisibleForPreview(unit: Int, value: Float, themedValue: Int) = if (isInEditMode) {
    TypedValue.applyDimension(unit, value, resources.displayMetrics).toInt()
} else {
    themedValue
}