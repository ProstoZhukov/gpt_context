package ru.tensor.sbis.message_panel.recorder.binding

import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Обработчик жестов [MotionEvent.ACTION_UP] и [MotionEvent.ACTION_MOVE] пользователя. Последний не обрабатывается
 * в [GestureDetectorCompat] при переходе в состояние long press.
 *
 * @author vv.chekurda
 * Создан 8/2/2019
 */
internal class PressedGestureListenerImpl(
    private val vm: RecorderViewModel,
    private val radius: Float
) : PressedGestureListener {

    override var activated = false

    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (activated)
            when (event.action) {
                MotionEvent.ACTION_UP   -> {
                    activated = false
                    vm.onIconReleased()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val distance = sqrt(event.x.pow(2) + event.y.pow(2))
                    vm.onOutOfIcon(distance > radius)
                    true
                }
                else                    -> false
            }
        else
            false
}