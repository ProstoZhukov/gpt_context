package ru.tensor.sbis.widget_player.widget.blockquote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout

/**
 * Виджет цитаты.
 *
 * @author am.boldinov
 */
internal class BlockQuoteView(context: Context) : VerticalBlockLayout(context) {

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = Offset.X3S.getDimen(context)
    }

    init {
        setWillNotDraw(false)
        val leftPadding = Offset.L.getDimenPx(context)
        val aroundPadding = Offset.X2S.getDimenPx(context)
        setPadding(leftPadding + linePaint.strokeWidth.toInt(), aroundPadding, aroundPadding, aroundPadding)
    }

    fun setLineColor(@ColorInt color: Int) {
        if (linePaint.color != color) {
            linePaint.color = color
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), linePaint)
    }
}