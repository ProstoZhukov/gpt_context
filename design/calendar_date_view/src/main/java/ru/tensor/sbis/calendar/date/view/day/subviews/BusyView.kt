package ru.tensor.sbis.calendar.date.view.day.subviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.design.utils.getThemeColor
import kotlin.math.roundToInt

/**
 * View для отображения занятого времени в календаре.
 *
 * @author ae.noskov
 */
internal class BusyView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatTextView(context, attrs, defStyle) {

    private val busyLevelWidth = 7 * context.resources.displayMetrics.density
    private val busyLevelViewContainerWidth = (8 * context.resources.displayMetrics.density).toInt()

    /**
     * Уровни занятости дня.
     */
    var busyLevel: List<Boolean>? = null
        @MainThread set(value) {
            field = value
            invalidate()
        }

    /**
     * Цвет уровней занятости дня.
     */
    var busyLevelColor: Int = ContextCompat.getColor(context, context.getThemeColor(R.attr.calendar_date_view_color_busy_level_default))
        @MainThread set(value) {
            field = value
            paintBusyLevel.color = value
            invalidate()
        }

    private inline val canvasStep get() = (((height / 6f) / context.resources.displayMetrics.density).roundToInt() ) * context.resources.displayMetrics.density

    private val paintBusyLevel = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1 * context.resources.displayMetrics.density
        color = ContextCompat.getColor(context, context.getThemeColor(R.attr.calendar_date_view_color_busy_level_default))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(measuredWidth + busyLevelViewContainerWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        busyLevel?.forEachIndexed { index, level ->
            if (level) {
                canvas.drawLine(
                        width - busyLevelWidth,
                        canvasStep * (index + 2),
                        width.toFloat(),
                        canvasStep * (index + 2),
                        paintBusyLevel
                )
            }
        }

        super.onDraw(canvas)
    }

}