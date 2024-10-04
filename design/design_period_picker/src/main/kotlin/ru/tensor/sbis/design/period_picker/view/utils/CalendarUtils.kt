package ru.tensor.sbis.design.period_picker.view.utils

import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumPosition
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType
import java.util.Calendar
import java.util.Calendar.*
import java.util.GregorianCalendar

/** Минимально возможная дата календаря. */
internal val MIN_DATE = GregorianCalendar(1900, 0, 1)

/** Максимально возможная дата календаря. */
internal val MAX_DATE = GregorianCalendar(2100, 11, 31)

/** Высота компонента в долях. */
internal const val heightFraction = 0.66

/** Высота компонента в процентах. */
internal const val heightPercent = 66

// Количество месяцев.
private const val MONTH_AMOUNT = 4

// Количество лет.
private const val YEAR_AMOUNT = 3

// Первый день в году.
internal const val firstDay = 1

// Последний день в году.
internal const val lastDay = 31

// Диапазон месяцев.
internal val monthRange = JANUARY..DECEMBER

// Диапазон месяцев в первом квартале.
internal val quarter1Range = JANUARY..MARCH

// Диапазон месяцев во втором квартале.
internal val quarter2Range = APRIL..JUNE

// Диапазон месяцев в третьем квартале.
internal val quarter3Range = JULY..SEPTEMBER

// Диапазон месяцев в четвертом квартале.
internal val quarter4Range = OCTOBER..DECEMBER

// Список месяцев начала кварталов.
internal val startQuarterMonths = listOf(JANUARY, APRIL, JULY, OCTOBER)

// Список месяцев окончания кварталов.
internal val endQuarterMonths = listOf(MARCH, JUNE, SEPTEMBER, DECEMBER)

// Список месяцев начала полугодий.
internal val startHalfYearMonths = listOf(JANUARY, JULY)

// Первый месяц в году.
internal const val firstMonthOfYear = JANUARY

// Последний месяц в году.
internal const val lastMonthOfYear = DECEMBER

// Количество дней в неделе.
internal const val weekdays = 7

// Кратность месяцев в квартале.
internal const val quarterMultiplicity = 3

// Кратность месяцев в полугодии.
internal const val halfYearMultiplicity = 6

internal const val monthStep = 0
internal const val quarterStep = 2
internal const val halfYearStep = 5
internal const val yearStep = 11

internal val halfYearRange = 1..2
internal val quarterRange = 1..4

/**@SelfDocumented*/
internal fun getCalendarFromMillis(millis: Long?): Calendar? {
    if (millis == null || millis == MIN_DATE.timeInMillis) return null

    val calendar = getInstance()
    calendar.timeInMillis = millis
    return calendar
}

/** Сопоставить номер месяца и StringResId. */
internal fun mapMonthToStringResId(month: Int): Int {
    return when (month) {
        JANUARY -> R.string.january
        FEBRUARY -> R.string.february
        MARCH -> R.string.march
        APRIL -> R.string.april
        MAY -> R.string.may
        JUNE -> R.string.june
        JULY -> R.string.july
        AUGUST -> R.string.august
        SEPTEMBER -> R.string.september
        OCTOBER -> R.string.october
        NOVEMBER -> R.string.november
        DECEMBER -> R.string.december
        else -> throw IllegalArgumentException()
    }
}

/**
 * Получение первого дня месяца с учетом диапазона календарной сетки.
 * @param year год.
 * @param month месяц.
 * @param min нижняя граница календарной сетки.
 */
internal fun getStartDayOfMonth(year: Int, month: Int, min: Calendar): Int {
    return if (year == min.year && month == min.month) min.dayOfMonth else 1
}

/**
 * Получение последнего дня месяца с учетом диапазона календарной сетки.
 * @param year год.
 * @param month месяц.
 * @param max верхняя граница календарной сетки.
 */
internal fun getEndDayOfMonth(year: Int, month: Int, max: Calendar): Int {
    return if (year == max.year && month == max.month) {
        max.dayOfMonth
    } else {
        GregorianCalendar(year, month, 1).getActualMaximum(DAY_OF_MONTH)
    }
}

