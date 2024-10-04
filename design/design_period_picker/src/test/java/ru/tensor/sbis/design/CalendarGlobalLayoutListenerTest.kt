package ru.tensor.sbis.design

import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.design.period_picker.view.listener.CalendarGlobalLayoutListener
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel

/**
 * Тестирование слушателя [CalendarGlobalLayoutListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CalendarGlobalLayoutListenerTest {

    private val height = 2400
    private val coordinate = 100

    private val movablePanel: MovablePanel = mock {
        on { getPanelHeight() }.thenReturn(height)
        on { getPanelY() }.thenReturn(0)
    }
    private val container: FrameLayout = mock()
    private val recyclerView: RecyclerView = mock()

    @Test
    @DisplayName(
        "When parent view is a movable panel and the global layout state changes " +
            "then callback method is invoked"
    )
    fun invokePanelGlobalLayoutMethod() {
        val listener: CalendarGlobalLayoutListener = spy(
            CalendarGlobalLayoutListener(movablePanel, recyclerView, false, {}, coordinate = coordinate)
        )

        listener.onGlobalLayout()

        verify(listener, times(1)).onGlobalLayout()
    }

    @Test
    @DisplayName(
        "When parent view is a container and the global layout state changes " +
            "then callback method is invoked"
    )
    fun invokeContainerGlobalLayoutMethod() {
        val linearLayoutManager: LinearLayoutManager = mock {
            on { findFirstVisibleItemPosition() }.thenReturn(1)
        }

        whenever(recyclerView.layoutManager).thenReturn(linearLayoutManager)
        whenever(recyclerView.height).thenReturn(height)
        val listener: CalendarGlobalLayoutListener = spy(
            CalendarGlobalLayoutListener(container, recyclerView, false, {}, coordinate = coordinate)
        )

        listener.onGlobalLayout()

        verify(listener, times(1)).onGlobalLayout()
    }
}