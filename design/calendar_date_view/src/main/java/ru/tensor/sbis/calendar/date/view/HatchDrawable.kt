package ru.tensor.sbis.calendar.date.view

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.TypedValue
import kotlin.math.max
import kotlin.math.min

/**
 * Класс для отрисовки фона. Рисует линии под наклоном 45 градусов, заполняя все пространство объекта.
 */
class HatchDrawable(
    resources: Resources,
    private val lineWidth: Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1f,
        resources.displayMetrics
    ),
    private val lineSpacing: Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        4f,
        resources.displayMetrics
    )
): Drawable() {

    /**
     * Цвет линий
     */
    var color: Int
        get() = crossPaint.color
        set(value) {
            crossPaint.color = value
        }

    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt()
        strokeWidth = lineWidth
    }

    /**
     * Рисует штриховку по canvas под углом 60 в прямоугольнике [getBounds]
     */
    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        // деление сдвига на sin 60 это будет сдвиг по оси X
        val dy = lineSpacing * 1.15f
        // деление сдвига на sin 30 это будет сдвиг по оси Y
        val dx = lineSpacing * 2f
        // высота на tan 60
        val heightTan = height * 1.73f
        // ширина на tan 60
        val widthTan = width * 0.57f

        val linesCount = (width / dx + height / dy).toInt()
        canvas.save()
        var shiftY = 0f
        var shiftX = 0f
        for (i in 1..linesCount) {
            shiftX += dx
            shiftY += dy

            val sx = bounds.left.toFloat() + max(shiftX - heightTan, 0f)
            val sy = min(bounds.top + shiftY, bounds.bottom.toFloat())

            val ex = min(bounds.left + shiftX, bounds.right.toFloat())
            val ey = bounds.top + max(0f, shiftY - widthTan)

            canvas.drawLine(sx, sy, ex, ey, crossPaint)
        }
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        crossPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        crossPaint.colorFilter = colorFilter
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.UNKNOWN

}