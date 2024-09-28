package ru.tensor.sbis.widget_player.widget.list.root

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.view.children
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
import ru.tensor.sbis.widget_player.widget.list.root.drawer.CheckboxMarkerDrawer
import ru.tensor.sbis.widget_player.widget.list.root.drawer.CircleMarkerDrawer
import ru.tensor.sbis.widget_player.widget.list.root.drawer.ListMarkerDrawer
import ru.tensor.sbis.widget_player.widget.list.root.drawer.NumberMarkerDrawer
import ru.tensor.sbis.widget_player.widget.list.root.drawer.SquareMarkerDrawer

/**
 * @author am.boldinov
 */
internal class ListViewLayout(context: Context) : VerticalBlockLayout(context) {

    companion object {
        private val defaultIndexProperties = ListIndexProperties()

        private val circleFillDrawer = CircleMarkerDrawer(Paint.Style.FILL)
        private val circleStrokeDrawer = CircleMarkerDrawer(Paint.Style.STROKE)
        private val squareFillDrawer = SquareMarkerDrawer(Paint.Style.FILL)
        private val squareStrokeDrawer = SquareMarkerDrawer(Paint.Style.STROKE)
        private val checkedCheckboxDrawer = CheckboxMarkerDrawer(checked = true)
        private val uncheckedCheckboxDrawer = CheckboxMarkerDrawer(checked = false)
        private val numberDrawer = NumberMarkerDrawer()
    }

    private val checkboxDrawerProxy = object : ListMarkerDrawer<CheckboxListViewConfig> {

        override fun onAttachedToWindow(config: CheckboxListViewConfig, context: Context) {
            super.onAttachedToWindow(config, context)
            checkedCheckboxDrawer.onAttachedToWindow(config, context)
            uncheckedCheckboxDrawer.onAttachedToWindow(config, context)
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            checkedCheckboxDrawer.onDetachedFromWindow()
            uncheckedCheckboxDrawer.onDetachedFromWindow()
        }

        override fun measure(
            index: Int,
            desiredWidthMeasureSpec: Int,
            desiredHeightMeasureSpec: Int,
            measured: MeasureSize
        ) {
            var width = 0
            var height = 0
            if (indexProperties.checkedCount > 0) {
                checkedCheckboxDrawer.measure(index, desiredWidthMeasureSpec, desiredHeightMeasureSpec, measured)
                width = measured.width
                height = measured.height
            }
            if (indexProperties.uncheckedCount > 0) {
                uncheckedCheckboxDrawer.measure(index, desiredWidthMeasureSpec, desiredHeightMeasureSpec, measured)
                width = maxOf(width, measured.width)
                height = maxOf(height, measured.height)
            }
            measured.set(width, height)
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
            val checked = indexProperties.checkedIndexes.getOrNull(position) ?: false
            val drawer = if (checked) checkedCheckboxDrawer else uncheckedCheckboxDrawer
            drawer.draw(canvas, position, index, left, top, right, bottom, baseline)
        }
    }

    private val drawerPaddingEnd = Offset.X2S.getDimenPx(context)

    private val measured = MeasureSize()

    private var markerSize = 0
    private var indexProperties: ListIndexProperties = defaultIndexProperties
    private var config: ListViewConfig? = null

    private var lockLayout = false

    private var drawer: ListMarkerDrawer<ListViewConfig>? = null

    init {
        setWillNotDraw(false)
    }

    fun configure(config: ListViewConfig, properties: ListIndexProperties) {
        var changed = false
        if (indexProperties != properties) {
            indexProperties = properties
            changed = true
        }
        val newConfig = config.prepare(properties.level)
        if (this.config != newConfig) {
            this.config = newConfig
            changed = true
            markerSize = newConfig.markerSize.getValuePx(context)
            drawer?.onDetachedFromWindow()
            drawer = newConfig.obtainDrawer().also {
                it.onAttachedToWindow(newConfig, context)
            }
        }
        if (changed) {
            requestLayout()
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawer = drawer
        if (childCount > 0 && drawer != null) {
            val widthSpec = MeasureSpec.makeMeasureSpec(markerSize, MeasureSpec.UNSPECIFIED)
            val heightSpec = MeasureSpec.makeMeasureSpec(markerSize, MeasureSpec.AT_MOST)
            measured.setEmpty()
            val maxIndex = indexProperties.startIndex + childCount
            // измеряем размер только максимального индекса, чтоб выделить под него область
            drawer.measure(maxIndex, widthSpec, heightSpec, measured)
            measured.set(maxOf(measured.width, markerSize), maxOf(measured.height, markerSize))
            runWithLockLayout {
                setPadding(measured.width + drawerPaddingEnd, paddingTop, paddingEnd, paddingBottom)
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val drawer = drawer
        if (childCount > 0 && drawer != null) {
            canvas.save()
            children.forEachIndexed { index, view ->
                val offsetIndex = indexProperties.startIndex + index + 1
                val top = view.top
                val bottom = top + measured.height
                val right = view.left - drawerPaddingEnd
                val left = right - measured.width
                val baseline = top + view.baseline
                drawer.draw(canvas, index, offsetIndex, left, top, right, bottom, baseline)
            }
            canvas.restore()
        }
        super.onDraw(canvas)
    }

    override fun requestLayout() {
        if (!lockLayout) {
            super.requestLayout()
        }
    }

    override fun invalidate() {
        if (!lockLayout) {
            super.invalidate()
        }
    }

    private fun ListViewConfig.prepare(level: Int): ListViewConfig {
        return (this as? LevelListViewConfig)?.let {
            it.levels[level % it.levels.size]
        }?.prepare(level) ?: this
    }

    @Suppress("UNCHECKED_CAST")
    private fun ListViewConfig.obtainDrawer() = when (this) {
        is CircleListViewConfig -> if (style == Paint.Style.FILL) circleFillDrawer else circleStrokeDrawer
        is SquareListViewConfig -> if (style == Paint.Style.FILL) squareFillDrawer else squareStrokeDrawer
        is CheckboxListViewConfig -> checkboxDrawerProxy
        is NumberListViewConfig -> numberDrawer
        is LevelListViewConfig -> error("config not supported in layout, drawer not found. Please call ListViewConfig.prepare method.")
    } as ListMarkerDrawer<ListViewConfig>

    private inline fun runWithLockLayout(block: () -> Unit) {
        lockLayout = true
        block.invoke()
        lockLayout = false
    }
}