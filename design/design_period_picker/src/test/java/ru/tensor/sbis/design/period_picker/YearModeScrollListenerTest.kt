package ru.tensor.sbis.design.period_picker

import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarReloadingDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.delegate.YearModeVisibleItemsDisplayDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.listener.YearModeScrollListener

/**
 * Тестирование слушателя [YearModeScrollListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class YearModeScrollListenerTest {

    private val reloadingDelegate: CalendarReloadingDelegate = mock()
    private val displayDelegate: YearModeVisibleItemsDisplayDelegate = mock {
        on { getAppearedItemPosition(any()) }.thenReturn(1)
    }
    private val listener = spy(YearModeScrollListener(mock(), reloadingDelegate, displayDelegate))
    private val recyclerView: RecyclerView = mock()

    @Test
    fun `When method onScrolled is performed then reloadData invokes`() {
        listener.onScrolled(recyclerView, 0, 0)

        verify(reloadingDelegate).reloadData(any(), any())
    }
}