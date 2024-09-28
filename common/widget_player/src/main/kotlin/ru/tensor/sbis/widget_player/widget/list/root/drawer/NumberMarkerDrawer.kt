package ru.tensor.sbis.widget_player.widget.list.root.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.widget.list.root.NumberListViewConfig

/**
 * @author am.boldinov
 */
internal class NumberMarkerDrawer : ListMarkerDrawer<NumberListViewConfig> {

    private val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val bounds = Rect()

    override fun onAttachedToWindow(config: NumberListViewConfig, context: Context) {
        super.onAttachedToWindow(config, context)
        paint.apply {
            color = config.color.getValue(context)
            textSize = config.numberSize.getValue(context)
            typeface = config.fontWeight.getTypeface(context)
        }
    }

    override fun measure(
        index: Int,
        desiredWidthMeasureSpec: Int,
        desiredHeightMeasureSpec: Int,
        measured: MeasureSize
    ) {
        val text = formatIndex(index)
        paint.getTextBounds(text, 0, text.length, bounds)
        measured.setFrom(bounds)
    }

    override fun draw(
        canvas: Canvas,
        position: Int,
        index: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        baseline: Int
    ) {
        val text = formatIndex(index)
        val x = (right - left) / 2f
        val y = baseline.takeIf { it > 0 }?.toFloat() ?: (bottom.toFloat() - paint.descent())
        canvas.drawText(text, x, y, paint)
    }

    private fun formatIndex(index: Int): String {
        return "$index."
    }
}