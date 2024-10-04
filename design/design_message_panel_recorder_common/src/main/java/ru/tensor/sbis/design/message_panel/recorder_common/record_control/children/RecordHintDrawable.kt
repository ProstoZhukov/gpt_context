package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.FloatRange
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordHintDrawable.AnimationStep.*
import kotlin.math.roundToInt

/**
 * Drawable текста-подсказки для пользователя (Влево-отмена).
 *
 * @author vv.chekurda
 */
internal class RecordHintDrawable(private val context: Context) : Drawable() {

    /**
     * Параметры анимации.
     *
     * @property slideDurationMs продолжительность скольжения в мс.
     * @property stepDelayMs задержка перед переходом к следующей фазе анимации в мс.
     * @property interpolator интерполятор анимации.
     */
    data class AnimationParams(
        val slideDurationMs: Int = DEFAULT_SLIDING_DURATION_MS,
        val stepDelayMs: Int = DEFAULT_STEP_DELAY_DURATION_MS,
        val interpolator: Interpolator = DecelerateInterpolator()
    )

    /**
     * Шаги цикличной анимации.
     */
    private enum class AnimationStep {
        /** Задержка перед скольжение вправа. */
        SLIDE_RIGHT_DELAY,
        /** Скольжение вправа. */
        SLIDE_RIGHT,
        /** Задержка перед скольжение влево. */
        SLIDE_LEFT_DELAY,
        /** Скольжение влево. */
        SLIDE_LEFT
    }

    private val resources: Resources
        get() = context.resources

    private val hintLayout = createTextLayoutByStyle(context, R.style.RecorderMovementHintDefaultStyle) {
        text = resources.getString(R.string.design_message_panel_recorder_common_movement_hint)
        paint.textSize = paint.textSize
            .coerceAtMost(resources.dp(MAX_TEXT_SIZE_DP).toFloat())
            .coerceAtLeast(resources.dp(MIN_TEXT_SIZE_DP).toFloat())
    }
    private val arrowIcon = hintLayout.copy {
        paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
        text = SbisMobileIcon.Icon.smi_MarkCLeftLight.character.toString()
    }

    private val drawableWidth = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_hint_animated_distance)
    private val availableDx = context.resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_hint_available_dx)

    private var isRunning = false
    private var animationStep = SLIDE_RIGHT_DELAY
    private var lastStepTimeMs = 0L
    private var hintDx = 0f

    /**
     * Текущие параметры анимации.
     */
    var animationParams = AnimationParams()

    /**
     * Смещение отрисовки по оси X.
     */
    var translationX: Float = 0f
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) invalidateSelf()
        }

    /**
     * Масштаб смещения отрисовки по оси Х.
     * Необходим для изменения дистанции движения на узких девайсах.
     */
    var scaleTranslationX: Float = 1f
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) invalidateSelf()
        }

    /**
     * Начать анимацию.
     */
    fun start() {
        if (isRunning) return
        isRunning = true
        invalidateSelf()
    }

    /**
     * Закончить анимацию.
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
        clearDrawingState()
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        if (isRunning && translationX == 0f) animate()
        canvas.withTranslation(x = scaleTranslationX * translationX + hintDx) {
            arrowIcon.draw(canvas)
            hintLayout.draw(canvas)
        }
    }

    private fun animate() {
        val currentTime = System.currentTimeMillis()
        val dt = currentTime - lastStepTimeMs
        when (animationStep) {
            SLIDE_RIGHT -> {
                val progress = updateStep(dt, animationParams.slideDurationMs)
                hintDx = progress * availableDx
            }
            SLIDE_LEFT -> {
                val progress = updateStep(dt, animationParams.slideDurationMs)
                hintDx = (1 - progress) * availableDx
            }
            else -> updateStep(dt, animationParams.stepDelayMs)
        }
        invalidateSelf()
    }

    @FloatRange(from = 0.0, to = 1.0)
    private fun updateStep(deltaTimeMs: Long, stepDuration: Int): Float {
        val progress = animationParams.interpolator.getInterpolation(minOf(deltaTimeMs / stepDuration.toFloat(), 1f))
        if (progress == 1f) {
            animationStep = when (animationStep) {
                SLIDE_RIGHT_DELAY -> SLIDE_RIGHT
                SLIDE_RIGHT -> SLIDE_LEFT_DELAY
                SLIDE_LEFT_DELAY -> SLIDE_LEFT
                SLIDE_LEFT -> SLIDE_RIGHT_DELAY
            }
            lastStepTimeMs = System.currentTimeMillis()
        }
        return progress
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        arrowIcon.layout(left, top + (hintLayout.height - arrowIcon.height) / 2)
        hintLayout.layout(arrowIcon.right, top)
    }

    override fun getIntrinsicWidth(): Int =
        drawableWidth

    override fun getIntrinsicHeight(): Int =
        hintLayout.height

    override fun setAlpha(alpha: Int) {
        val layoutAlpha  = alpha / PAINT_MAX_ALPHA.toFloat()
        arrowIcon.alpha = layoutAlpha
        hintLayout.alpha = layoutAlpha
    }

    override fun getAlpha(): Int =
        (hintLayout.alpha * PAINT_MAX_ALPHA).roundToInt()

    override fun setColorFilter(colorFilter: ColorFilter?) {
        arrowIcon.textPaint.colorFilter = colorFilter
        hintLayout.textPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private fun clearDrawingState() {
        alpha = 0
        animationStep = SLIDE_RIGHT_DELAY
        lastStepTimeMs = System.currentTimeMillis()
        hintDx = 0f
        invalidateSelf()
    }
}

private const val DEFAULT_SLIDING_DURATION_MS = 800
private const val DEFAULT_STEP_DELAY_DURATION_MS = 200
private const val MAX_TEXT_SIZE_DP = 16
private const val MIN_TEXT_SIZE_DP = 12