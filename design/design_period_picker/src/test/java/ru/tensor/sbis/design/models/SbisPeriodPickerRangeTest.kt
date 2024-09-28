package ru.tensor.sbis.design.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.GregorianCalendar

/**
 * Тестирование [SbisPeriodPickerRange].
 *
 * @author mb.kruglova
 */
@RunWith(JUnit4::class)
class SbisPeriodPickerRangeTest {

    @Test
    fun `By default SbisPeriodPickerRange has MIN_DATE start date and MAX_DATE end date`() {
        val start = MIN_DATE
        val end = MAX_DATE
        val range = SbisPeriodPickerRange()

        assertEquals(range.startDate?.timeInMillis, start.timeInMillis)
        assertEquals(range.endDate?.timeInMillis, end.timeInMillis)
        assertEquals(range.start.timeInMillis, start.timeInMillis)
        assertEquals(range.end.timeInMillis, end.timeInMillis)
        assertEquals(range.startYear, start.year)
        assertEquals(range.endYear, end.year)
        assertEquals(range.startMonth, start.month)
        assertEquals(range.endMonth, end.month)
        assertEquals(range.startDayOfMonth, start.dayOfMonth)
        assertEquals(range.endDayOfMonth, end.dayOfMonth)
    }

    @Test
    @DisplayName(
        "When start and end dates are null for SbisPeriodPickerRange " +
            "then startDate and endDate are null and start and end are not null"
    )
    fun getSbisPeriodPickerRangeForNull() {
        val start = MIN_DATE
        val end = MAX_DATE
        val range = SbisPeriodPickerRange(null, null)

        assertNull(range.startDate)
        assertNull(range.endDate)
        assertEquals(range.start.timeInMillis, start.timeInMillis)
        assertEquals(range.end.timeInMillis, end.timeInMillis)
        assertEquals(range.startYear, start.year)
        assertEquals(range.endYear, end.year)
        assertEquals(range.startMonth, start.month)
        assertEquals(range.endMonth, end.month)
        assertEquals(range.startDayOfMonth, start.dayOfMonth)
        assertEquals(range.endDayOfMonth, end.dayOfMonth)
    }

    @Test
    @DisplayName(
        "When start and end dates are not null for SbisPeriodPickerRange " +
            "then startDate, endDate, start and end are not null"
    )
    fun getSbisPeriodPickerRange() {
        val start = GregorianCalendar(2024, 1, 20)
        val end = GregorianCalendar(2024, 10, 20)
        val range = SbisPeriodPickerRange(start, end)

        assertEquals(range.startDate?.timeInMillis, start.timeInMillis)
        assertEquals(range.endDate?.timeInMillis, end.timeInMillis)
        assertEquals(range.start.timeInMillis, start.timeInMillis)
        assertEquals(range.end.timeInMillis, end.timeInMillis)
        assertEquals(range.startYear, start.year)
        assertEquals(range.endYear, end.year)
        assertEquals(range.startMonth, start.month)
        assertEquals(range.endMonth, end.month)
        assertEquals(range.startDayOfMonth, start.dayOfMonth)
        assertEquals(range.endDayOfMonth, end.dayOfMonth)
    }
}