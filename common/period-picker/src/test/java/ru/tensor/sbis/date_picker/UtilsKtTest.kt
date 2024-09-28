package ru.tensor.sbis.date_picker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class UtilsKtTest {

    @Before
    fun setUp() {
        Locale.setDefault(Locale("ru", "RU"))
    }

    private val halfYearTitles = listOf("I полугодие", "II полугодие")
    private val quarterTitles = listOf("I квартал", "II квартал", "III квартал", "IV квартал")
    private val monthTitles = listOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )

    @Test
    fun prepareDateIntervalTitle_FirstHalfYearPositiveTest() {
        val dateFrom = GregorianCalendar(2017, 0, 1)
        val dateTo = GregorianCalendar(2017, 5, 30)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "I полугодие 2017")
    }

    @Test
    fun prepareDateIntervalTitle_FirstHalfYearNegativeTest() {
        val dateFrom = GregorianCalendar(2017, 0, 1)
        val dateTo = GregorianCalendar(2017, 4, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "I полугодие 2017")
    }

    @Test
    fun prepareDateIntervalTitle_SecondHalfYearPositiveTest() {
        val dateFrom = GregorianCalendar(2018, 6, 1)
        val dateTo = GregorianCalendar(2018, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "II полугодие 2018")
    }

    @Test
    fun prepareDateIntervalTitle_SecondHalfYearNegativeTest() {
        val dateFrom = GregorianCalendar(2018, 7, 1)
        val dateTo = GregorianCalendar(2018, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "II полугодие 2018")
    }

    @Test
    fun prepareDateIntervalTitle_FirstQuarterPositiveTest() {
        val dateFrom = GregorianCalendar(2018, 0, 1)
        val dateTo = GregorianCalendar(2018, 2, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "I квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_FirstQuarterNegativeTest() {
        val dateFrom = GregorianCalendar(2018, 0, 1)
        val dateTo = GregorianCalendar(2018, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "I квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_SecondQuarterPositiveTest() {
        val dateFrom = GregorianCalendar(2018, 3, 1)
        val dateTo = GregorianCalendar(2018, 5, 30)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "II квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_SecondQuarterNegativeTest() {
        val dateFrom = GregorianCalendar(2018, 3, 1)
        val dateTo = GregorianCalendar(2018, 5, 1)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "II квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_ThirdQuarterPositiveTest() {
        val dateFrom = GregorianCalendar(2018, 6, 1)
        val dateTo = GregorianCalendar(2018, 8, 30)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "III квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_ThirdQuarterNegativeTest() {
        val dateFrom = GregorianCalendar(2018, 6, 1)
        val dateTo = GregorianCalendar(2018, 8, 1)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "III квартал 2018")
    }

    @Test
    fun prepareDateIntervalTitle_FourQuarterPositiveTest() {
        val dateFrom = GregorianCalendar(2016, 9, 1)
        val dateTo = GregorianCalendar(2016, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "IV квартал 2016")
    }

    @Test
    fun prepareDateIntervalTitle_FourQuarterNegativeTest() {
        val dateFrom = GregorianCalendar(2016, 9, 1)
        val dateTo = GregorianCalendar(2016, 9, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "IV квартал 2016")
    }

    @Test
    fun prepareDateIntervalTitle_SingleDatePositiveTest() {
        val dateFrom = GregorianCalendar(2016, 9, 1)
        val dateTo = GregorianCalendar(2016, 9, 1)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "1 октября 2016")
    }

    @Test
    fun prepareDateIntervalTitle_CustomPeriodPositiveTest() {
        val dateFrom = GregorianCalendar(2016, 9, 1)
        val dateTo = GregorianCalendar(2016, 9, 10)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "1 октября 2016 - 10 октября 2016")
    }

    @Test
    fun prepareDateIntervalTitle_YearPositiveTest() {
        val dateFrom = GregorianCalendar(2000, 0, 1)
        val dateTo = GregorianCalendar(2000, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "2000")
    }

    @Test
    fun prepareDateIntervalTitle_YearNegativeTest() {
        val dateFrom = GregorianCalendar(2000, 0, 1)
        val dateTo = GregorianCalendar(2000, 5, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "2000")
    }

    @Test
    fun prepareDateIntervalTitle_YearsPositiveTest() {
        val dateFrom = GregorianCalendar(2000, 0, 1)
        val dateTo = GregorianCalendar(2002, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "2000 - 2002")
    }

    @Test
    fun prepareDateIntervalTitle_YearsNegativeTest() {
        val dateFrom = GregorianCalendar(2000, 0, 1)
        val dateTo = GregorianCalendar(2002, 5, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "2000 - 2002")
    }

    @Test
    fun prepareDateIntervalTitle_MonthPositiveTest() {
        val dateFrom = GregorianCalendar(2001, 11, 1)
        val dateTo = GregorianCalendar(2001, 11, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "Декабрь 2001")
    }

    @Test
    fun prepareDateIntervalTitle_MonthNegativeTest() {
        val dateFrom = GregorianCalendar(2001, 6, 1)
        val dateTo = GregorianCalendar(2001, 6, 31)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "Декабрь 2001")
    }

    @Test
    fun prepareDateIntervalTitle_MonthsPositiveTest() {
        val dateFrom = GregorianCalendar(2001, 11, 1)
        val dateTo = GregorianCalendar(2003, 3, 30)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertEquals(result, "Декабрь 2001 - Апрель 2003")
    }

    @Test
    fun prepareDateIntervalTitle_MonthsNegativeTest() {
        val dateFrom = GregorianCalendar(2001, 11, 5)
        val dateTo = GregorianCalendar(2003, 3, 30)
        val result = prepareDateIntervalTitle(halfYearTitles, quarterTitles, monthTitles, dateFrom, dateTo).toString()
        assertNotEquals(result, "Декабрь 2001 - Апрель 2003")
    }
}