/**
 * Получение первого месяца с учетом диапазона календарной сетки.
 * @param year год.
 * @param min нижняя граница календарной сетки.
 */
internal fun getStartMonthOfYear(year: Int, min: Calendar): Int {
    return if (year == min.year) min.month else JANUARY
}

/**
 * Получение последнего месяца с учетом диапазона календарной сетки.
 * @param year год.
 * @param max верхняя граница календарной сетки.
 */
internal fun getEndMonthOfYear(year: Int, max: Calendar): Int {
    return if (year == max.year) max.month else DECEMBER
}

/**
 * Получение года.
 */
internal val Calendar.year: Int
    get() = get(YEAR)

/**
 * Получение месяца.
 */
internal val Calendar.month: Int
    get() = get(MONTH)

/**
 * Получение и изменение дня месяца.
 */
internal var Calendar.dayOfMonth: Int
    get() = get(DAY_OF_MONTH)
    set(value) {
        set(DAY_OF_MONTH, value)
    }

/**
 * Получение номера дня недели.
 * @return номер дня недели (0-6).
 */
internal val Calendar.dayOfWeek: Int
    get() {
        return when (get(DAY_OF_WEEK)) {
            MONDAY -> 0
            TUESDAY -> 1
            WEDNESDAY -> 2
            THURSDAY -> 3
            FRIDAY -> 4
            SATURDAY -> 5
            else -> 6
        }
    }

/** @SelfDocumented */
internal val Calendar.isMonday
    get() = get(DAY_OF_WEEK) == MONDAY

/** @SelfDocumented */
internal val Calendar.isSunday
    get() = get(DAY_OF_WEEK) == SUNDAY

/** @SelfDocumented */
internal val Calendar.firstDayOfMonth
    get() = getActualMinimum(DAY_OF_MONTH)

/** @SelfDocumented */
internal val Calendar.lastDayOfMonth
    get() = getActualMaximum(DAY_OF_MONTH)

/** @SelfDocumented */
internal fun isCurrentYear(year: Int) = getInstance().year == year

/** @SelfDocumented */
internal operator fun Calendar.rangeTo(that: Calendar) = CalendarDayRange(this, that)

/** @SelfDocumented */
internal fun Calendar.removeTime(): Calendar {
    this[HOUR] = 0
    this[MINUTE] = 0
    this[SECOND] = 0
    this[MILLISECOND] = 0
    this[AM_PM] = 0
    return this
}

/** Получить отформатированный заголовок месяца. */
internal fun getFormattedMonthLabel(month: String, year: Int) = String.format(
    "%s\'%s",
    month,
    year.toString().substring(2, 4)
)

/** Получить ключ года. */
internal fun getYearKey(year: Int): Calendar {
    return getMonthKey(year, 0)
}

/** Получить ключ месяца. */
internal fun getMonthKey(year: Int, month: Int): Calendar {
    return GregorianCalendar(year, month, 1)
}

/** Получить ключ дня. */
internal fun getDayKey(year: Int, month: Int, dayOfMonth: Int): Calendar {
    return GregorianCalendar(year, month, dayOfMonth)
}

/**
 * Получить новую дату.
 *
 * Новая дата получается добавлением к текущей или отниманием от текущей определенного количества месяцев(MONTH_AMOUNT)
 * для компактного вида или лет (YEAR_AMOUNT) в противном случае.
 * В случае добавления день месяца выставляется в максимально возможный в этом месяце.
 * В случае отнимания день месяца выставляется в минимальной возможный в этом месяце (1 число).
 *
 * Новое число не может быть больше/меньше установленных границ.
 *
 * @param date текущая дата.
 * @param limit граница для даты.
 * @param isPositive свойство, определяющее, что мы будем делать с текущей датой: если true, то будем добавлять месяцы,
 * если false, то будем отнимать месяцы.
 * @param isCompact свойство, определяющее компактный вид или нет.
 */
