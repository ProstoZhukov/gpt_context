package ru.tensor.sbis.message_panel.recorder.binding

import android.view.MotionEvent
import org.mockito.kotlin.*
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel

/**
 * @author vv.chekurda
 * Создан 8/2/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PressedGestureListenerTest {

    private val radius = 10F

    @Mock
    private lateinit var vm: RecorderViewModel

    private lateinit var listener: PressedGestureListener

    @Before
    fun setUp() {
        listener = PressedGestureListenerImpl(vm, radius)
    }

    @Test
    fun `Default state inactive`() {
        assertThat(listener.activated, equalTo(false))
    }

    @Test
    fun `Ignore event if inactive`() {
        val event = createEvent()

        assertThat(listener.onTouchEvent(event), equalTo(false))
    }

    @Test
    fun `Release record icon on ACTION_UP`() {
        val event = createEvent(MotionEvent.ACTION_UP)
        listener.activated = true

        assertThat(listener.onTouchEvent(event), equalTo(true))
        verify(vm, only()).onIconReleased()
    }

    @Test
    fun `Finger out of icon if event distance more than radius`() {
        val event = createEvent(MotionEvent.ACTION_MOVE, Float.MAX_VALUE, Float.MAX_VALUE)
        listener.activated = true

        assertThat(listener.onTouchEvent(event), equalTo(true))
        verify(vm, only()).onOutOfIcon(true)
    }

    @Test
    fun `Finger on the icon if event distance is equal to radius`() {
        val event = createEvent(MotionEvent.ACTION_MOVE, radius)
        listener.activated = true

        assertThat(listener.onTouchEvent(event), equalTo(true))
        verify(vm, only()).onOutOfIcon(false)
    }

    @Test
    fun `Finger on the icon if event distance less than radius`() {
        val event = createEvent(MotionEvent.ACTION_MOVE, radius / 2F)
        listener.activated = true

        assertThat(listener.onTouchEvent(event), equalTo(true))
        verify(vm, only()).onOutOfIcon(false)
    }

    private fun createEvent(action: Int = MotionEvent.ACTION_DOWN, x: Float = 0F, y: Float = 0F): MotionEvent =
        spy<MotionEvent>().apply {
            doReturn(action).whenever(this).action
            doReturn(x).whenever(this).x
            doReturn(y).whenever(this).y
        }
}