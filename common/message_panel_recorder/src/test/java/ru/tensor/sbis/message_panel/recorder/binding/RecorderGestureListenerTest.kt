package ru.tensor.sbis.message_panel.recorder.binding

import org.junit.Test
import org.mockito.kotlin.*
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel

/**
 * @author vv.chekurda
 * Создан 8/2/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecorderGestureListenerTest {

    @Mock
    private lateinit var vm: RecorderViewModel

    @Mock
    private lateinit var pressedListener: PressedGestureListener

    @InjectMocks
    private lateinit var listener: RecorderGestureListener

    @Test
    fun `Handle onDown event`() {
        assertThat(listener.onDown(spy()), equalTo(true))
        verifyNoMoreInteractions(vm, pressedListener)
    }

    @Test
    fun `Handle single tap`() {
        assertThat(listener.onSingleTapUp(spy()), equalTo(true))
        verify(vm, only()).onIconClick()
        verifyNoMoreInteractions(pressedListener)
    }

    @Test
    fun `Handle long press`() {
        // выжен только вызов, не результат
        val startResult = true
        whenever(vm.onIconLongClick()).thenReturn(startResult)

        listener.onLongPress(spy())

        verify(vm, only()).onIconLongClick()
        verify(pressedListener, only()).activated = startResult
    }
}