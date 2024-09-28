package ru.tensor.sbis.design.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.theme.Direction
import ru.tensor.sbis.design.theme.Direction.*
import kotlin.math.pow
import kotlin.math.roundToInt

private const val GRADIENT_POINT_COUNT = 256
private const val MAX_ALPHA = 255

/**
 * Фабрика создания градиента для плавного перехода от прозрачного цвета к заданному
 *
 * @author us.bessonov
 */
class GradientShaderFactory(@ColorInt var color: Int, private val orientation: Direction = TOP_TO_BOTTOM) :
    ShapeDrawable.ShaderFactory() {

    override fun resize(width: Int, height: Int): Shader? = createGradient(width, height)

    private fun createGradient(width: Int, height: Int): LinearGradient? {
        if (height == 0) {
            return null
        }
        val colors = IntArray(GRADIENT_POINT_COUNT)
        val positions = FloatArray(GRADIENT_POINT_COUNT)

        for (i in 0 until GRADIENT_POINT_COUNT) {
            val x = i.toFloat() / (GRADIENT_POINT_COUNT - 1)
            val alpha = getAlpha(x)

            positions[i] = x
            colors[i] = color.setColorAlpha(alpha)
        }

        if (orientation == BOTTOM_TO_TOP || orientation == RIGHT_TO_LEFT) {
            colors.reverse()
        }

        return when (orientation) {
            TOP_TO_BOTTOM, BOTTOM_TO_TOP -> LinearGradient(
                0f,
                0f,
                0f,
                height.toFloat(),
                colors,
                positions,
                Shader.TileMode.CLAMP
            )
            LEFT_TO_RIGHT, RIGHT_TO_LEFT -> LinearGradient(
                0f,
                0f,
                width.toFloat(),
                0f,
                colors,
                positions,
                Shader.TileMode.CLAMP
            )
        }
    }

    private fun getAlpha(x: Float): Float {
        return -0.64f * x.pow(4) + 0.107f * x.pow(3) + 1.4f * x.pow(2) + 0.133f * x
    }
}

/**
 * Создаёт [Drawable] вертикального градиента с переходом от прозрачного цвета к заданному
 */
fun createGradientDrawable(@ColorInt color: Int): Drawable {
    return PaintDrawable().apply {
        shape = RectShape()
        shaderFactory = GradientShaderFactory(color)
    }
}

private fun Int.setColorAlpha(@FloatRange(from = 0.0, to = 1.0) alphaValue: Float): Int {
    val alpha = (MAX_ALPHA * alphaValue).roundToInt()
    return ColorUtils.setAlphaComponent(this, alpha)
}