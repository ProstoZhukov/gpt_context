package ru.tensor.sbis.widget_player.widget.text.touch

import android.text.Layout
import android.view.MotionEvent
import android.view.View
import ru.tensor.sbis.richtext.span.LongClickSpan

/**
 * @author am.boldinov
 */
internal object LongClickSpanTouchHandler : TextTouchHandler {

    override fun handleTouch(view: View, layout: Layout, event: MotionEvent): Boolean {
        return TextTouchHandler.findSpansByEvent<LongClickSpan>(view, layout, event)
            .takeIf { it.isNotEmpty() }?.let { spans ->
                spans.forEach {
                    it.onLongClick(view)
                }
                true
            } ?: false
    }
}