package ru.tensor.sbis.design_notification.popup.state_machine

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import ru.tensor.sbis.design_notification.popup.state_machine.state.DefaultState
import ru.tensor.sbis.design_notification.popup.state_machine.state.DisplayedState

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PopupNotificationStateMachineTest {

    private val stateMachine = PopupNotificationStateMachine(mock())

    @Test
    fun `When state machine is reset, then state is changed to DefaultState`() {
        stateMachine.reset()

        assertTrue(stateMachine.state is DefaultState)
    }

    @Test
    fun `When show method is called, then state is changed to DisplayedState`() {
        stateMachine.show(mock())

        assertTrue(stateMachine.state is DisplayedState)
    }

}