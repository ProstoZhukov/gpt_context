package ru.tensor.sbis.calendar.date.view.year

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.calendar.date.view.day.DayView
import ru.tensor.sbis.design.utils.getDimen
import org.joda.time.LocalDate
import ru.tensor.sbis.design.R as RDesign

/**
 * Для отрисовки маркера в календарике
 */
internal class MarkerDecoration(context: Context): RecyclerView.ItemDecoration() {

    var date: LocalDate? = null
    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }
    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }

    private val bounds = Rect()
    private val depthStroke = context.getDimen(RDesign.attr.borderThickness_m)

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        if (date == null) return

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            if (child is DayView && child.dateFullText == date) {
                parent.getDecoratedBoundsWithMargins(child, bounds)
                val margin = child.margin
                canvas.drawRect(
                    bounds.right - margin - depthStroke,
                    bounds.top + margin,
                    bounds.right - margin,
                    bounds.bottom - margin,
                    paint
                )
                return
            }
        }
    }
}