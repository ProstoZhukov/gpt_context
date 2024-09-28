/**
 * Инструменты для преобразования между системами координат родительского View и его потомка
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.swipeablelayout.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Помещает в [rect] координаты области [View] в системе координат родителя
 */
internal fun View.obtainVisibleRectInParentCoords(rect: Rect, parent: ViewGroup) {
    getLocalVisibleRect(rect)
    parent.offsetDescendantRectToMyCoords(this, rect)
}

/**
 * Преобразует координаты [MotionEvent] для родительского [View] в систему координат потомка
 */
internal fun View.mapParentMotionEventCoordsToMyCoords(
    event: MotionEvent, parent: ViewGroup, rect: Rect
): MotionEvent {
    getLocalVisibleRect(rect)
    val localLeft = rect.left
    val localTop = rect.top
    obtainVisibleRectInParentCoords(rect, parent)
    val dx = rect.left - localLeft
    val dy = rect.top - localTop
    return with(event) {
        MotionEvent.obtain(downTime, eventTime, action, x - dx, y - dy, metaState)
    }
}
