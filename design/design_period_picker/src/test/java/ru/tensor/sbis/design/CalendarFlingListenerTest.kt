package ru.tensor.sbis.design

import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.listener.CalendarFlingListener

/**
 * Тестирование слушателя [CalendarFlingListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CalendarFlingListenerTest {

    private val recyclerView: RecyclerView = mock()
    private val listener: CalendarFlingListener = spy(CalendarFlingListener(recyclerView))

    private val maxFling = 4000

    @Test
    fun `When velocity is more than 4000 while scrolling down then fling is started`() {
        val isFlung = listener.onFling(0, maxFling + 1)

        assert(isFlung)
    }

    @Test
    fun `When velocity is less than -4000 while scrolling up then fling is started`() {
        val isFlung = listener.onFling(0, -(maxFling + 1))

        assert(isFlung)
    }

    @Test
    fun `When velocity is less than 4001 while scrolling down then fling is not started`() {
        val isFlung = listener.onFling(0, maxFling)

        assert(!isFlung)
    }

    @Test
    fun `When velocity is more than -4001 while scrolling up then fling is not started`() {
        val isFlung = listener.onFling(0, -maxFling)

        assert(!isFlung)
    }
}