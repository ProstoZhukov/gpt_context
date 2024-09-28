package ru.tensor.sbis.richtext.view.gesture

import android.text.Layout
import android.text.Spanned
import ru.tensor.sbis.richtext.util.getOffsetForHorizontalValidated

/**
 * Дефолтная реализация компонента для поиска спанов в тексте на основе жестов пользователя.
 *
 * @author am.boldinov
 */
internal object DefaultGestureSpanFinder : GestureSpanFinder {

    override fun <T> find(
        layout: Layout,
        text: Spanned,
        x: Int,
        y: Int,
        type: Class<T>,
        strategy: GestureSpanFinder.Strategy
    ): Array<T> {
        val line = layout.getLineForVertical(y)
        val off = when (strategy) {
            GestureSpanFinder.Strategy.SOFT_LINE -> layout.getOffsetForHorizontal(line, x.toFloat())
            GestureSpanFinder.Strategy.STRONG_CHARACTER -> layout.getOffsetForHorizontalValidated(line, x.toFloat())
        }
        return if (off != -1) {
            text.getSpans(off, off, type)
        } else {
            @Suppress("UNCHECKED_CAST")
            java.lang.reflect.Array.newInstance(type, 0) as Array<T>
        }
    }
}