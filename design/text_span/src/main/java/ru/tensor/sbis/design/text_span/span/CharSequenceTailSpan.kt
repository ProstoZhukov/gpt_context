package ru.tensor.sbis.design.text_span.span

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.roundToInt

/**
 * Расширение [CharSequenceSpan], которое вставляет последовательность символов **после** указанной
 * позиции. Пожалуй, единственное применение этой реализации - _вставка после текста_
 *
 * ```
 * // В текстовом поле строка "123"
 * spannable.setSpan(CharSequenceSpan("+++"), 1, 2, Spanned.SPAN_POINT_MARK)
 * // Теперь в текстовом поле "123+++"
 * ```
 *
 * @author ma.kolpakov
 * Создан 3/29/2019
 */
class CharSequenceTailSpan(charters: CharSequence) : CharSequenceSpan(charters) {

    private var shift = 0

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        shift = paint.measureText(text, start, end).roundToInt()
        canvas.drawText(charters, 0, charters.length, x + shift, y.toFloat(), paint)
    }
}