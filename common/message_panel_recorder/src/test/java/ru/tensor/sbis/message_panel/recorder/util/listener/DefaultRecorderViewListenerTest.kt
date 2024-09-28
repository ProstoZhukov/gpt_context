package ru.tensor.sbis.message_panel.recorder.util.listener

import android.content.pm.ActivityInfo
import androidx.fragment.app.FragmentActivity
import org.mockito.kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.swipeback.SwipeBackLayout

/**
 * @author vv.chekurda
 * Создан 8/9/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DefaultRecorderViewListenerTest {

    @Mock
    private lateinit var activity: FragmentActivity

    @Mock
    private lateinit var swipeBackLayout: SwipeBackLayout

    @InjectMocks
    private lateinit var listener: DefaultRecorderViewListener

    @Test
    fun `Disable swipe back and lock screen on record start`() {
        listener.onRecordStarted()

        verify(swipeBackLayout).setDragEdge(SwipeBackLayout.DragEdge.NONE)
        verify(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    @Test
    fun `Enable swipe back and unlock screen on record completed`() {
        listener.onRecordCompleted()

        verify(swipeBackLayout).setDragEdge(SwipeBackLayout.DragEdge.LEFT)
        verify(activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}