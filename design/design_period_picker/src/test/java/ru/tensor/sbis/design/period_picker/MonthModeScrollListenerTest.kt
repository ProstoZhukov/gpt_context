package ru.tensor.sbis.design.period_picker

import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarReloadingDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.delegate.MonthModeVisibleItemsDisplayDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.listener.MonthModeScrollListener

/**
 * Тестирование слушателя [MonthModeScrollListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MonthModeScrollListenerTest {

    private val reloadingDelegate: CalendarReloadingDelegate = mock()
    private val displayDelegate: MonthModeVisibleItemsDisplayDelegate = mock {
        on { updateVisibleItems(any(), any()) }.thenReturn(1 to 2)
    }
    private val listener = spy(MonthModeScrollListener(mock(), mock(), true, reloadingDelegate, displayDelegate))
    private val recyclerView: RecyclerView = mock()

    @Test
    @DisplayName(
        "When method onScrolled is performed by user (dy is more than 0) " +
            "then reloadData invokes and updateVisibleItems does not invoke"
    )
    fun performScrollByDefault() {
        listener.onScrolled(recyclerView, 0, 0)

        verify(reloadingDelegate).reloadData(any(), any())
        verify(displayDelegate, never()).updateVisibleItems(any(), any())
    }

    @Test
    @DisplayName(
        "When method onScrolled is performed by user (dy is more than 0) " +
            "then reloadData and updateVisibleItems methods invoke"
    )
    fun performScrollByUser() {
        listener.onScrolled(recyclerView, 0, 1)

        verify(reloadingDelegate).reloadData(any(), any())
        verify(displayDelegate).updateVisibleItems(any(), any())
    }
}