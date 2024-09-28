package ru.tensor.sbis.widget_player.util

import android.graphics.Rect

/**
 * @author am.boldinov
 */
internal inline fun Rect.update(callback: (rect: Rect) -> Unit): Boolean {
    val curLeft = left
    val curTop = top
    val curRight = right
    val curBottom = bottom
    callback.invoke(this)
    return curLeft != left || curRight != right || curTop != top || curBottom != bottom
}