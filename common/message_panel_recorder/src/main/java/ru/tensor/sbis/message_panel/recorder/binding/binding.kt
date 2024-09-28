/**
 * Инструменты для связывания бизнес логики с представлением
 *
 * @author vv.chekurda
 * Создан 7/27/2019
 */
package ru.tensor.sbis.message_panel.recorder.binding

import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import ru.tensor.sbis.message_panel.recorder.RecorderViewImpl
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.RecordViewModelListener
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.RecordViewModelListenerImpl
import ru.tensor.sbis.message_panel_recorder.R
import ru.tensor.sbis.recorder.decl.RecordViewHintListener
import ru.tensor.sbis.recorder.decl.RecorderViewListener

internal fun RecorderViewImpl.createListener(
    hintListener: RecordViewHintListener,
    stateListener: RecorderViewListener?
): RecordViewModelListener = RecordViewModelListenerImpl(
    findViewById(R.id.recordGroup),
    findViewById(R.id.recordButton),
    findViewById(R.id.recordCircle),
    findViewById(R.id.timeLabel),
    stateListener,
    hintListener
)

internal fun RecorderViewImpl.bindToVm(vm: RecorderViewModel) {
    val radius = resources.getDimensionPixelSize(R.dimen.recorder_view_circle_size) / 2F

    val listener = PressedGestureListenerImpl(vm, radius)
    val detector = GestureDetectorCompat(context, RecorderGestureListener(vm, listener))

    findViewById<TextView>(R.id.recordButton).setOnTouchListener { _, event ->
        // важно, чтобы отрабатывали оба обработчика. Они дополняют друг друга, а не переопределяют
        detector.onTouchEvent(event) or listener.onTouchEvent(event)
    }
}
