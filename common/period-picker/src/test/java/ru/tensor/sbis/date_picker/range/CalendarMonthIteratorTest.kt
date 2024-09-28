package ru.tensor.sbis.date_picker.range

import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.date_picker.month
import ru.tensor.sbis.date_picker.year
import java.util.*

class CalendarMonthIteratorTest {

    private val months = listOf(
        GregorianCalendar(2019, 8, 4),
        GregorianCalendar(2019, 9, 5),
        GregorianCalendar(2019, 10, 6),
        GregorianCalendar(2019, 11, 7),
        GregorianCalendar(2020, 0, 8)
    )

    @Test
    fun iteratorTestPositive() {
        val months = (GregorianCalendar(2019, 8, 1).monthRangeTo(GregorianCalendar(2020, 0, 31)).toList())
        assertEquals(this.months.size, months.size)

        val result = this.months
            .mapIndexed { index, month -> month.year == months[index].year && month.month == months[index].month }
            .reduce { result, element -> result && element }
        assertTrue(result)
    }

    @Test
    fun iteratorTestNegative() {
        val months = (GregorianCalendar(2018, 8, 1).monthRangeTo(GregorianCalendar(2019, 0, 31)).toList())
        assertEquals(this.months.size, months.size)

        val result = this.months
            .mapIndexed { index, month -> month.year == months[index].year && month.month == months[index].month }
            .reduce { result, element -> result && element }
        assertFalse(result)
    }
}