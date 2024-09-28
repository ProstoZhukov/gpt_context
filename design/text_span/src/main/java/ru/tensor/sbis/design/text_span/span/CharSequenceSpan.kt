package ru.tensor.sbis.design.text_span.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

/**
 * Реализация [ReplacementSpan] для вставки последовательности символовлов в текст. Например, для
 * добавления символов маски в текст. Вставка происходит между символами в указанной позиции.
 * Реализация не подходит для вставок _после текста_, для этой цели можно использовать [CharSequenceTailSpan]
 *
 * @param nextSymbolColor если применяется вместе с [ForegroundColorSpan], нужно передать цвет.
 * Иначе символ после span будет другого цвета.
 *
 * @author Dmitry.Subbotenko
 * Создан 1/20/2018
 */
open class CharSequenceSpan(
    protected val charters: CharSequence,
    private val nextSymbolColor: Int = 0
) : ReplacementSpan() {

    /**
     * Атрибут, где сохраняется занимаемая [charters] "ширина" после вызова [getSize]
     */
    protected var space = 0

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        space = paint.measureText(charters, 0, charters.length).roundToInt()
        return paint.measureText(text, start, end).roundToInt() + space
    }

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
        canvas.drawText(
            text, start, end, x + space, y.toFloat(),
            paint.apply {
                if (nextSymbolColor != 0) {
                    color = nextSymbolColor
                }
            }
        )
        canvas.drawText(charters, 0, charters.length, x, y.toFloat(), paint)
    }
}