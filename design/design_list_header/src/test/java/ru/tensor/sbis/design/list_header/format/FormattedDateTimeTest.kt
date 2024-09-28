package ru.tensor.sbis.design.list_header.format

import org.junit.Assert
import org.junit.Test

/**
 * Тест для FormattedDateTime
 * @author Roman Petrov (ra.petrov)
 */
class FormattedDateTimeTest {

    companion object {
        private const val DATE = "01.05.2021"
        private const val TIME = "15.54"
    }

    @Test
    fun `When pass values to constructor then get the same`() {
        val value = FormattedDateTime(DATE, TIME)
        Assert.assertEquals(DATE, value.date)
        Assert.assertEquals(TIME, value.time)
    }

    @Test
    fun equalsTest() {
        Assert.assertEquals(FormattedDateTime(DATE, TIME), FormattedDateTime(DATE, TIME))
    }
}