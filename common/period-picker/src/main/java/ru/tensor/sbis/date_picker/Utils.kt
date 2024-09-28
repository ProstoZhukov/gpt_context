/**
 * @author mb.kruglova
 */
package ru.tensor.sbis.date_picker

import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.date_picker.free.items.HistoryPeriod
import ru.tensor.sbis.date_picker.items.Day
import ru.tensor.sbis.date_picker.items.Month
import java.util.*

fun Date.toCalendar(): Calendar {
    val calendar = GregorianCalendar()
    calendar.time = this
    return calendar
}

fun prepareDateInterval(
    halfYearTitles: List<String>,
    quarterTitles: List<String>,
    monthTitles: List<String>,
    period: Period
): HistoryPeriod {
    val dateFrom = period.from ?: Date()
    val dateTo = period.to ?: Date()
    val title = prepareDateIntervalTitle(
        halfYearTitles,
        quarterTitles,
        monthTitles,
        dateFrom.toCalendar(),
        dateTo.toCalendar()
    ).toString()
    return HistoryPeriod(title, dateFrom, dateTo)
}

fun prepareDateIntervalTitle(
    halfYearTitles: List<String>,
    quarterTitles: List<String>,
    monthTitles: List<String>,
    period: Period
): PeriodText {
    return when {
        period.hasFromAndTo -> prepareDateIntervalTitle(
            halfYearTitles,
            quarterTitles,
            monthTitles,
            period.dateFrom!!,
            period.dateTo!!
        )
        period.hasFrom -> prepareDateIntervalTitle(
            halfYearTitles,
            quarterTitles,
            monthTitles,
            period.dateFrom!!,
            period.fakeDateTo ?: period.dateFrom!!
        )
        period.hasTo -> PeriodText(DateFormatUtils.format(period.to, DateFormatTemplate.LONG_DATE_WITHOUT_LEADING_ZERO))
        else -> PeriodText(null)
    }
}

fun prepareDateIntervalTitle(
    halfYearTitles: List<String>,
    quarterTitles: List<String>,
    monthTitles: List<String>,
    from: Calendar,
    to: Calendar
): PeriodText {
    val yearFrom = from.year
    val monthFrom = from.month
    val dayFrom = from.dayOfMonth
    val minDayFrom = from.getActualMinimum(Calendar.DAY_OF_MONTH)
    val yearTo = to.year
    val monthTo = to.month
    val dayTo = to.dayOfMonth
    val maxDayTo = to.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Одиночная дата
    if (from == to) {
        return PeriodText(DateFormatUtils.format(from.time, DateFormatTemplate.LONG_DATE_WITHOUT_LEADING_ZERO))
    }

    if (monthFrom == Calendar.JANUARY && dayFrom == 1 && monthTo == Calendar.DECEMBER && dayTo == 31) {
        // Год
        return if (yearFrom == yearTo) {
            PeriodText(yearFrom.toString())
            // Несколько лет
        } else {
            PeriodText(yearFrom.toString(), yearTo.toString())
        }
    }

    if (yearFrom == yearTo && dayFrom == minDayFrom && dayTo == maxDayTo) {
        if (monthFrom == Calendar.JANUARY && monthTo == Calendar.JUNE) {
            return PeriodText(getYearPeriodTitle(halfYearTitles[0], yearFrom))
        }

        // Первое полугодие
        if (monthFrom == Calendar.JANUARY && monthTo == Calendar.JUNE) {
            return PeriodText(getYearPeriodTitle(halfYearTitles[0], yearFrom))
        }

        // Второе полугодие
        if (monthFrom == Calendar.JULY && monthTo == Calendar.DECEMBER) {
            return PeriodText(getYearPeriodTitle(halfYearTitles[1], yearFrom))
        }

        // Первый квартал
        if (monthFrom == Calendar.JANUARY && monthTo == Calendar.MARCH) {
            return PeriodText(getYearPeriodTitle(quarterTitles[0], yearFrom))
        }

        // Второй квартал
        if (monthFrom == Calendar.APRIL && monthTo == Calendar.JUNE) {
            return PeriodText(getYearPeriodTitle(quarterTitles[1], yearFrom))
        }

        // Третий квартал
        if (monthFrom == Calendar.JULY && monthTo == Calendar.SEPTEMBER) {
            return PeriodText(getYearPeriodTitle(quarterTitles[2], yearFrom))
        }

        // Четвертый квартал
        if (monthFrom == Calendar.OCTOBER && monthTo == Calendar.DECEMBER) {
            return PeriodText(getYearPeriodTitle(quarterTitles[3], yearFrom))
        }
    }

    if (dayFrom == 1 && dayTo == to.getActualMaximum(Calendar.DAY_OF_MONTH)) {
        // Месяц
        if (monthFrom == monthTo && yearFrom == yearTo) {
            return PeriodText(getYearPeriodTitle(monthTitles[monthFrom], yearFrom))
            // Несколько месяцев
        } else {
            val fromText = StringBuilder()
                .append(monthTitles[monthFrom])
                .append(" ")
                .append(yearFrom)
                .toString()
            val toText = StringBuilder()
                .append(monthTitles[monthTo])
                .append(" ")
                .append(yearTo)
                .toString()

            return PeriodText(fromText, toText)
        }
    }

    return PeriodText(
        DateFormatUtils.format(from.time, DateFormatTemplate.LONG_DATE_WITHOUT_LEADING_ZERO),
        DateFormatUtils.format(to.time, DateFormatTemplate.LONG_DATE_WITHOUT_LEADING_ZERO)
    )
}

