package ru.tensor.sbis.design.theme.utils

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.theme.global_variables.OtherColor
import kotlin.math.roundToInt

/**
 * Создание [Drawable] с вертикальным градиентом в несколько переходов.
 * @param context темизированный контекст
 */
fun createGradientDrawable(context: Context): Drawable = PaintDrawable().apply {
    shape = RectShape()
    val brandColor = OtherColor.BRAND.getValue(context)
    shaderFactory = GradientShaderFactory(brandColor)
}

/**
 * Фабрика создания градиента с несколькими переходами в разных точках экрана и разной прозрачностью цвета [color].
 */
private class GradientShaderFactory(@ColorInt var color: Int) : ShapeDrawable.ShaderFactory() {

    override fun resize(width: Int, height: Int): Shader? = createVerticalGradient(height)

    private fun createVerticalGradient(height: Int): LinearGradient? {
        if (height == 0) return null
        val positions = GRADIENT_POINTS.toFloatArray()
        val colors = GRADIENT_ALPHA_VALUES.map { color.setAlpha(it) }.toIntArray()
        return LinearGradient(
            0f,
            height.toFloat(),
            0f,
            0f,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
    }

    @ColorInt
    private fun Int.setAlpha(@FloatRange(from = 0.0, to = 1.0) alphaValue: Float): Int =
        ColorUtils.setAlphaComponent(this, (MAX_ALPHA * alphaValue).roundToInt())

    /**
     * Точки переходов градиента:
     * background: linear-gradient (360deg, rgba(0, 133, 242, 0.4) 0%, rgba(0, 133, 242, 0.3) 10%,
     * rgba(0, 133, 242, 0.2) 35%, rgba(0, 133, 242, 0) 100%);
     */
    private companion object {
        val GRADIENT_POINTS = listOf(0f, 0.1f, 0.35f, 1f)
        val GRADIENT_ALPHA_VALUES = listOf(0.4f, 0.3f, 0.2f, 0f)
        const val MAX_ALPHA = 255
    }
}