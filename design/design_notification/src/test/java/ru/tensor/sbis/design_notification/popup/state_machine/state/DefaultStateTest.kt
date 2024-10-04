package ru.tensor.sbis.design_notification.popup.state_machine.state

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.ColorUtils
import androidx.core.os.HandlerCompat
import curtains.Curtains
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.common.testing.on
import ru.tensor.sbis.design_notification.popup.SbisNotificationFactory
import ru.tensor.sbis.design_notification.popup.state_machine.ActionHide
import ru.tensor.sbis.design_notification.popup.state_machine.ActionPush
import ru.tensor.sbis.design_notification.popup.state_machine.PopupNotificationStateMachine
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration
import ru.tensor.sbis.design_notification.popup.state_machine.util.PopupWindowViewDisplayManager
import ru.tensor.sbis.design_notification.popup.state_machine.util.StatusBarColorHelper

private const val VIEW_HEIGHT = 42
private const val SHOW_DURATION = 3000L

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DefaultStateTest {

    private val stateMachine = mock<PopupNotificationStateMachine>()

    private val mockHandler = mock<Handler>()

    private val mockStatusBarColorHelper = mock<StatusBarColorHelper>()

    private val mockWindowManager = mock<WindowManager>()

    private val defaultState = DefaultState(
        stateMachine,
        mockHandler,
        PopupWindowViewDisplayManager(mockStatusBarColorHelper)
    )

    private val mockCurtains = mockStatic<Curtains>()

    private val mockStaticValueAnimator = mockStatic<ValueAnimator>()

    private val mockValueAnimator = mock<ValueAnimator>()

    @Before
    fun setUp() {
        val mockRootView = mock<View>() {
            on { context } doReturn mock<Activity>()
        }
        mockCurtains.on<Curtains, List<View>> {
            Curtains.rootViews
        } doReturn listOf(mockRootView)
        mockStaticValueAnimator.on<ValueAnimator, ValueAnimator> {
            ValueAnimator.ofFloat(-VIEW_HEIGHT.toFloat(), 0f)
        } doReturn mockValueAnimator
    }

    @After
    fun tearDown() {
        mockCurtains.close()
        mockStaticValueAnimator.close()
    }

    @Test
    fun `When ActionPush is consumed, then it produces AppearingState and goes to DisplayedState after animation end`() {
        val mockActivity = mock<Activity> {
            on { getSystemService(Activity.WINDOW_SERVICE) } doReturn mockWindowManager
        }
        val mockView = mock<View> {
            on { context } doReturn mockActivity
            on { width } doReturn 1
            on { height } doReturn VIEW_HEIGHT
        }
        val mockNotification = mock<SbisNotificationFactory> {
            on { createView(any(), any()) } doReturn mockView
        }
        val layoutParamsCaptor = argumentCaptor<WindowManager.LayoutParams>()
        val viewCaptor = argumentCaptor<View>()
        val listenerCaptor = argumentCaptor<Animator.AnimatorListener>()
        val hideRunnableCaptor = argumentCaptor<Runnable>()

        val result = defaultState.consume(ActionPush(mockHandler, mockNotification, DisplayDuration.Default))

        assertTrue(result is AppearingState)
        verify(mockWindowManager).addView(viewCaptor.capture(), layoutParamsCaptor.capture())
        assertEquals(mockView, viewCaptor.firstValue)
        with(layoutParamsCaptor.firstValue) {
            assertEquals(WindowManager.LayoutParams.MATCH_PARENT, width)
            assertEquals(WindowManager.LayoutParams.WRAP_CONTENT, height)
            assertEquals(Gravity.TOP, gravity)
            assertEquals(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, flags)
        }
        verify(mockValueAnimator).addListener(listenerCaptor.capture())
        verify(mockView).translationY = -VIEW_HEIGHT.toFloat()
        listenerCaptor.firstValue.onAnimationEnd(mock())
        verify(stateMachine).show(hideRunnableCaptor.capture())
        verify(mockHandler).postDelayed(hideRunnableCaptor.firstValue, SHOW_DURATION)
    }

    @Test
    fun `When ActionPush is consumed, then it produces DefaultState`() {
        val result = defaultState.consume(ActionHide)

        assertTrue(result is DefaultState)
    }
}