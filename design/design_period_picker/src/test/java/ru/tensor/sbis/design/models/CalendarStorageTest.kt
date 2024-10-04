package ru.tensor.sbis.design.models

import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayModel
import ru.tensor.sbis.design.period_picker.view.utils.MAX_DATE
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.lastDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Тестирование [CalendarStorage].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CalendarStorageTest {

    private val counters = 5

    private val startMonth = GregorianCalendar(2024, 0, 1).removeTime()
    private val endMonth = GregorianCalendar(2024, 0, 31).removeTime()

    private val startYear = GregorianCalendar(2025, 0, 1).removeTime()
    private val endYear = GregorianCalendar(2025, 11, 31).removeTime()

    private val date = GregorianCalendar(2024, 0, 15).removeTime()

    private val daysStorageSpy: CalendarStorage = spy(
        generateDaysStorage(startMonth, endMonth)
    )

    private val quantaStorageSpy: CalendarStorage = spy(
        CalendarStorage().apply {
            generateQuanta(startYear, endYear, SbisPeriodPickerRange())
            hasYearMode = true
        }
    )

    @Test
    fun `Method generateDays returns data to create a grid of days for month mode`() {
        val storage = generateDaysStorage(startMonth, endMonth)
        val size = startMonth.lastDayOfMonth + 1

        assert(storage.dayGrid.size == size)
    }

    @Test
    fun `Method generateQuanta returns data to create a grid of quanta for year mode`() {
        val months = 12
        val quarters = 4
        val halfYears = 2
        val years = 1
        val yearLabels = MAX_DATE.year - MIN_DATE.year + 1

        assert(quantaStorageSpy.monthGrid.size == months)
        assert(quantaStorageSpy.quarterGrid.size == quarters)
        assert(quantaStorageSpy.halfYearGrid.size == halfYears)
        assert(quantaStorageSpy.yearGrid.size == years)
        assert(quantaStorageSpy.yearLabelsGrid.size == yearLabels)
    }

    @Test
    fun `When data is added to the end of storage then storage increases`() {
        val storage = generateDaysStorage(startMonth, endMonth)
        var size = storage.dayGrid.size

        val start = GregorianCalendar(2024, 1, 1)
        val end = GregorianCalendar(2024, 1, 29)
        val newStorage = generateDaysStorage(start, end)
        size += newStorage.dayGrid.size

        storage.addDataToStorage(newStorage, true)

        assert(storage.dayGrid.size == size)
    }

    @Test
    fun `When data is added to the beginning of storage then storage increases`() {
        val start = GregorianCalendar(2024, 1, 1)
        val end = GregorianCalendar(2024, 1, 29)
        val storage = generateDaysStorage(start, end)
        var size = storage.dayGrid.size

        val newStorage = generateDaysStorage(startMonth, endMonth)
        size += newStorage.dayGrid.size

        storage.addDataToStorage(newStorage, false)

        assert(storage.dayGrid.size == size)
    }

    @Test
    fun `When start date is null then period is not selected`() {
        daysStorageSpy.selectPeriod(null, null)

        verify(daysStorageSpy, never()).selectDays(startMonth, endMonth)
    }

    @Test
    fun `When year mode is not available and period is one day then selectRange method has never been called`() {
        daysStorageSpy.selectPeriod(date, date)

        verify(daysStorageSpy).selectDays(date, date)
        verify(daysStorageSpy, never()).selectRange(any(), any())
        verify(daysStorageSpy).selectDay(any(), any())
    }

    @Test
    fun `When year mode is not available and period is a range then selectDay method has never been called`() {
        val newDate = GregorianCalendar(2024, 0, 22).removeTime()

        daysStorageSpy.selectPeriod(date, newDate)

        verify(daysStorageSpy).selectDays(any(), any())
        verify(daysStorageSpy).selectRange(any(), any())
        verify(daysStorageSpy, never()).selectDay(any(), any())
    }

    @Test
    fun `When year mode is not available and end date is null then selectRange method has never been called`() {
        daysStorageSpy.selectPeriod(date, null)

        verify(daysStorageSpy).selectDays(date, null)
        verify(daysStorageSpy, never()).selectRange(any(), any())
        verify(daysStorageSpy).selectDay(any(), any())
    }

    @Test
    fun `When year mode is not available and start date is more than end date then nothing is done`() {
        val newDate = GregorianCalendar(2024, 0, 14).removeTime()

        daysStorageSpy.selectPeriod(date, newDate)

        verify(daysStorageSpy).selectDays(any(), any())
        verify(daysStorageSpy, never()).selectRange(any(), any())
        verify(daysStorageSpy, never()).selectDay(any(), any())
    }

    @Test
    fun `When year mode is available and period is one day then selectQuantum method is called`() {
        val storageSpy: CalendarStorage = spy(
            generateDaysStorage(startMonth, endMonth).apply {
                hasYearMode = true
            }
        )

        storageSpy.selectPeriod(date, date)

        verify(storageSpy).selectDays(any(), any())
        verify(storageSpy).selectQuantum(any(), any())
    }

    @Test
    @DisplayName(
        "When year mode is available and end date is null " +
            "then selectYear, selectHalfYear, selectQuarter and selectMonths methods have never been called"
    )
    fun selectQuantum() {
        quantaStorageSpy.selectPeriod(startYear, null)

        verify(quantaStorageSpy).selectDays(startYear, null)
        verify(quantaStorageSpy).selectQuantum(startYear, null)
        verify(quantaStorageSpy, never()).selectYear(any(), any())
        verify(quantaStorageSpy, never()).selectHalfYear(any(), any())
        verify(quantaStorageSpy, never()).selectQuarter(any(), any())
        verify(quantaStorageSpy, never()).selectMonths(any(), any())
    }

    @Test
    fun `When year mode is available then selectYear method is called`() {
        quantaStorageSpy.selectPeriod(startYear, endYear)

        verify(quantaStorageSpy).selectDays(any(), any())
        verify(quantaStorageSpy).selectQuantum(any(), any())
        verify(quantaStorageSpy).selectYear(any(), any())
    }

    @Test
    fun `When end date is null then selectYear method is performed`() {
        quantaStorageSpy.selectYear(startMonth, null)

        verify(quantaStorageSpy, times(1)).selectYear(startMonth, null)
    }

    @Test
    fun `When year mode is available and period is a quarter then selectQuarter method is called`() {
        val end = GregorianCalendar(2025, 2, 31).removeTime()

        quantaStorageSpy.selectPeriod(startYear, end)

        verify(quantaStorageSpy).selectDays(any(), any())
        verify(quantaStorageSpy).selectQuantum(any(), any())
        verify(quantaStorageSpy).selectQuarter(any(), any())
    }

    @Test
    fun `When year mode is available and period is a half year then selectHalfYear method is called`() {
        val end = GregorianCalendar(2025, 5, 30).removeTime()

        quantaStorageSpy.selectPeriod(startYear, end)

        verify(quantaStorageSpy).selectDays(any(), any())
        verify(quantaStorageSpy).selectQuantum(any(), any())
        verify(quantaStorageSpy).selectHalfYear(any(), any())
    }

    @Test
    fun `When year mode is available and period is a month then selectMonths method is called`() {
        val start = GregorianCalendar(2025, 5, 1).removeTime()
        val end = GregorianCalendar(2025, 5, 30).removeTime()

        quantaStorageSpy.selectPeriod(start, end)

        verify(quantaStorageSpy).selectDays(any(), any())
        verify(quantaStorageSpy).selectQuantum(any(), any())
        verify(quantaStorageSpy).selectMonths(any(), any())
    }

    @Test
    fun `When year mode is available and start and end dates are not null then deselectPeriod method returns true`() {
        val endCalendar = GregorianCalendar(2026, 11, 31)

        val storageSpy: CalendarStorage = spy(
            generateDaysStorage(startMonth, endCalendar).apply {
                hasYearMode = true
            }
        )

        val start = GregorianCalendar(2024, 4, 6)
        val end = GregorianCalendar(2026, 9, 21)

        val isDeselected = storageSpy.deselectPeriod(start, end)

        assert(isDeselected)
    }

    @Test
    @DisplayName(
        "When year mode is not available and start and end dates are not null " +
            "then deselectPeriod method returns true"
    )
    fun deselectNullPeriod() {
        val isDeselected = daysStorageSpy.deselectPeriod(date, date)

        assert(isDeselected)
    }

    @Test
    fun `When year mode is not available and start and end dates are null then deselectPeriod method returns false`() {
        val isDeselected = daysStorageSpy.deselectPeriod(null, null)

        assert(!isDeselected)
    }

    @Test
    fun `When a specific year label is marked then isMarked returns true`() {
        val storageSpy: CalendarStorage = spy(
            CalendarStorage().apply {
                generateQuanta(startMonth, endMonth, SbisPeriodPickerRange())
            }
        )

        storageSpy.updateMarkedYears(1900)

        assert(storageSpy.yearLabelsGrid[0].isMarked)
    }

    @Test
    @DisplayName(
        "When a specific year label is marked and new year label is marked " +
            "then isMarked returns true for the new year"
    )
    fun updateMarkedYears() {
        quantaStorageSpy.updateMarkedYears(1900)

        assert(quantaStorageSpy.yearLabelsGrid[0].isMarked)

        quantaStorageSpy.updateMarkedYears(1901)

        assert(!quantaStorageSpy.yearLabelsGrid[0].isMarked)
        assert(quantaStorageSpy.yearLabelsGrid[1].isMarked)
    }

    @Test
    fun `When set counters for specific day then this day has these counters`() {
        val lastItem = daysStorageSpy.dayGrid.last
        val date = lastItem.date

        daysStorageSpy.setCounters(mapOf(date to counters))

        assert((lastItem as DayModel).counter == counters.toString())
    }

    @Test
    fun `When set counters for specific day and reset it then this day does not have these counters`() {
        val lastItem = daysStorageSpy.dayGrid.last
        val date = lastItem.date

        daysStorageSpy.setCounters(mapOf(date to counters))

        assert((lastItem as DayModel).counter == counters.toString())

        daysStorageSpy.resetCounters(setOf(date))

        assert(lastItem.counter == "")
    }

    @Test
    fun `Method getYearModeCalendar returns items for year mode`() {
        val calendar = quantaStorageSpy.getYearModeCalendar()

        assert(calendar.size == 2)
    }

    private fun generateDaysStorage(min: Calendar, max: Calendar) = CalendarStorage().apply {
        generateDays(min, max, SbisPeriodPickerRange(), MarkerType.NO_MARKER, null) { SbisPeriodPickerDayCustomTheme() }
    }
}