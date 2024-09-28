package ru.tensor.sbis.calendar.date.view.year

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Region
import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.view.HatchWithOutlineDrawer
import ru.tensor.sbis.calendar.date.view.day.DayView
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider

/**
 * Для отрисовки нежелательных дней
 */
internal class UnwantedVacation(context: Context): RecyclerView.ItemDecoration() {

    private val hatchWithOutlineDrawer = HatchWithOutlineDrawer(
        context,
        context.resources.getDimension(R.dimen.design_hatch_day_unwanted_vacation_border_width),
    )

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }

    private val colorsProvider = ColorsProvider(context)
    private val path = Path()
    private val region = Region()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val childCount = parent.childCount
        val verticalOffset = parent.computeVerticalScrollOffset()
        val diagonalOffset = verticalOffset.toFloat() % (hatchWithOutlineDrawer.gap * 1.15f)
        hatchWithOutlineDrawer.topLinesOffset = -diagonalOffset
        val bounds = Rect()
        path.reset()
        region.setEmpty()
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            if (child is DayView) {
                parent.getDecoratedBoundsWithMargins(child, bounds)
                val margin = child.margin
                if (child.isUnwantedVacationDay) {
                    region.op(bounds, Region.Op.UNION)
                }
                paint.color = when {
                    child.isInvisible -> Color.TRANSPARENT
                    child.dateText.let { it.isNullOrEmpty() || it == "0" } -> Color.TRANSPARENT
                    child.isUsualDay() && child.dayType == EventType.WORKDAY -> colorsProvider.colorWorkday
                    child.isUsualDay() && child.dayType == EventType.DAY_OFF -> colorsProvider.colorDayOff
                    else -> Color.TRANSPARENT
                }
                if (paint.color != Color.TRANSPARENT) {
                    canvas.drawRect(
                        bounds.left + margin,
                        bounds.top + margin,
                        bounds.right - margin,
                        bounds.bottom - margin,
                        paint
                    )
                }
            }
        }

        region.getBoundaryPath(path)
        canvas.save()
        if (hatchWithOutlineDrawer.hatchPath.isEmpty) {
            hatchWithOutlineDrawer.fillPath(0, canvas.width, 0, canvas.height, hatchWithOutlineDrawer.hatchPath)
        }
        canvas.clipPath(path)
        hatchWithOutlineDrawer.drawOnlyHatch(canvas, hatchWithOutlineDrawer.hatchPath)
        canvas.restore()
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        canvas.save()
        canvas.clipPath(path)
        hatchWithOutlineDrawer.drawOnlyBorder(canvas, path)
        canvas.restore()
    }
}