package ru.tensor.sbis.design

import androidx.core.content.res.ResourcesCompat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarFromMillis
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import java.util.GregorianCalendar
import org.junit.Assert.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.checkPeriod
import ru.tensor.sbis.design.period_picker.view.utils.checkQuantum
import ru.tensor.sbis.design.period_picker.view.utils.getDateToScroll
import ru.tensor.sbis.design.period_picker.view.utils.getHalfYear
import ru.tensor.sbis.design.period_picker.view.utils.getLastDateOfYear
import ru.tensor.sbis.design.period_picker.view.utils.getNextDate
import ru.tensor.sbis.design.period_picker.view.utils.getPlacement
import ru.tensor.sbis.design.period_picker.view.utils.getQuarter
import ru.tensor.sbis.design.period_picker.view.utils.getType
import ru.tensor.sbis.design.period_picker.view.utils.mapMonthToStringResId
import ru.tensor.sbis.design.period_picker.view.utils.monthRange
import ru.tensor.sbis.design.period_picker.view.utils.setHalfYearVerticalPlacement
import java.util.Calendar

/**
 * Тестирование методов вспомогательного класса [CalendarUtils].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CalendarUtilsTest {

    private val date = GregorianCalendar(2024, 0, 15).removeTime()
    private val startLimit = GregorianCalendar(2024, 0, 1).removeTime()
    private val endLimit = GregorianCalendar(2024, 0, 31).removeTime()
    private val millis = 1705266000000

    private val date1 = GregorianCalendar(2024, 0, 15)
    private val date2 = GregorianCalendar(2024, 4, 15)
    private val date3 = GregorianCalendar(2024, 8, 15)
    private val date4 = GregorianCalendar(2024, 9, 15)

    private val year = 2024
    private val overMonth = 13

    @Test
    fun `Method getCalendarFromMillis returns date by millis`() {
        val date = getCalendarFromMillis(millis)

        assert(date?.timeInMillis == millis)
    }

    @Test
    fun `When millis is null then getCalendarFromMillis method returns null`() {
        val date = getCalendarFromMillis(null)

        assertNull(date)
    }

    @Test
    fun `When millis is MIN_DATE millis then getCalendarFromMillis method returns null`() {
        val date = getCalendarFromMillis(MIN_DATE.timeInMillis)

        assertNull(date)
    }

    @Test
    fun `When month is from 0 to 11 then mapMonthToStringResId method returns string resource`() {
        monthRange.forEach {
            val month = mapMonthToStringResId(it)
            assert(month != ResourcesCompat.ID_NULL)
        }
    }

    @Test
    fun `When month is over 11 then mapMonthToStringResId method returns illegal argument exception`() {
        assertThrows<IllegalArgumentException> { mapMonthToStringResId(overMonth) }
    }

    @Test
    fun `When it needs to get the previous date then getNextDate method returns the previous date`() {
        val nextDate = getNextDate(date, startLimit, isPositive = false, false)

        assert(nextDate.timeInMillis == startLimit.timeInMillis)
    }

    @Test
    fun `When it needs to get the next date then getNextDate method returns the next date`() {
        val nextDate = getNextDate(date, endLimit, isPositive = true, false)

        assert(nextDate.timeInMillis == endLimit.timeInMillis)
    }

    @Test
    fun `Method getQuarter returns a quarter which date belongs to`() {
        val quarter1 = date1.getQuarter()
        val quarter2 = date2.getQuarter()
        val quarter3 = date3.getQuarter()
        val quarter4 = date4.getQuarter()

        assert(quarter1 == 1)
        assert(quarter2 == 2)
        assert(quarter3 == 3)
        assert(quarter4 == 4)
    }

    @Test
    fun `Method getHalfYear returns a half year which date belongs to`() {
        val halfYear1 = date1.getHalfYear()
        val halfYear2 = date3.getHalfYear()

        assert(halfYear1 == 1)
        assert(halfYear2 == 2)
    }

    @Test
    fun `Method getLastDateOfYear returns the 31th of December by a specific year`() {
        val lastDay = getLastDateOfYear(year)
        val date = GregorianCalendar(year, 11, 31)

        assert(lastDay.timeInMillis == date.timeInMillis)
    }

    @Test
    fun `When a period is a month, a quarter, a half year or a year then this period is a quantum`() {
        var start = GregorianCalendar(2024, 0, 1).removeTime()
        var end = GregorianCalendar(2024, 0, 31).removeTime()
        var isQuantum = checkQuantum(start, end)

        assert(isQuantum)

        start = GregorianCalendar(2024, 0, 1).removeTime()
        end = GregorianCalendar(2024, 2, 31).removeTime()
        isQuantum = checkQuantum(start, end)

        assert(isQuantum)

        start = GregorianCalendar(2024, 0, 1).removeTime()
        end = GregorianCalendar(2024, 5, 30).removeTime()
        isQuantum = checkQuantum(start, end)

        assert(isQuantum)

        start = GregorianCalendar(2024, 0, 1).removeTime()
        end = GregorianCalendar(2024, 11, 31).removeTime()
        isQuantum = checkQuantum(start, end)

        assert(isQuantum)
    }

    @Test
    fun `When a period is random then this period is not a quantum`() {
        val start = GregorianCalendar(2024, 0, 1).removeTime()
        val end = GregorianCalendar(2024, 0, 30).removeTime()
        val isQuantum = checkQuantum(start, end)

        assert(!isQuantum)
    }

    @Test
    fun `When start date is less or equal to end date then period is correct`() {
        val start = GregorianCalendar(2024, 0, 14).removeTime()
        val end = GregorianCalendar(2024, 0, 16).removeTime()
        val isCorrect = checkPeriod(start, end, startLimit, endLimit)

        assert(isCorrect)
    }

    @Test
    fun `When start and end dates are null then period is not correct`() {
        val isCorrect = checkPeriod(null, null, startLimit, endLimit)

        assert(!isCorrect)
    }

    @Test
    fun `When start date is more than end date then period is not correct`() {
        val start = GregorianCalendar(2024, 0, 14).removeTime()
        val end = GregorianCalendar(2024, 1, 16).removeTime()
        val isCorrect = checkPeriod(start, end, startLimit, endLimit)

        assert(!isCorrect)
    }

    @Test
    fun `When current date does not belong to limited range then getDateToScroll method returns start limit date`() {
        val calendar = getDateToScroll(
            null,
            startLimit,
            endLimit,
            isBottom = false
        )

        assert(calendar.timeInMillis == startLimit.timeInMillis)
    }

    @Test
    fun `When current date does not belong to limited range then getDateToScroll method returns end limit date`() {
        val calendar = getDateToScroll(
            null,
            startLimit,
            endLimit,
            isBottom = true
        )

        assert(calendar.timeInMillis == endLimit.timeInMillis)
    }

    @Test
    fun `When current date belongs to limited range then getDateToScroll method returns current date`() {
        val calendar = getDateToScroll(
            null,
            MIN_DATE,
            MAX_DATE,
            isBottom = true
        )

        assert(calendar.timeInMillis == Calendar.getInstance().removeTime().timeInMillis)
    }

    @Test
    fun `When default date is not null then getDateToScroll method returns default date`() {
        val calendar = getDateToScroll(
            date,
            startLimit,
            endLimit,
            isBottom = true
        )

        assert(calendar.timeInMillis == date.timeInMillis)
    }

    @Test
    fun `Method getType returns QuantumType of date`() {
        var type = date.getType(startLimit, endLimit, true)

        assert(type == QuantumType.SINGLE)

        type = date.getType(startLimit, startLimit, false)

        assert(type == QuantumType.STANDARD)

        type = startLimit.getType(startLimit, endLimit, false)

        assert(type == QuantumType.START)

        type = endLimit.getType(startLimit, endLimit, false)

        assert(type == QuantumType.END)

        type = date.getType(startLimit, endLimit, false)

        assert(type == QuantumType.STANDARD)
    }

    @Test
    fun `Method getPlacement returns QuantumPosition of date`() {
        val placement = date.getPlacement(startLimit, endLimit)

        assert(!placement.bottom)
        assert(!placement.left)
        assert(placement.right)
        assert(!placement.top)
    }

    @Test
    fun `When month belongs to quarter 1 or quarter 3 then setHalfYearVerticalPlacement method returns false`() {
        val flag = date.setHalfYearVerticalPlacement()

        assert(!flag)
    }

    @Test
    @DisplayName(
        "When month does not belong to quarter 1 and quarter 3 " +
            "then setHalfYearVerticalPlacement method returns true"
    )
    fun setHalfYearVerticalPlacement() {
        val date = GregorianCalendar(2024, 3, 15).removeTime()
        val flag = date.setHalfYearVerticalPlacement()

        assert(flag)
    }
}