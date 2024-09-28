package ru.tensor.sbis.date_picker.range

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.*

class CalendarDayRangeTest {

    private val week = listOf(
        GregorianCalendar(2019, 1, 4),
        GregorianCalendar(2019, 1, 5),
        GregorianCalendar(2019, 1, 6),
        GregorianCalendar(2019, 1, 7),
        GregorianCalendar(2019, 1, 8),
        GregorianCalendar(2019, 1, 9),
        GregorianCalendar(2019, 1, 10)
    )

    @Test
    fun iteratorTestPositive() {
        val days = (GregorianCalendar(2019, 1, 4)..GregorianCalendar(2019, 1, 10)).toList()
        assertEquals(week, days)
    }

    @Test
    fun iteratorTestNegative() {
        val days = (GregorianCalendar(2019, 1, 4)..GregorianCalendar(2019, 1, 11)).toList()
        assertNotEquals(week, days)
    }
}