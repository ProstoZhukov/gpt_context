package ru.tensor.sbis.widget_player.widget.text.touch

import android.content.ActivityNotFoundException
import android.text.Layout
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View

/**
 * @author am.boldinov
 */
internal object ClickableSpanTouchHandler : TextTouchHandler {

    override fun handleTouch(view: View, layout: Layout, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            TextTouchHandler.findSpanByEvent<ClickableSpan>(view, layout, event)?.let {
                if (action == MotionEvent.ACTION_UP) {
                    try {
                        it.onClick(view)
                    } catch (e: ActivityNotFoundException) {
                        // ignore
                    }
                }
                return true // возвращаем true всегда если нашли span для последующего отслеживания ACTION_UP
            }
        }
        return false
    }
}