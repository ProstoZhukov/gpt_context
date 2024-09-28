package ru.tensor.sbis.richtext.util

import android.content.ActivityNotFoundException
import android.text.Layout
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView
import ru.tensor.sbis.richtext.span.LongClickSpan
import ru.tensor.sbis.richtext.view.gesture.DefaultGestureSpanFinder
import ru.tensor.sbis.richtext.view.gesture.GestureSpanFinder
import ru.tensor.sbis.richtext.view.gesture.SpanMovementDelegate

/**
 * Утилита для работы с View компонентами богатого текста
 *
 * @author am.boldinov
 */
object RichWidgetUtil {

    /**
     * Осуществляет клик по [ClickableSpan]
     * Часть метода onTouchEvent из LinkMovementMethod
     */
    @JvmStatic
    @JvmOverloads
    fun processLinkMovement(
        widget: TextView,
        event: MotionEvent,
        finder: GestureSpanFinder = DefaultGestureSpanFinder,
        delegates: List<SpanMovementDelegate>? = null
    ): Boolean {
        val action = event.action
        // обрабатываем link click только если отсутствует выделение текста,
        // т.к после завершения выделения срабатывает ACTION_UP
        if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) && widget.selectionStart == widget.selectionEnd) {
            val clickListener = delegates.findDelegateListener(widget, event) { delegate, layout, text, x, y ->
                delegate.findClickListener(layout, text, finder, x, y)
            }
            val clickableSpan = if (clickListener == null) {
                findSpanByEvent(widget, event, ClickableSpan::class.java)
            } else {
                null
            }
            if (clickListener != null || clickableSpan != null) {
                if (action == MotionEvent.ACTION_UP) {
                    try {
                        clickListener?.onClick(widget)
                        clickableSpan?.onClick(widget)
                    } catch (e: ActivityNotFoundException) {
                        // ignore
                    }
                }
                return true
            }
        }
        return false
    }

    /**
     * Осуществляет клик по [LongClickSpan]
     */
    @JvmStatic
    @JvmOverloads
    fun processLongClick(
        widget: TextView,
        event: MotionEvent,
        finder: GestureSpanFinder = DefaultGestureSpanFinder,
        delegates: List<SpanMovementDelegate>? = null
    ): Boolean {
        val longClickListener = delegates.findDelegateListener(widget, event) { delegate, layout, text, x, y ->
            delegate.findLongClickListener(layout, text, finder, x, y)
        }
        if (longClickListener != null && longClickListener.onLongClick(widget)) {
            return true
        }
        val longClickSpans = findSpansByEvent(
            widget,
            event,
            LongClickSpan::class.java
        )
        longClickSpans.forEach {
            it.onLongClick(widget)
        }
        return longClickSpans.isNotEmpty()
    }

    /**
     * Осуществляет поиск спана по координатам MotionEvent
     */
    @JvmStatic
    fun <T> findSpanByEvent(widget: TextView, event: MotionEvent, type: Class<T>): T? {
        return findSpansByEvent(widget, event, type).firstOrNull()
    }

    /**
     * Осуществляет поиск спанов по координатам MotionEvent
     */
    @JvmStatic
    @JvmOverloads
    fun <T> findSpansByEvent(
        widget: TextView,
        event: MotionEvent,
        type: Class<T>,
        finder: GestureSpanFinder = DefaultGestureSpanFinder
    ): Array<T> {
        @Suppress("UNCHECKED_CAST")
        return event.mapInTextView(widget) { layout, text, x, y ->
            finder.find(
                layout,
                text,
                x,
                y,
                type,
                GestureSpanFinder.Strategy.STRONG_CHARACTER
            )
        } ?: java.lang.reflect.Array.newInstance(type, 0) as Array<T>
    }

    private inline fun <T> List<SpanMovementDelegate>?.findDelegateListener(
        widget: TextView,
        event: MotionEvent,
        callback: (delegate: SpanMovementDelegate, layout: Layout, text: Spanned, x: Int, y: Int) -> T?
    ): T? {
        return takeUnless { it.isNullOrEmpty() }?.let { delegates ->
            event.mapInTextView(widget) { layout, text, x, y ->
                delegates.forEach {
                    val result = callback.invoke(it, layout, text, x, y)
                    if (result != null) {
                        return@mapInTextView result
                    }
                }
                null
            }
        }
    }

    private inline fun <T> MotionEvent.mapInTextView(
        widget: TextView,
        callback: (layout: Layout, text: Spanned, x: Int, y: Int) -> T
    ): T? {
        return (widget.text as? Spanned)?.let { text ->
            var x = x.toInt()
            var y = y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY
            callback.invoke(widget.layout, text, x, y)
        }
    }
}
