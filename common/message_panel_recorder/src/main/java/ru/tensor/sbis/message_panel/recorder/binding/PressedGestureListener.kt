package ru.tensor.sbis.message_panel.recorder.binding

import android.view.MotionEvent
import android.view.View

/**
 * Обработчик жестов пользователя при long press
 *
 * @author vv.chekurda
 * Создан 8/2/2019
 */
internal interface PressedGestureListener {

    /**
     * Управление активностью обработчика
     */
    var activated: Boolean

    /**
     * Обработка жестов пользователя аналогичная [View.OnTouchListener.onTouch]
     */
    fun onTouchEvent(event: MotionEvent): Boolean
}