package ru.tensor.sbis.design.decorators.money

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import androidx.core.text.getSpans
import ru.tensor.sbis.design.decorators.number.NumberDecoratorStrikethroughSpan
import ru.tensor.sbis.design.theme.HorizontalPosition
import kotlin.math.roundToInt

/**
 * Span для отображения иконки валюты в декораторах.
 *
 * @author ps.smirnyh
 */
internal class MoneyDecoratorSpan(
    val charters: CharSequence,
    val currencyColor: Int = 0,
    val currencySize: Int = 0,
    private val currencyPosition: HorizontalPosition = HorizontalPosition.LEFT
) : ReplacementSpan() {

    /** Атрибут, где сохраняется занимаемая [charters] "ширина" после вызова [getSize]. */
    private var space = 0

    /** Ширина текста, к которому применяется span. */
    private var shift = 0

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        val metrics = paint.fontMetricsInt
        val oldSize = paint.textSize
        if (currencySize != 0) paint.textSize = currencySize.toFloat()
        space = paint.measureText(charters, 0, charters.length).roundToInt()
        paint.textSize = oldSize
        fm?.copyFrom(metrics)
        return paint.measureText(text, start, end).roundToInt().also { shift = it } + space
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
        var hasStrikethroughSpan = false
        var widthLine = 0f
        val isLeftIcon = currencyPosition == HorizontalPosition.LEFT
        canvas.drawText(
            text,
            start,
            end,
            if (isLeftIcon) x + space else x,
            y.toFloat(),
            paint.apply {
                text as Spanned
                text.getSpans<ForegroundColorSpan>(start, end)
                    .lastOrNull()
                    ?.let { color = it.foregroundColor }
                text.getSpans<NumberDecoratorStrikethroughSpan>(start, end)
                    .firstOrNull()
                    ?.let {
                        hasStrikethroughSpan = true
                        widthLine = it.width
                    }
            }
        )
        if (hasStrikethroughSpan) {
            paint.strokeWidth = widthLine
            val newStartX = if (isLeftIcon) x + space else x
            val newEndX = newStartX + shift
            val newY = bottom / 2f
            canvas.drawLine(
                newStartX,
                newY,
                newEndX,
                newY,
                paint
            )
            paint.strokeWidth = 0f
        }
        canvas.drawText(
            charters,
            0,
            charters.length,
            if (currencyPosition == HorizontalPosition.LEFT) x else x + shift,
            y.toFloat(),
            paint.apply {
                if (currencyColor != 0) {
                    color = currencyColor
                }
                if (currencySize != 0) {
                    textSize = currencySize.toFloat()
                }
            }
        )
    }

    private fun FontMetricsInt.copyFrom(metrics: FontMetricsInt) {
        top = metrics.top
        ascent = metrics.ascent
        descent = metrics.descent
        bottom = metrics.bottom
    }
}