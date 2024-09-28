package ru.tensor.sbis.design.text_span.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * Спан для выставления указанного в пикселях отступа взамен [CharSequence], на который накладывается этот спан
 *
 * @author am.boldinov
 */
class CharIndentSpan @JvmOverloads constructor(
    private val indentSize: Int,
    private val adaptiveFontMetrics: Boolean = false
) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null && adaptiveFontMetrics) {
            paint.getFontMetricsInt(fm)
        }
        return indentSize
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // ignored
    }

}