package ru.tensor.sbis.design_notification.popup.state_machine.state

import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.design_notification.popup.state_machine.util.HideViewAction

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class HidingStateTest {

    private val hideViewAction = HideViewAction { }

    private val mockStateMachine = mock<PopupNotificationStateMachine>()

    private val hidingState = HidingState(mockStateMachine, hideViewAction)

    @Test
    fun `When ActionPush is consumed, then it produces HidingState, and, after hide animation is finished, resets state and pushes new panel`() {
        val mockNotificationFactory = mock<SbisNotificationFactory>()
        val mockDuration = mockk<DisplayDuration>()

        val result = hidingState.consume(ActionPush(mock(), mockNotificationFactory, mockDuration))
        hideViewAction.onHidden()

        assertTrue(result is HidingState)
        verify(mockStateMachine).reset()
        verify(mockStateMachine).push(mockNotificationFactory, mockDuration)
    }

    @Test
    fun `When HidingState is consumed, then it produces HidingState`() {
        val result = hidingState.consume(ActionHide)

        assertTrue(result is HidingState)
    }

}