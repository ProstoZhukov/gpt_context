package ru.tensor.sbis.message_panel.recorder.viewmodel.listener

import android.view.View
import android.widget.TextView
import ru.tensor.sbis.recorder.decl.RecorderViewListener
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderIconState
import ru.tensor.sbis.recorder.decl.RecordViewHintListener

/**
 * @author vv.chekurda
 * @since 12/20/2019
 */
internal class RecordViewModelListenerImpl(
    private val recordGroup: View,
    private val recordButton: View,
    private val recordCircle: View,
    private val timeLabel: TextView,
    private val recordListener: RecorderViewListener?,
    hintListener: RecordViewHintListener
) : RecordViewModelListener, RecordViewHintListener by hintListener {

    override fun onStateChanged(state: RecorderIconState) {
        when (state) {
            RecorderIconState.DEFAULT -> {
                recordGroup.visibility = View.GONE
                recordButton.isActivated = false
                recordCircle.isActivated = false
                recordListener?.onRecordCompleted()
            }
            RecorderIconState.RECORD -> {
                recordGroup.visibility = View.VISIBLE
                recordButton.isActivated = true
                recordCircle.isActivated = true
                recordListener?.onRecordStarted()
            }
            RecorderIconState.CANCEL -> {
                recordCircle.isActivated = false
            }
        }
    }

    override fun onTimeChanged(time: String) {
        timeLabel.text = time
    }
}