package ru.tensor.sbis.design.decorators.number

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import androidx.core.text.getSpans
import kotlin.math.roundToInt

/**
 * Span для зачеркнутого текста декоратора.
 * Не используется стандартный потому что используется разный размер текста целой и дробной частей.
 * Стандартный span будет рисовать линию на разных уровнях.
 *
 * @property width ширина линии.
 *
 * @author ps.smirnyh
 */
class NumberDecoratorStrikethroughSpan(val width: Float) : ReplacementSpan() {

    private var widthText = 0f
    private var widthFirstCharacters = 0f

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        fm?.top = paint.fontMetricsInt.top
        fm?.ascent = paint.fontMetricsInt.ascent
        fm?.descent = paint.fontMetricsInt.descent
        fm?.bottom = paint.fontMetricsInt.bottom
        widthFirstCharacters = paint.measureText(text, 0, start)
        widthText = paint.measureText(text, start, end)
        return widthText.roundToInt()
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
        text as Spanned
        text.getSpans<ForegroundColorSpan>(start, end)
            .firstOrNull()
            ?.let { paint.color = it.foregroundColor }
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        val newY = bottom / 2f
        paint.strokeWidth = width
        canvas.drawLine(x - widthFirstCharacters, newY, x + widthText, newY, paint)
    }
}