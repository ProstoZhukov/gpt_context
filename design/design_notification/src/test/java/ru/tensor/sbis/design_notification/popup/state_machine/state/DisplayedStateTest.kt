package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.os.Handler
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DisplayedStateTest {

    private val mockHideRunnable = mock<Runnable>()

    private val mockStateMachine = mock<PopupNotificationStateMachine>()

    private val mockPopupWindowViewDisplayManager = mock<PopupWindowViewDisplayManager>()

    private val mockHandler = mock<Handler>()

    private val displayedState =
        DisplayedState(mockStateMachine, mockHideRunnable, mockHandler, mockPopupWindowViewDisplayManager)


    @Test
    fun `When ActionPush is consumed, then it produces HidingState, hides the views and , after hide animation is finished, resets state and pushes new panel`() {
        val mockNotificationFactory = mock<SbisNotificationFactory>()
        val mockDuration: DisplayDuration = mock<DisplayDuration.Indefinite>()

        val result = displayedState.consume(ActionPush(mock(), mockNotificationFactory, mockDuration))
        (result as? HidingState)?.hideViewAction?.onHidden?.invoke()

        assertTrue(result is HidingState)
        verify(mockHandler).removeCallbacks(mockHideRunnable)
        verify(mockPopupWindowViewDisplayManager).hideViews(any())
        verify(mockStateMachine).reset()
        verify(mockStateMachine).push(mockNotificationFactory, mockDuration)
    }

    @Test
    fun `When ActionHide is consumed, then it produces HidingState, and, after hide animation is finished, resets state`() {
        val result = displayedState.consume(ActionHide)
        (result as? HidingState)?.hideViewAction?.onHidden?.invoke()

        assertTrue(result is HidingState)
        verify(mockHandler).removeCallbacks(mockHideRunnable)
        verify(mockPopupWindowViewDisplayManager).hideViews(any())
        verify(mockStateMachine).reset()
    }

}