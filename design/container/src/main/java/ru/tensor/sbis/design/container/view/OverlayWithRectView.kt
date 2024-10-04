package ru.tensor.sbis.design.container.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import ru.tensor.sbis.design.container.locator.watcher.Area

/**
 * Класс для отображения затемнения с заданным прямоугольным вырезом.
 * @author ma.kolpakov
 */
internal class OverlayWithRectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val path: Path = Path()
    private val subpaths: MutableList<Path> = mutableListOf()
    private val boundsPath = Path()

    internal var outerCutCornersRadius = 0f
        internal set(value) {
            field = value
            invalidate()
        }

    internal var dimColor = Color.BLACK
        internal set(value) {
            field = value
            invalidate()
        }

    var onDetachedFromWindowListener: () -> Unit = {}

    var boundsRect: Rect = Rect()
        set(value) {
            field = value
            boundsPath.apply {
                reset()
                addRect(RectF(value), Path.Direction.CW)
            }
            resetPath()
        }

    var areas: List<Area> = emptyList()
        set(value) {
            field = value
            resetPath()
        }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (!path.isEmpty) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(path)
            } else {
                @Suppress("DEPRECATION")
                canvas.clipPath(path, Region.Op.DIFFERENCE)
            }
        }
        val mainRect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        val dimPaint = Paint().apply { color = dimColor }
        canvas.drawRoundRect(mainRect, outerCutCornersRadius, outerCutCornersRadius, dimPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onDetachedFromWindowListener()
    }

    private fun resetPath() {
        while (subpaths.size > areas.size) subpaths.removeLast()
        while (subpaths.size < areas.size) subpaths.add(Path())
        areas.forEachIndexed { index, area ->
            subpaths[index].apply {
                reset()
                setArea(area)
            }
        }
        path.reset()
        subpaths.filter { !it.isEmpty }.forEach { path.addPath(it) }
        invalidate()
    }

    private fun Path.setArea(area: Area) {
        val (rect, cornerRadius) = area
        if (!rect.isEmpty) {
            val radii = FloatArray(8).apply { fill(cornerRadius) }
            if (rect.intersects(boundsRect.left, boundsRect.top, boundsRect.right, boundsRect.bottom)) {
                addRoundRect(RectF(rect), radii, Path.Direction.CW)
                op(boundsPath, Path.Op.INTERSECT)
            }
        }
    }
}
