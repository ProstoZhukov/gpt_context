package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.os.Handler
import androidx.core.os.HandlerCompat
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.on
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.design_notification.popup.state_machine.util.ShowViewAction

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class AppearingStateTest {

    private val showViewAction = ShowViewAction { }

    private val mockHideRunnable = mock<Runnable>()

    private val mockStateMachine = mock<PopupNotificationStateMachine>()

    private val mockHandler = mock<Handler>()

    private val mockHandlerCompat = mockStatic<HandlerCompat>()

    private val appearingState =
        AppearingState(mockStateMachine, mockHideRunnable, showViewAction, mockHandler, mock())

    @Before
    fun setUp() {
        mockHandlerCompat.on<HandlerCompat, Boolean> {
            HandlerCompat.hasCallbacks(mockHandler, mockHideRunnable)
        } doReturn true
    }

    @After
    fun tearDown() {
        mockHandlerCompat.close()
    }

    @Test
    fun `When ActionPush is consumed, then it produces AppearingState, goes to DisplayedState and pushes new panel when show animation is finished`() {
        val mockNotificationFactory = mock<SbisNotificationFactory>()
        val mockDuration = mockk<DisplayDuration>()

        val result = appearingState.consume(
            ActionPush(
                mock(),
                mockNotificationFactory,
                mockDuration
            )
        )
        showViewAction.onShown()

        assertTrue(result is AppearingState)
        verify(mockStateMachine).show(mockHideRunnable)
        verify(mockStateMachine).push(mockNotificationFactory, mockDuration)
    }

    @Test
    fun `When ActionHide is consumed, then it produces AppearingState, goes to DisplayedState and hides panel when show animation is finished`() {
        val result = appearingState.consume(ActionHide)
        showViewAction.onShown()

        assertTrue(result is AppearingState)
        verify(mockStateMachine).show(mockHideRunnable)
        verify(mockStateMachine).hide()
    }

}