package ru.tensor.sbis.widget_player.widget.list.root.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.widget.list.root.SquareListViewConfig

/**
 * @author am.boldinov
 */
internal class SquareMarkerDrawer(
    private val style: Paint.Style
) : ListMarkerDrawer<SquareListViewConfig> {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = this@SquareMarkerDrawer.style
    }

    private var size = 0

    override fun onAttachedToWindow(config: SquareListViewConfig, context: Context) {
        super.onAttachedToWindow(config, context)
        paint.color = config.color.getValue(context)
        size = config.size.getValuePx(context)
    }

    override fun measure(
        index: Int,
        desiredWidthMeasureSpec: Int,
        desiredHeightMeasureSpec: Int,
        measured: MeasureSize
    ) {
        measured.set(size, size)
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
        val width = right - left
        val l: Float
        val r: Float
        if (width > size) {
            l = left + (width - size) / 2f
            r = l + size
        } else {
            l = left.toFloat()
            r = right.toFloat()
        }
        val height = bottom - top
        val t: Float
        val b: Float
        if (height > size) {
            t = top + (height - size) / 2f
            b = t + size
        } else {
            t = top.toFloat()
            b = bottom.toFloat()
        }
        canvas.drawRect(l, t, r, b, paint)
    }
}