private fun getYearPeriodTitle(periodTitle: String, yearFrom: Int): String {
    return StringBuilder().append(periodTitle).append(" ").append(yearFrom).toString()
}

/**
 * Получение первого дня месяца с учетом диапазона календарной сетки
 * @param year год
 * @param month месяц
 * @param min нижняя граница календарной сетки
 */
fun getStartDayOfMonth(year: Int, month: Int, min: Calendar): Int {
    return if (year == min.year && month == min.month) min.dayOfMonth else 1
}

/**
 * Получение последнего дня месяца с учетом диапазона календарной сетки
 * @param year год
 * @param month месяц
 * @param max верхняя граница календарной сетки
 */
fun getEndDayOfMonth(year: Int, month: Int, max: Calendar): Int {
    return if (year == max.year && month == max.month) {
        max.dayOfMonth
    } else {
        GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}

/**
 * Получение первого месяца с учетом диапазона календарной сетки
 * @param year год
 * @param min нижняя граница календарной сетки
 */
fun getStartMonthOfYear(year: Int, min: Calendar): Int {
    return if (year == min.year) min.month else Calendar.JANUARY
}

/**
 * Получение последнего месяца с учетом диапазона календарной сетки
 * @param year год
 * @param max верхняя граница календарной сетки
 */
fun getEndMonthOfYear(year: Int, max: Calendar): Int {
    return if (year == max.year) max.month else Calendar.DECEMBER
}

/**
 * Метод, возвращающий текущую дату без учета времени
 */
fun getCurrentDay(): Calendar {
    val day = GregorianCalendar()
    day.set(Calendar.HOUR_OF_DAY, 0)
    day.set(Calendar.MINUTE, 0)
    day.set(Calendar.SECOND, 0)
    day.set(Calendar.MILLISECOND, 0)
    return day
}

/**
 * Сравнение двух дней
 * @param otherDay другой день
 * @return true - если день один и тот же
 */
infix fun Calendar.sameDay(otherDay: Calendar): Boolean {
    return year == otherDay.year
            && month == otherDay.month
            && dayOfMonth == otherDay.dayOfMonth
}

/**
 * Получение года
 */
val Calendar.year: Int
    get() = get(Calendar.YEAR)

/**
 * Получение месяца
 */
val Calendar.month: Int
    get() = get(Calendar.MONTH)

/**
 * Получение и изменение дня месяца
 */
var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) {
        set(Calendar.DAY_OF_MONTH, value)
    }

/**
 * Конвертация [Calendar] в [Day]
 */
fun Calendar.toDay() = Day(year, month, dayOfMonth)

/**
 * Конвертация [Calendar] в [Month]
 */
fun Calendar.toMonth() = Month(year, month)

/**
 * Получение номера дня недели
 * @return номер дня недели (0-6)
 */
val Calendar.dayOfWeek: Int
    get() {
        return when (get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> throw IllegalArgumentException("Неизвестный день недели")
        }
    }

/**
 * Конвертация [Day] в [Calendar]
 */
fun Day.toCalendar() = GregorianCalendar(year, month, day)

val Calendar.isMonday
    get() = get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY

val Calendar.isSunday
    get() = get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

/**@SelfDocumented*/
fun Calendar.withYearShift(yearShift: Int): Calendar {
    return (clone() as Calendar)
        .apply { set(Calendar.YEAR, year + yearShift) }
}
