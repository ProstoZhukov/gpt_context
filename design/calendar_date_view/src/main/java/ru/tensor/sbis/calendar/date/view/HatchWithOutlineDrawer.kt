package ru.tensor.sbis.calendar.date.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import ru.tensor.sbis.design.R as RDesign

/** Вспомогательный класс для отрисовки штриховки нежелательных дней и их границы.*/
class HatchWithOutlineDrawer(
    context: Context,
    val outlineWidth: Float,
    val gap: Float = 4 * context.resources.displayMetrics.density
) {

    companion object {
        /** Константа для вычисления сдвига линии при наклоне на 45 градусов */
        val diagonalConstant = sqrt(2f)
    }

    /** [Paint] линии границы нежелательных дней */
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getThemeColorInt(RDesign.attr.borderLastVacation)
        strokeWidth = outlineWidth
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(
            floatArrayOf(
                context.resources.getDimension(R.dimen.design_hatch_day_border_dash_width),
                context.resources.getDimension(R.dimen.design_hatch_day_border_dash_gap)
            ), 0f
        )
    }

    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getThemeColorInt(RDesign.attr.borderLastVacation)
        style = Paint.Style.STROKE
        strokeWidth = context.resources.getDimension(R.dimen.design_hatch_day_unwanted_vacation_width)
        pathEffect = null
    }

    val hatchPath = Path()

    var startLinesOffset = 0f
    var topLinesOffset = 0f

    /**
     * Рисует штриховку по canvas
     * @param pathBorder задаем контур для линии границы
     */
    fun drawHatch(canvas: Canvas, pathBorder: Path) {
        drawHatch(canvas, 0, canvas.width)
        drawOnlyBorder(canvas, pathBorder)
    }

    /** Нарисовать штриховку */
    fun drawOnlyHatch(canvas: Canvas) {
        canvas.save()
        canvas.translate(startLinesOffset, topLinesOffset)
        drawHatch(canvas, 0, canvas.width)
        canvas.restore()
    }

    /** Нарисовать штриховку */
    fun drawOnlyHatch(canvas: Canvas, path: Path) {
        with(canvas) {
            save()
            translate(startLinesOffset, topLinesOffset)
            drawPath(path, crossPaint)
            restore()
        }
    }

    /** Нарисовать обводку */
    fun drawOnlyBorder(canvas: Canvas, pathBorder: Path) {
        canvas.drawPath(pathBorder, borderPaint)
    }

    /** Нарисовать обводку */
    fun drawOnlyBorderGridTable(canvas: Canvas, pathBorder: Path) {
        borderPaint.pathEffect = null
        canvas.drawPath(pathBorder, borderPaint)
    }

    /**
     * Рисует штриховку по canvas
     * @param left указывает начало границы отрисовки
     * @param right указывает конец границы отрисовки
     */
    fun drawHatch(canvas: Canvas, left: Int, right: Int) {
        hatchPath.reset()
        fillPath(left, right, 0, canvas.height, hatchPath)
        hatchPath.close()
        drawOnlyHatch(canvas, hatchPath)
    }

    /**
     * Заполнить путь [path] косыми линиями под углом 60
     * в прямоугольнике с углами [left], [top] и [right], [bottom]
     */
    fun fillPath(left: Int, right: Int, top: Int, bottom: Int, path: Path) {
        val actualRight = right + gap * 2f
        val actualBottom = bottom + gap * 1.15f
        val width = (actualRight - left)
        val height = (actualBottom - top).toFloat()
        // деление сдвига на sin 60 это будет сдвиг по оси X
        val dy = gap * 1.15f
        // деление сдвига на sin 30 это будет сдвиг по оси Y
        val dx = gap * 2f
        // высота на tan 60
        val heightTan = height * 1.73f
        // ширина на tan 60
        val widthTan = width * 0.57f
        val linesCount = (width / dx + height / dy).toInt()
        var shiftX = 0f
        var shiftY = 0f
        for (i in 1..linesCount) {
            shiftX += dx
            shiftY += dy

            val sx = left.toFloat() + max(shiftX - heightTan, 0f)
            val sy = min(top + shiftY, actualBottom.toFloat())

            val ex = min(left + shiftX, actualRight)
            val ey = top + max(0f, shiftY - widthTan)

            path.moveTo(sx, sy)
            path.lineTo(ex, ey)
        }
    }
}