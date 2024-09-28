package ru.tensor.sbis.widget_player.widget.text.touch

import android.text.Layout
import android.text.Spanned
import android.view.MotionEvent
import android.view.View
import ru.tensor.sbis.richtext.util.getOffsetForHorizontalValidated

/**
 * @author am.boldinov
 */
internal interface TextTouchHandler {

    fun handleTouch(view: View, layout: Layout, event: MotionEvent): Boolean

    companion object {

        inline fun <reified T> findSpanByEvent(view: View, layout: Layout, event: MotionEvent): T? {
            return findSpansByEvent<T>(view, layout, event).firstOrNull()
        }

        inline fun <reified T> findSpansByEvent(view: View, layout: Layout, event: MotionEvent): Array<T> {
            return (layout.text as? Spanned)?.let { text ->
                val x = event.x - view.paddingStart + view.scrollX
                val y = event.y.toInt() - view.paddingTop + view.scrollY
                val line = layout.getLineForVertical(y)
                layout.getOffsetForHorizontalValidated(line, x).takeIf {
                    it != -1
                }?.let { offset ->
                    text.getSpans(offset, offset, T::class.java)
                }
            } ?: emptyArray()
        }
    }
}