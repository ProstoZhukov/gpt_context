package ru.tensor.sbis.business.common.ui.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * Span для настройки отображения иконки шрифта sbis_mobile_icons в тексте
 *
 * @property padding левый отступ иконки от основного текста
 * @property iconSize размер иконки
 * @property iconColor цвет иконки
 */
class IconSpan(private val padding: Int,
               private val iconSize: Int,
               private val iconColor: Int) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return padding + paint.measureText(text, start, end).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        paint.apply {
            textSize = iconSize.toFloat()
            color = iconColor
        }
        val drawnPositionX = padding + x
        val drawnPositionY = (canvas.height / 2) - (paint.ascent() - paint.descent()) / 2
        canvas.drawText(text ?: "", start, end, drawnPositionX, drawnPositionY, paint)
    }
}