internal fun getNextDate(
    date: Calendar,
    limit: Calendar,
    isPositive: Boolean,
    isCompact: Boolean
): Calendar {
    val newDate = if (isCompact) {
        (date.clone() as Calendar).apply {
            set(DAY_OF_MONTH, firstDay)
            add(MONTH, if (isPositive) MONTH_AMOUNT else -MONTH_AMOUNT)
            if (isPositive) set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH))
        }
    } else {
        (date.clone() as Calendar).apply {
            set(DAY_OF_MONTH, if (isPositive) lastDay else firstDay)
            set(MONTH, if (isPositive) DECEMBER else JANUARY)
            add(YEAR, if (isPositive) YEAR_AMOUNT else -YEAR_AMOUNT)
        }
    }

    return if (isPositive && newDate.timeInMillis > limit.timeInMillis ||
        !isPositive && newDate.timeInMillis < limit.timeInMillis
    ) {
        (limit.clone() as Calendar).apply {
            if (isPositive) {
                set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH))
            } else {
                set(DAY_OF_MONTH, firstDay)
            }
        }
    } else {
        newDate.removeTime()
    }
}

/** Получить квартал, к которому принадлежит дата. */
internal fun Calendar.getQuarter(): Int {
    return (this.month / 3) + 1
}

/** Получить полугодие, к которому принадлежит дата. */
internal fun Calendar.getHalfYear(): Int {
    return (this.month / 6) + 1
}

/** @SelfDocumented */
internal fun getLastDateOfYear(year: Int): Calendar = GregorianCalendar(year, DECEMBER, lastDay)

/** Проверить принадлежность месяцев к доступному для отображения периоду. */
internal fun checkRangeBelonging(
    limit: SbisPeriodPickerRange,
    month: Int,
    year: Int,
    monthStep: Int
): Boolean {
    val lastDayOfMonth = limit.end.lastDayOfMonth
    var isRangePart = true

    if (year in limit.startYear..limit.endYear) {
        checkEnabled@ for (m in month..(month + monthStep)) {
            val enabledStartMonth = checkStartLimit(m, limit.startMonth, limit.startDayOfMonth)
            val enabledEndMonth = checkEndLimit(m, limit.endMonth, limit.endDayOfMonth, lastDayOfMonth)
            isRangePart = (limit.startYear == limit.endYear && enabledStartMonth && enabledEndMonth) ||
                (limit.startYear < limit.endYear && year == limit.startYear && enabledStartMonth) ||
                (limit.startYear < limit.endYear && year == limit.endYear && enabledEndMonth) ||
                (year > limit.startYear && year < limit.endYear)
            if (!isRangePart) break@checkEnabled
        }
    } else {
        isRangePart = false
    }

    return isRangePart
}

/**
 * Проверить квант, что он равен одному дню, или одному месяцу, или одному кварталу, или одному полугодию,
 * или одному году.
 */
internal fun checkQuantum(start: Calendar, end: Calendar): Boolean {
    if (start.year != end.year) return false

    val isDay = start.timeInMillis == end.timeInMillis
    if (isDay) return true

    val isMonth = start.month == end.month && start.dayOfMonth == firstDay && end.dayOfMonth == end.lastDayOfMonth
    if (isMonth) return true

    val isQuarter = startQuarterMonths.contains(start.month) && start.month + quarterStep == end.month &&
        start.dayOfMonth == firstDay && end.dayOfMonth == end.lastDayOfMonth
    if (isQuarter) return true

    val isHalfYear = startHalfYearMonths.contains(start.month) && start.month + halfYearStep == end.month &&
        start.dayOfMonth == firstDay && end.dayOfMonth == end.lastDayOfMonth
    if (isHalfYear) return true

    val isYear = start.month == firstMonthOfYear && end.month == lastMonthOfYear &&
        start.dayOfMonth == firstDay && end.dayOfMonth == lastDay

    return isYear
}

/**
 * Проверить выбранный период, чтобы начальная дата была меньше или равна конечной,
 * а также чтобы период не выходил за пределы максимальных значений.
 */
internal fun checkPeriod(start: Calendar?, end: Calendar?, startLimit: Calendar, endLimit: Calendar): Boolean {
    return start != null && end != null &&
        start.timeInMillis <= end.timeInMillis &&
        start.timeInMillis >= startLimit.timeInMillis &&
        end.timeInMillis <= endLimit.timeInMillis
}

