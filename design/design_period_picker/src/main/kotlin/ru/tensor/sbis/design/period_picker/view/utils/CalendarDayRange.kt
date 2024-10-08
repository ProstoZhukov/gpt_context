package ru.tensor.sbis.design.period_picker.view.utils

import java.util.Calendar

/**
 * Интервал дней от [start] до [endInclusive]
 *
 * @author mb.kruglova
 */
class CalendarDayRange(override val start: Calendar, override val endInclusive: Calendar) :
    ClosedRange<Calendar>,
    Iterable<Calendar> {
    override fun iterator() = CalendarDayIterator(start, endInclusive)
}

/**
 * Итератор по дням
 *
 * @author mb.kruglova
 */
class CalendarDayIterator(start: Calendar, private val endInclusive: Calendar) : Iterator<Calendar> {

    private var current = start.clone() as Calendar

    override fun hasNext() = current <= endInclusive

    override fun next(): Calendar {
        val next = current.clone() as Calendar
        current.add(Calendar.DATE, 1)
        return next
    }
}