package ru.tensor.sbis.calendar.tests

import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.calendar.date.view.year.YearAdapter
import ru.tensor.sbis.calendar.date.view.year.YearAdapterDayPositionCalculator

/**
 * Тест для [YearAdapter].
 *
 */
class YearAdapterTest {

    private val yearAdapter = YearAdapter()
    private val yearAdapterDayPositionCalculator = YearAdapterDayPositionCalculator(LocalDate(1970, 1, 1))

    @Test
    fun getItemViewTypeTest() {
        // Для текущего дня - 10 лет
        try {
            yearAdapter.setFirstDate(LocalDate(2010, 1, 1), null)
        } catch (e: Exception) {
            // падение в notifyDataSetChanged()
        }
        for (position in listOf(0, 36, 65))
            Assert.assertEquals(YearAdapter.VIEW_TYPE_MONTH_NAME, yearAdapter.getItemViewType(position))

        for (position in 1..35)
            Assert.assertEquals(YearAdapter.VIEW_TYPE_DAY, yearAdapter.getItemViewType(position))
    }

    @Test
    fun monthByPositionTest() {
        for (position in 0..35)
            Assert.assertEquals(1, yearAdapterDayPositionCalculator.monthByPosition(position).monthOfYear)
        for (position in 36..71)
            Assert.assertEquals(2, yearAdapterDayPositionCalculator.monthByPosition(position).monthOfYear)


        // тоже самое
        for (position in 0..35)
            Assert.assertEquals(1, yearAdapterDayPositionCalculator.monthByPosition(position).monthOfYear)
        for (position in 36..71)
            Assert.assertEquals(2, yearAdapterDayPositionCalculator.monthByPosition(position).monthOfYear)

        Assert.assertEquals(2, yearAdapterDayPositionCalculator.monthByPosition(5000).monthOfYear)
    }


}