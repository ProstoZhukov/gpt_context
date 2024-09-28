package ru.tensor.sbis.message_panel.recorder.viewmodel.listener

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderIconState
import ru.tensor.sbis.recorder.decl.RecordViewHintListener
import ru.tensor.sbis.recorder.decl.RecorderViewListener

/**
 * @author vv.chekurda
 * @since 12/20/2019
 */
@RunWith(MockitoJUnitRunner::class)
class RecordViewModelListenerImplTest {

    @Mock
    private lateinit var recordGroup: View
    @Mock
    private lateinit var recordButton: View
    @Mock
    private lateinit var recordCircle: View
    @Mock
    private lateinit var timeLabel: TextView
    @Mock
    private lateinit var recordListener: RecorderViewListener
    @Mock
    private lateinit var hintListener: RecordViewHintListener

    private lateinit var stateListener: RecordViewModelListener

    @Before
    fun setUp() {
        stateListener = RecordViewModelListenerImpl(
            recordGroup, recordButton, recordCircle, timeLabel, recordListener, hintListener
        )
    }

    @Test
    fun `When time changed, then time label should receive time string`() {
        val time = "Test time string"

        stateListener.onTimeChanged(time)

        verify(timeLabel, only()).text = time
        verifyNoMoreInteractions(recordGroup, recordButton, recordCircle, recordListener, hintListener)
    }

    @Test
    fun `When record cancelled, then record circle should be deactivated`() {
        stateListener.onStateChanged(RecorderIconState.CANCEL)

        verify(recordCircle, only()).isActivated = false
        verifyNoMoreInteractions(recordGroup, recordButton, timeLabel, recordListener, hintListener)
    }

    @Test
    fun `When record started, then record group should become visible, record button and record circle should be activated`() {
        stateListener.onStateChanged(RecorderIconState.RECORD)

        verify(recordGroup, only()).visibility = View.VISIBLE
        verify(recordButton, only()).isActivated = true
        verify(recordCircle, only()).isActivated = true
        verifyNoMoreInteractions(timeLabel, hintListener)
    }

    @Test
    fun `When record completed, then record group should be hidden, record button and record circle should be deactivated`() {
        stateListener.onStateChanged(RecorderIconState.DEFAULT)

        verify(recordGroup, only()).visibility = View.GONE
        verify(recordButton, only()).isActivated = false
        verify(recordCircle, only()).isActivated = false
        verifyNoMoreInteractions(timeLabel, hintListener)
    }

    @Test
    fun `When record started, then record listener onRecordStarted() method should be called`() {
        stateListener.onStateChanged(RecorderIconState.RECORD)

        verify(recordListener, only()).onRecordStarted()
    }

    @Test
    fun `When record completed, then record listener onRecordCompleted() method should be called`() {
        stateListener.onStateChanged(RecorderIconState.DEFAULT)

        verify(recordListener, only()).onRecordCompleted()
    }
}