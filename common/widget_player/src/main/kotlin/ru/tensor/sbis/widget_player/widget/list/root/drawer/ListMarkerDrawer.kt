package ru.tensor.sbis.widget_player.widget.list.root.drawer

import android.content.Context
import android.graphics.Canvas
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.widget.list.root.ListViewConfig

/**
 * @author am.boldinov
 */
internal interface ListMarkerDrawer<CONFIG : ListViewConfig> {

    fun measure(index: Int, desiredWidthMeasureSpec: Int, desiredHeightMeasureSpec: Int, measured: MeasureSize)

    fun draw(
        canvas: Canvas,
        position: Int,
        index: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        baseline: Int
    )

    fun onAttachedToWindow(config: CONFIG, context: Context) {

    }

    fun onDetachedFromWindow() {

    }
}