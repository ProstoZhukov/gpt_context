package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.FloatRange
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordingIndicatorDrawable.AnimationStep.FADE_IN
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordingIndicatorDrawable.AnimationStep.FADE_OUT
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Drawable индикатора записи (красная точка).
 *
 * @author vv.chekurda
 */
class RecordingIndicatorDrawable(context: Context) : Drawable() {

    /**
     * Параметры анимации.
     *
     * @property fadeDurationMs продолжительность затухания в мс.
     * @property stepDelayMs задержка в мс перед переходом к следующей фазе (затухания/появления).
     * @property interpolator индерполятор анимации.
     */
    data class AnimationParams(
        val fadeDurationMs: Int = DEFAULT_FADING_DURATION_MS,
        val stepDelayMs: Int = DEFAULT_STEP_DELAY_DURATION_MS,
        val interpolator: Interpolator = DecelerateInterpolator()
    )

    /**
     * Шаги цикличной анимации.
     */
    private enum class AnimationStep {
        /** Затухание. */
        FADE_OUT,
        /** Задержка после затухания. */
        FADE_OUT_DELAY,
        /** Появление. */
        FADE_IN,
        /** Задержка после появления. */
        FADE_IN_DELAY
    }

    private val paint = SimplePaint {
        color = StyleColor.DANGER.getIconColor(context)
        style = Paint.Style.FILL
    }

    private var animationStep = FADE_OUT
    private var isRunning: Boolean = false
    private var lastStepTimeMs = 0L

    /**
     * Текущие параметры анимации.
     */
    var animationParams = AnimationParams()

    /**
     * Смещение отрисовки по оси X.
     */
    var translationX: Float = 0f

    /**
     * Начать анимацию.
     */
    fun start() {
        if (isRunning) return
        isRunning = true
        animationStep = FADE_OUT
        alpha = PAINT_MAX_ALPHA
        syncTimeMs = 0L
        lastStepTimeMs = System.currentTimeMillis()
        invalidateSelf()
    }

    /**
     * Остановить анимацию.
     */
    fun stop() {
        if (!isRunning) return
        isRunning = false
        invalidateSelf()
    }

    /**
     * Очистить анимацию.
     */
    fun clear() {
        isRunning = false
        animationStep = FADE_OUT
        syncTimeMs = 0L
        alpha = 0
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        if (isRunning) animate()
        canvas.withTranslation(x = translationX) {
            drawCircle(
                bounds.centerX().toFloat(),
                bounds.centerY().toFloat(),
                bounds.height() / 2f,
                paint
            )
        }
    }

    private fun animate() {
        when (animationStep) {
            FADE_OUT -> {
                val progress = updateStep(animationParams.fadeDurationMs)
                alpha = ((1f - progress) * PAINT_MAX_ALPHA).toInt()
            }
            FADE_IN -> {
                val progress = updateStep(animationParams.fadeDurationMs)
                alpha = (progress * PAINT_MAX_ALPHA).toInt()
            }
            else -> updateStep(animationParams.stepDelayMs)
        }
        invalidateSelf()
    }

    private var syncTimeMs = 0L

    @FloatRange(from = 0.0, to = 1.0)
    private fun updateStep(stepDuration: Int): Float {
        val currentTime = System.currentTimeMillis()
        val dt = currentTime + syncTimeMs - lastStepTimeMs
        val progress = animationParams.interpolator.getInterpolation(minOf(dt / stepDuration.toFloat(), 1f))
        if (progress == 1f) {
            syncTimeMs = dt - stepDuration
            animationStep = AnimationStep.values()[animationStep.ordinal.inc() % AnimationStep.values().size]
            lastStepTimeMs = currentTime
        }
        return progress
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getAlpha(): Int =
        paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int =
        PixelFormat.TRANSLUCENT
}

/**
 * Стандартная продолжительность затухания в мс.
 */
private const val DEFAULT_FADING_DURATION_MS = 850

/**
 * Стандартная задержка перед переходом к следующей фазе анимации затухания в мс.
 */
private const val DEFAULT_STEP_DELAY_DURATION_MS = 150