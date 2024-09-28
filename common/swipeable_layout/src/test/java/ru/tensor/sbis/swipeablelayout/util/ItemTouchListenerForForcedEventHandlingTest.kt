package ru.tensor.sbis.swipeablelayout.util

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ItemTouchListenerForForcedEventHandlingTest {

    @Mock
    private lateinit var parent: RecyclerView
    @Mock
    private lateinit var view: View
    @Mock
    private lateinit var onInterceptMissedDownEvent: (downEvent: MotionEvent, followingEvent: MotionEvent) -> Boolean
    @Mock
    private lateinit var onTouchEvent: (event: MotionEvent) -> Unit

    private lateinit var listener: ItemTouchListenerForForcedEventHandling

    @Before
    fun setUp() {
        listener = ItemTouchListenerForForcedEventHandling(view, onInterceptMissedDownEvent, onTouchEvent)
    }

    @Test
    fun `When view is not attached to window, then touch event should be ignored`() {
        val event: MotionEvent = mock()

        listener.onTouchEvent(parent, event)

        verifyNoMoreInteractions(event)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=83862702-2b60-4bb5-8b91-78c5a8c65a1d
     */
    @Test
    fun `When view is not attached to parent, then touch event should be ignored`() {
        val event: MotionEvent = mock()
        whenever(view.isAttachedToWindow).thenReturn(true)

        listener.onTouchEvent(parent, event)

        verifyNoMoreInteractions(event)
    }
}