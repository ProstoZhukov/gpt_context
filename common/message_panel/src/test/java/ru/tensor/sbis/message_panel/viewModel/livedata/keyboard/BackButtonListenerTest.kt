package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import android.view.KeyEvent
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random

/**
 * @author vv.chekurda
 * @since 1/14/2020
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class BackButtonListenerTest {

    @Mock
    private lateinit var mediator: KeyboardEventMediator

    @InjectMocks
    private lateinit var listener: BackButtonListener

    @Test
    fun `When KEYCODE_BACK received, then it should be handled and ClosedByRequest should be published`() {
        assertTrue(listener.onKeyPreImeEvent(KeyEvent.KEYCODE_BACK, mock()))
        verify(mediator, only()).postKeyboardEvent(ClosedByRequest)
    }

    @Test
    fun `When key code is not equal to KEYCODE_BACK, then event should not be handled`() {
        // выбор любого, кроме KEYCODE_BACK
        var keyEvent = Random.nextInt(0, 300)
        keyEvent = if (keyEvent == KeyEvent.KEYCODE_BACK) keyEvent + 1 else keyEvent

        assertFalse(listener.onKeyPreImeEvent(keyEvent, mock()))
        verifyNoMoreInteractions(mediator)
    }
}