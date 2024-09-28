package ru.tensor.sbis.richtext.view.gesture

import android.text.Layout
import android.text.Spanned
import android.view.View

/**
 * Делегат для отслеживания пользовательских действий по тексту.
 *
 * @author am.boldinov
 */
interface SpanMovementDelegate {

    /**
     * Выполняет поиск обработчика нажатия по координатам [x] и [y] внутри [text] на [layout].
     * Обработчиком может быть как сам span, так и обертка над любым span в виде [View.OnClickListener].
     *
     * В случае если обработчик был найден, то все остальные пересекающиеся по иерархии обработчики,
     * в том числе [android.text.style.ClickableSpan], будут проигнорированы и вызов будет направлен найденному.
     */
    fun findClickListener(
        layout: Layout,
        text: Spanned,
        spanFinder: GestureSpanFinder,
        x: Int,
        y: Int
    ): View.OnClickListener? = null

    /**
     * Выполняет поиск обработчика долгого нажатия по координатам [x] и [y] внутри [text] на [layout].
     * Обработчиком может быть как сам span, так и обертка над любым span в виде [View.OnLongClickListener].
     *
     * В случае если обработчик был найден и результат обработки вернул true,то все остальные пересекающиеся
     * по иерархии обработчики, в том числе [ru.tensor.sbis.richtext.span.LongClickSpan],
     * будут проигнорированы и вызов будет направлен найденному.
     */
    fun findLongClickListener(
        layout: Layout,
        text: Spanned,
        spanFinder: GestureSpanFinder,
        x: Int,
        y: Int
    ): View.OnLongClickListener? = null

    companion object {

        /**
         * Создает делегат для отслеживания пользовательских действий по тексту.
         *
         * @param strategy стратегия поиска спанов в тексте.
         * @param onClick опциональная обработка события нажатия пользователя.
         * @param onLongClick опциональная обработка события долгого нажатия пользователя.
         */
        inline fun <reified SPAN> create(
            strategy: GestureSpanFinder.Strategy = GestureSpanFinder.Strategy.STRONG_CHARACTER,
            noinline onClick: ((host: View, span: SPAN) -> Unit)? = null,
            noinline onLongClick: ((host: View, span: SPAN) -> Boolean)? = null
        ): SpanMovementDelegate {
            return object : SpanMovementDelegate {
                override fun findClickListener(
                    layout: Layout,
                    text: Spanned,
                    spanFinder: GestureSpanFinder,
                    x: Int,
                    y: Int
                ) = onClick?.let { listener ->
                    spanFinder.find(layout, text, x, y, SPAN::class.java, strategy).firstOrNull()?.let { span ->
                        View.OnClickListener {
                            listener.invoke(it, span)
                        }
                    }
                }

                override fun findLongClickListener(
                    layout: Layout,
                    text: Spanned,
                    spanFinder: GestureSpanFinder,
                    x: Int,
                    y: Int
                ) = onLongClick?.let { listener ->
                    spanFinder.find(layout, text, x, y, SPAN::class.java, strategy).firstOrNull()?.let { span ->
                        View.OnLongClickListener {
                            listener.invoke(it, span)
                        }
                    }
                }
            }
        }
    }
}