package ru.tensor.sbis.widget_player.widget.list.root.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.widget.list.root.CircleListViewConfig

/**
 * @author am.boldinov
 */
internal class CircleMarkerDrawer(
    private val style: Paint.Style
) : ListMarkerDrawer<CircleListViewConfig> {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = this@CircleMarkerDrawer.style
    }

    private var size = 0

    override fun onAttachedToWindow(config: CircleListViewConfig, context: Context) {
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
        val x = (right - left) / 2f
        val y = (top + bottom) / 2f
        val radius = size / 2f
        canvas.drawCircle(x, y, radius, paint)
    }
}