/** Проверить не выходит ли месяц за границу начального значения доступного для отображения периода. */
private fun checkStartLimit(
    month: Int,
    limitStartMonth: Int,
    limitStartDayOfMonth: Int
): Boolean {
    return month > limitStartMonth || (month == limitStartMonth && limitStartDayOfMonth == firstDay)
}

/** Проверить не выходит ли месяц за границу конечного значения доступного для отображения периода. */
private fun checkEndLimit(
    month: Int,
    limitEndMonth: Int,
    limitEndDayOfMonth: Int,
    lastDayOfMonth: Int
): Boolean {
    return month < limitEndMonth || (month == limitEndMonth && limitEndDayOfMonth == lastDayOfMonth)
}

/** Получить дату, к которой скроллируем календарь. */
internal fun getDateToScroll(
    defaultScrollDate: Calendar?,
    limitStart: Calendar,
    limitEnd: Calendar,
    isBottom: Boolean
): Calendar {
    if (defaultScrollDate == null) {
        val currentDate = getInstance().removeTime()
        return when {
            currentDate.timeInMillis in limitStart.timeInMillis..limitEnd.timeInMillis -> currentDate
            isBottom -> limitEnd
            else -> limitStart
        }
    }

    return defaultScrollDate
}

/** Получить тип кванта. */
internal fun Calendar.getType(dateFrom: Calendar, dateTo: Calendar, isYear: Boolean = false): QuantumType {
    return when {
        isYear -> QuantumType.SINGLE
        dateFrom == dateTo -> QuantumType.STANDARD
        this == dateFrom -> QuantumType.START
        this == dateTo -> QuantumType.END
        else -> QuantumType.STANDARD
    }
}

/** Получить позицию кванта в календаре относительно выбранного периода. */
internal fun Calendar.getPlacement(dateFrom: Calendar, dateTo: Calendar): QuantumPosition {
    return QuantumPosition(
        left = setLeftPlacement(dateFrom),
        top = setTopPlacement(dateFrom),
        right = setRightPlacement(dateTo),
        bottom = setBottomPlacement(dateTo)
    )
}

/** Настроить нахождение выбранных квантов сверху/снизу относительно текущего кванта для полугодий. */
internal fun Calendar.setHalfYearVerticalPlacement(): Boolean {
    return month !in quarter1Range && month !in quarter3Range
}

/** Получить период в один год. */
internal fun getYearRange(year: Int): SbisPeriodPickerRange {
    return SbisPeriodPickerRange(
        getYearKey(year),
        getLastDateOfYear(year)
    )
}

/** Настроить нахождение выбранных квантов слева относительно текущего кванта. */
private fun Calendar.setLeftPlacement(dateFrom: Calendar): Boolean {
    // Слева от января, апреля, июля и октября нет квантов.
    return if (startQuarterMonths.contains(month)) {
        false
    } // Слева от текущего дня есть другие дни, это не начало периода.
    else {
        this != dateFrom
    }
}

/** Настроить нахождение выбранных квантов сверху относительно текущего кванта. */
private fun Calendar.setTopPlacement(dateFrom: Calendar): Boolean {
    return if (month in quarter1Range) {
        false
    } else {
        dateFrom.year == year && dateFrom.month <= month - quarterMultiplicity || dateFrom.year != year
    }
}

/** Настроить нахождение выбранных квантов справа относительно текущего кванта. */
private fun Calendar.setRightPlacement(dateTo: Calendar): Boolean {
    // Справа от марта, июня, сентября и декабря нет квантов.
    return if (endQuarterMonths.contains(month)) {
        false
    } // Справа от текущего дня есть другие дни, если это не конец периода и не последний день месяца.
    else {
        this != dateTo
    }
}

/** Настроить нахождение выбранных квантов снизу относительно текущего кванта. */
private fun Calendar.setBottomPlacement(dateTo: Calendar): Boolean {
    return if (month in quarter4Range) {
        false
    } else {
        dateTo.year == year && dateTo.month >= month + quarterMultiplicity || dateTo.year != year
    }
}