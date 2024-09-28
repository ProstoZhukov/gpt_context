package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.withRotation
import androidx.core.view.isVisible
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.R as RDesign

/**
 * Заглушка камеры с анимацией мерцания.
 *
 * @author vv.chekurda
 */
internal class CameraFlickerStubView(context: Context) : View(context) {

    private val flickerPaint = SimplePaint()
    private val outlinePaint = SimplePaint {
        style = Paint.Style.STROKE
        strokeWidth = context.resources.dp(2).toFloat()
    }
    private val backgroundPaint = SimplePaint {
        color = ContextCompat.getColor(context, RDesign.color.palette_color_gray16)
    }
    private val flickerColor = ColorUtils.setAlphaComponent(Color.WHITE, FLICKER_ALPHA)
    private val outlineColor = ColorUtils.setAlphaComponent(Color.WHITE, OUTLINE_ALPHA)

    private var radius: Float = 0f
    private var flickerSize: Float = 0f
    private val rectF: RectF = RectF()
    private val translationMatrix: Matrix = Matrix()

    private var isRunning: Boolean = false
    private var progress = 0f
    private var lastUpdateTime: Long = 0
    private var repeatProgress = 1f + REPEAT_DELAY_MS.toFloat() / FLICKER_ANIMATION_TIME_MS
    private val interpolator = AccelerateDecelerateInterpolator()

    private val hideInterpolator = AccelerateInterpolator()
    private var hideAnimator: ValueAnimator? = null

    /**
     * Начать анимацию мерцания.
     */
    fun start() {
        hideAnimator?.cancel()
        if (isRunning) clear()
        isRunning = true
        invalidate()
    }

    /**
     * Скрыть компонент.
     */
    fun hide() {
        hideAnimator?.cancel()
        hideAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = HIDE_DURATION_MS
            addUpdateListener { alpha = 1f - hideInterpolator.getInterpolation(it.animatedFraction) }
            doOnEnd {
                isVisible = false
                alpha = 1f
                clear()
            }
            start()
        }
    }

    /**
     * Сбросить компонент.
     */
    fun clear() {
        hideAnimator?.cancel()
        hideAnimator = null
        isRunning = false
        progress = 0f
        lastUpdateTime = 0L
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val availableWidth = w - paddingStart - paddingEnd
        val availableHeight = h - paddingTop - paddingBottom
        radius = availableWidth.coerceAtLeast(availableHeight) / 2f
        rectF.set(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            w - paddingEnd.toFloat(),
            h - paddingBottom.toFloat()
        )
        flickerSize = width.toFloat() * 5 / 6f
        updateShaders()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(rectF, radius, radius, backgroundPaint)
        if (!isRunning) return

        val currentTime = System.currentTimeMillis()
        if (lastUpdateTime != 0L) {
            val dt = currentTime - lastUpdateTime
            progress += dt / FLICKER_ANIMATION_TIME_MS.toFloat()
            if (progress > repeatProgress) progress = 0f
        }
        lastUpdateTime = currentTime

        if (progress < 1f) {
            val interpolation = interpolator.getInterpolation(progress)
            val startX = - flickerSize
            val dx = startX + (width - startX) * interpolation
            translationMatrix.setTranslate(dx, 0f)
            flickerPaint.shader.setLocalMatrix(translationMatrix)
            outlinePaint.shader.setLocalMatrix(translationMatrix)

            canvas.withRotation(ROTATION_DEGREES, radius, radius) {
                drawRoundRect(rectF, radius, radius, flickerPaint)
                drawRoundRect(rectF, radius, radius, outlinePaint)
            }
        }
        invalidate()
    }

    private fun updateShaders() {
        fun createShader(@ColorInt color: Int): Shader = LinearGradient(
            0f,
            0f,
            flickerSize,
            0f,
            intArrayOf(
                Color.TRANSPARENT,
                color,
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )
        flickerPaint.shader = createShader(flickerColor)
        outlinePaint.shader = createShader(outlineColor)
    }
}

private const val ROTATION_DEGREES = 30f
private const val FLICKER_ALPHA = (PAINT_MAX_ALPHA * 0.3).toInt()
private const val OUTLINE_ALPHA = (PAINT_MAX_ALPHA * 0.6).toInt()
private const val FLICKER_ANIMATION_TIME_MS = 1000L
private const val REPEAT_DELAY_MS = 200L
private const val HIDE_DURATION_MS = 200L