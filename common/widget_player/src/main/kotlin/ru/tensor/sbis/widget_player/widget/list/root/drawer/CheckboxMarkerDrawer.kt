package ru.tensor.sbis.widget_player.widget.list.root.drawer

import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Canvas
import ru.tensor.sbis.design.checkbox.SbisCheckboxView
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.widget_player.layout.MeasureSize
import ru.tensor.sbis.widget_player.widget.list.root.CheckboxListViewConfig

/**
 * @author am.boldinov
 */
internal class CheckboxMarkerDrawer(
    private val checked: Boolean
) : ListMarkerDrawer<CheckboxListViewConfig> {

    private var view: SbisCheckboxView? = null

    override fun onAttachedToWindow(config: CheckboxListViewConfig, context: Context) {
        super.onAttachedToWindow(config, context)
        view?.let {
            (it.context as MutableContextWrapper).baseContext = context
        } ?: run {
            view = SbisCheckboxView(MutableContextWrapper(context)).apply {
                size = SbisCheckboxSize.SMALL
                setChecked(checked)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (view?.context as? MutableContextWrapper)?.baseContext = null
    }

    override fun measure(
        index: Int,
        desiredWidthMeasureSpec: Int,
        desiredHeightMeasureSpec: Int,
        measured: MeasureSize
    ) {
        view?.let { view ->
            view.measure(desiredWidthMeasureSpec, desiredHeightMeasureSpec)
            view.layout(0, 0)
            measured.setFrom(view)
        } ?: run {
            measured.setEmpty()
        }
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
        view?.let { view ->
            canvas.save()
            val dx = left.toFloat()
            val dy = top + (bottom - top - view.height) / 2f
            canvas.translate(dx, dy)
            view.draw(canvas)
            canvas.restore()
        }
    }
}