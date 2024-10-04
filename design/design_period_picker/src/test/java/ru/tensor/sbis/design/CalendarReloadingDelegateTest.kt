package ru.tensor.sbis.design

import androidx.recyclerview.widget.LinearLayoutManager
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.adapter.CalendarReloadingProvider
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarReloadingDelegate

/**
 * Тестирование делегата [CalendarReloadingDelegate].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CalendarReloadingDelegateTest {

    private val provider: CalendarReloadingProvider = mock()
    private val manager: LinearLayoutManager = mock()
    private val delegate: CalendarReloadingDelegate = CalendarReloadingDelegate(provider, manager)

    private val amount = 10

    @Test
    fun `When data amount is more than 0 while scrolling down then calendar is reloading`() {
        delegate.reloadData(amount, 0)

        verify(provider).performCalendarReloading(true)
    }

    @Test
    fun `When data amount is more than 0 while scrolling up then calendar is reloading`() {
        delegate.reloadData(amount, 0)

        verify(provider, never()).performCalendarReloading(false)
    }

    @Test
    fun `When data amount is less than 0 while scrolling up then calendar is reloading`() {
        delegate.reloadData(-amount, 0)

        verify(provider).performCalendarReloading(false)
    }

    @Test
    fun `When data amount is less than 0 while scrolling down then calendar is reloading`() {
        delegate.reloadData(-amount, 0)

        verify(provider, never()).performCalendarReloading(true)
    }
}