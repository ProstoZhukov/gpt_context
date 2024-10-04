package ru.tensor.sbis.design.period_picker

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners.CalendarListener
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import java.util.GregorianCalendar

/**
 * Тестирование слушателя [CalendarListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MonthModeCalendarListenerTest {

    private val dispatch = mock<(MonthModePeriodPickerView.Event) -> Unit>()

    private val listener = spy(CalendarListener(dispatch))

    private val date = GregorianCalendar(2024, 0, 15).removeTime()

    private val dateFrom = GregorianCalendar(2023, 8, 1)
    private val dateTo = GregorianCalendar(2023, 8, 30)

    @Test
    fun `When onClickItem method is called for day view then dispatch invokes`() {
        listener.onClickItem(date, false)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onClickItem method is called for month label then dispatch invokes`() {
        listener.onClickItem(date, true)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onUpdateCounters method is called then dispatch invokes`() {
        listener.onUpdateCounters(dateFrom, dateTo)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onReloadCalendar method is called for the next page then dispatch invokes`() {
        listener.onReloadCalendar(true)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onReloadCalendar method is called for the previous page then dispatch invokes`() {
        listener.onReloadCalendar(false)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onResetSelectionPeriod method is called then dispatch invokes`() {
        listener.onResetSelectionPeriod()

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onSelectPeriod method is called then dispatch invokes`() {
        listener.onSelectPeriod(dateFrom, dateTo)

        verify(dispatch).invoke(any())
    }
}