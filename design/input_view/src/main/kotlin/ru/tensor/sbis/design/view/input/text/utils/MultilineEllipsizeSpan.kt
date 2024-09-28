package ru.tensor.sbis.design.view.input.text.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Rect
import android.text.style.ReplacementSpan

/**
 * Спан для усечения длины многострочного текста шириной канваса для отрисовки.
 *
 * @property lineTextPositionRange диапазон символов последней строки, в которую вставится ellipsize,
 * а остальные строки отрисованы не будут.
 */
internal class MultilineEllipsizeSpan(
    private val lineTextPositionRange: IntRange
) : ReplacementSpan() {

    private val bounds = Rect()

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return if (start <= lineTextPositionRange.first) {
            fm?.copyFrom(paint.fontMetricsInt)
            paint.measureText(
                text,
                lineTextPositionRange.first,
                lineTextPositionRange.last
            ).toInt()
        } else {
            fm?.copyFrom(FontMetricsInt())
            0
        }
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
        if (lineTextPositionRange.first < start) return
        canvas.getClipBounds(bounds)
        val width = bounds.right - x
        val ellipsisWidth = paint.measureText(CHAR_ELLIPSIS)
        var newEndIndex = start + paint.breakText(
            text,
            lineTextPositionRange.first,
            lineTextPositionRange.last,
            true,
            width - ellipsisWidth,
            null
        )
        while (newEndIndex > start && (text[newEndIndex - 1] == ' ')) { // Пропускаем пробелы в конце строки
            --newEndIndex
        }
        canvas.drawText(text, start, newEndIndex, x, y.toFloat(), paint)
        canvas.drawText(
            CHAR_ELLIPSIS,
            x + paint.measureText(text, start, newEndIndex),
            y.toFloat(),
            paint
        )
    }

    private fun FontMetricsInt.copyFrom(metrics: FontMetricsInt) {
        top = metrics.top
        ascent = metrics.ascent
        descent = metrics.descent
        bottom = metrics.bottom
    }
}

private const val CHAR_ELLIPSIS = "\u2026"