package ru.tensor.sbis.widget_player.layout.internal.fadingedge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.view.View
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import kotlin.math.max
import kotlin.math.min

/**
 * Горизонтальная реализация эффекта Fading Edge, которая будет отображаться по левому и по правому краю у View.
 *
 * @author am.boldinov
 */
internal class HorizontalFadingEdgeDrawer : FadingEdgeDrawer {

    private var paint: Paint? = null
    private var matrix: Matrix? = null
    private var shader: Shader? = null
    private var defaultShader: Shader? = null

    override var color: Int = 0
        set(value) {
            if (field != value) {
                field = value
                shader = createFadingEdgeShader(value)
            }
        }

    override var length: Int = 0

    override fun draw(view: View, canvas: Canvas, startStrength: Float, endStrength: Float) {
        if (length == 0) {
            return
        }
        val left = view.scrollX + view.paddingLeft
        val right = left + view.right - view.left - view.paddingRight - view.paddingLeft
        val top = view.scrollY + view.paddingTop
        val bottom = top + view.bottom - view.top - view.paddingBottom - view.paddingTop
        val fadingRectWidth = if (left + length > right - length) {
            // если размера View не хватает для отображения fading edge c каждой из сторон - отображаем до центра
            (right - left) / 2f
        } else {
            length.toFloat()
        }
        val leftFadeStrength = max(0.0f, min(1.0f, startStrength))
        val drawLeftEdge = leftFadeStrength * length > 1.0f
        val rightFadeStrength = max(0.0f, min(1.0f, endStrength))
        val drawRightEdge = rightFadeStrength * length > 1.0f
        val paint = paint ?: Paint().also {
            paint = it
        }
        val matrix = matrix ?: Matrix().also {
            matrix = it
        }
        val shader = shader ?: view.context.getDefaultFadingEdgeShader()
        if (drawRightEdge) {
            with(matrix) {
                setScale(1f, length * rightFadeStrength)
                postRotate(90f)
                postTranslate(right.toFloat(), top.toFloat())
                shader.setLocalMatrix(this)
                paint.setShader(shader)
                canvas.drawRect(
                    right - fadingRectWidth,
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    paint
                )
            }
        }
        if (drawLeftEdge) {
            with(matrix) {
                setScale(1f, length * leftFadeStrength)
                postRotate(-90f)
                postTranslate(left.toFloat(), top.toFloat())
                shader.setLocalMatrix(this)
                paint.setShader(shader)
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    left + fadingRectWidth,
                    bottom.toFloat(),
                    paint
                )
            }
        }
    }

    private fun Context.getDefaultFadingEdgeShader(): Shader {
        return defaultShader ?: createFadingEdgeShader(
            OtherColor.SHADOW.getValue(this)
        ).also {
            defaultShader = it
        }
    }

    private fun createFadingEdgeShader(@ColorInt color: Int): Shader {
        return LinearGradient(0f, 0f, 0f, 1f, color, Color.TRANSPARENT, Shader.TileMode.CLAMP)
    }
}