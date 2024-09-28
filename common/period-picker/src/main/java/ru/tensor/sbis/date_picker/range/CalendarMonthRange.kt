package ru.tensor.sbis.date_picker.range

import ru.tensor.sbis.date_picker.month
import ru.tensor.sbis.date_picker.year
import java.util.*

/**
 * Интервал месяцев от [start] до [endInclusive]
 *
 * @author mb.kruglova
 */
class CalendarMonthRange(override val start: Calendar, override val endInclusive: Calendar) : ClosedRange<Calendar>,
    Iterable<Calendar> {
    override fun iterator() = CalendarMonthIterator(start, endInclusive)
}

/**
 * Итератор по месяцам
 *
 * @author mb.kruglova
 */
class CalendarMonthIterator(start: Calendar, private val endInclusive: Calendar) : Iterator<Calendar> {

    private var current = start.clone() as Calendar

    override fun hasNext() =
        current.year < endInclusive.year ||
                current.year == endInclusive.year && current.month <= endInclusive.month

    override fun next(): Calendar {
        val next = current.clone() as Calendar
        current.add(Calendar.MONTH, 1)
        return next
    }
}

fun Calendar.monthRangeTo(that: Calendar) = CalendarMonthRange(this, that)