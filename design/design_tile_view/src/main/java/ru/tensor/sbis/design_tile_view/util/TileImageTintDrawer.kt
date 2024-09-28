package ru.tensor.sbis.design_tile_view.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design_tile_view.R
import ru.tensor.sbis.design.R as DesignR

/**
 * Режим затемнения изображения
 *
 * @author us.bessonov
 */
internal enum class TintMode {
    /** Градиент с затемнением наверху */
    GRADIENT,
    /** Затемнение под контентом и градиент над ним */
    FILL_AND_GRADIENT,
    /** Отсутствие затемнения */
    NONE
}

/**
 * Предназначен для отрисовки затемнения поверх изображения в плитке
 *
 * @author us.bessonov
 */
internal class TileImageTintDrawer(context: Context) {

    @ColorInt
    private val fillColor = ContextCompat.getColor(context, R.color.design_tile_view_image_tint_fill_color)

    @ColorInt
    private val thinGradientEndColor = ContextCompat.getColor(context, DesignR.color.palette_color_transparent)

    @ColorInt
    private val gradientStartColor = ContextCompat.getColor(context, DesignR.color.palette_alpha_color_black7)

    @ColorInt
    private val gradientEndColor = ContextCompat.getColor(context, DesignR.color.palette_alpha_color_black3)

    @Px
    private val imageTintGradientHeight =
        context.resources.getDimensionPixelSize(R.dimen.design_tile_view_image_tint_gradient_height)

    private val imageTintGradient = GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(gradientStartColor, gradientEndColor)
    )

    private val fillPaint = Paint().apply {
        color = fillColor
        isAntiAlias = true
    }

    /** @SelfDocumented */
    var tintMode = TintMode.NONE
        private set

    /** @SelfDocumented */
    @Px
    var imageWidth = 0

    /** @SelfDocumented */
    @Px
    var imageHeight = 0

    /** @SelfDocumented */
    @Px
    var fillHeight = 0

    /** @SelfDocumented */
    fun setTintMode(mode: TintMode) {
        if (mode == tintMode) return
        tintMode = mode
        when (mode) {
            TintMode.GRADIENT -> {
                imageTintGradient.colors = intArrayOf(gradientStartColor, gradientEndColor)
            }
            TintMode.FILL_AND_GRADIENT -> {
                imageTintGradient.colors = intArrayOf(thinGradientEndColor, fillColor)

            }
            else -> Unit
        }
    }

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        if (tintMode == TintMode.NONE) return
        when (tintMode) {
            TintMode.NONE -> return
            TintMode.GRADIENT -> {
                imageTintGradient.setBounds(0, 0, imageWidth, imageHeight)
                imageTintGradient.draw(canvas)
            }
            TintMode.FILL_AND_GRADIENT -> {
                val bottom = imageHeight - fillHeight
                imageTintGradient.setBounds(0, bottom - imageTintGradientHeight, imageWidth, bottom)
                imageTintGradient.draw(canvas)
                canvas.drawRect(
                    0f,
                    imageHeight.toFloat() - fillHeight,
                    imageWidth.toFloat(),
                    imageHeight.toFloat(),
                    fillPaint
                )
            }
        }
    }
}