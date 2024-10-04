package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import androidx.annotation.FloatRange
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.util.dpToPx
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.LazyThreadSafetyMode.NONE
import ru.tensor.sbis.design.R as RDesign

/**
 * Drawable скругленного поля.
 * Необходим для кастомной анимации расширения поля.
 *
 * @author vv.chekurda
 */
class RecorderFieldDrawable(context: Context) : Drawable() {

    private val oneDp = context.dpToPx(ONE_DP).toFloat()

    private val paint = SimplePaint {
        color = context.getThemeColorInt(com.google.android.material.R.attr.backgroundColor)
        style = Paint.Style.FILL
    }
    private val shadowPaint by lazy(NONE) {
        SimplePaint {
            style = Paint.Style.FILL
            alpha = SHADOW_COLOR_PAINT_ALPHA
            setShadowLayer(
                context.dpToPx(SHADOW_DP).toFloat(),
                oneDp,
                oneDp / 2,
                context.getThemeColorInt(RDesign.attr.shadowColor)
            )
        }
    }
    private val roundRectBounds = RectF()

    private val cornerRadius = context.getDimenPx(RDesign.attr.borderRadius_s).toFloat()
    private var paddingStart = 0f
    private var paddingEnd = 0f
    private val shadowPaddingStart: Float
        get() = if (showShadow) oneDp * 1.4f else 0f

    private val expandInterpolator = DecelerateInterpolator()

    /**
     * Показывать тень.
     */
    var showShadow: Boolean = false
        set(value) {
            field = value
            invalidateSelf()
        }

    /**
     * Отступ левой стороны в сжатом состоянии.
     */
    var collapsedPaddingStart = 0f
        set(value) {
            field = value
            paddingStart = value * (1f - expandFraction)
        }

    /**
     * Отступ правой стороны в сжатом состоянии.
     */
    var collapsedPaddingEnd = 0f
        set(value) {
            field = value
            paddingEnd = value * (1f - expandFraction)
        }

    /**
     * Доля анимации расширения поля.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    var expandFraction: Float = 0f
        set(value) {
            val fraction = value.coerceAtMost(1f).coerceAtLeast(0f)
            if (field == fraction) return
            field = fraction
            val interpolation = 1f - expandInterpolator.getInterpolation(fraction)
            paddingStart = interpolation * collapsedPaddingStart
            paddingEnd = interpolation * collapsedPaddingEnd
            invalidateSelf()
        }

    /**
     * Очистить анимацию.
     */
    fun clear() {
        expandFraction = 0f
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        roundRectBounds.set(
            bounds.left.toFloat() + paddingStart + shadowPaddingStart,
            bounds.top.toFloat(),
            bounds.right.toFloat() - paddingEnd,
            bounds.bottom.toFloat()
        )
        if (showShadow) {
            canvas.drawRoundRect(roundRectBounds, cornerRadius, cornerRadius, shadowPaint)
        }
        canvas.drawRoundRect(roundRectBounds, cornerRadius, cornerRadius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

private const val ONE_DP = 1
private const val SHADOW_DP = 4
private const val SHADOW_COLOR_PAINT_ALPHA = 30