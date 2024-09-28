package ru.tensor.sbis.message_panel.recorder.binding

import android.view.GestureDetector
import android.view.MotionEvent
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel

/**
 * Обработчик жестов пользователя, которые важны для управления записью
 *
 * @author vv.chekurda
 * Создан 8/2/2019
 */
internal class RecorderGestureListener(
    private val vm: RecorderViewModel,
    private val listener: PressedGestureListener
) : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        vm.onIconClick()
        return true
    }

    override fun onDown(p0: MotionEvent): Boolean = true

    override fun onLongPress(p0: MotionEvent) {
        listener.activated = vm.onIconLongClick()
    }
}