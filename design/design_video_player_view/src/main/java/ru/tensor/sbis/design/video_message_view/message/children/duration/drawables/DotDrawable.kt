package ru.tensor.sbis.design.video_message_view.message.children.duration.drawables

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Drawable для отображения точки.
 *
 * @author da.zhukov
 */
internal class DotDrawable : Drawable() {

    /**
     * Размер точки.
     */
    @Px
    var size: Int = DEFAULT_DOTS_SIZE_PX
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                dotRadius = value / 2f
                invalidateSelf()
            }
        }

    private var dotRadius: Float = size / 2f

    @get:ColorInt
    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            paint.color = value
        }

    /**
     * Основная краска, которой рисуется точка.
     */
    private val paint = Paint().apply {
        isAntiAlias = true
        color = textColor
    }

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun draw(canvas: Canvas) {
        if (!isVisible) return

        val dotHorizontalCenter = bounds.left + dotRadius
        canvas.drawCircle(dotHorizontalCenter, bounds.top + dotRadius, dotRadius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

/**
 * Стандартный размер точк в px.
 */
private const val DEFAULT_DOTS_SIZE_PX = 50