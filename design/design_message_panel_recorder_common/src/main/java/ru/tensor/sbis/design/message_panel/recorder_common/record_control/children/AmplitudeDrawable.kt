package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import kotlin.math.abs

/**
 * Drawable амплитуды громкости микрофона при записи аудиосообщения.
 *
 * @author vv.chekurda
 */
class AmplitudeDrawable(private val paint: Paint) : Drawable() {

    private var amplitudeScale = 0f
    private var amplitudeChangingSpeed = 0f
    private var lastDrawingTimeMs = System.currentTimeMillis()

    /**
     * Минимальныый радиус амплитуды.
     */
    var minRadius = 0f
        set(value) {
            if (field == value) return
            field = value
        }

    /**
     * Максимальный радиус амплитуды.
     */
    var maxRadius = 0f
        set(value) {
            field = value
            amplitudeScale = maxRadius / minRadius - 1f
        }

    /**
     * Радиус пульсации амплитуды.
     */
    @get:Px
    var pulsationRadiusDx = 0f

    @get:Px
    private val amplitudeAvailableDiff: Float
        get() = maxRadius - minRadius

    @get:FloatRange(from = 0.0, to = 1.0)
    private val pulsationPick: Float
        get() = pulsationRadiusDx / amplitudeAvailableDiff
    private var animatePulsation: Boolean = false
    private var isPulseGrowing = true
    private var pulsationProgress = 0f
    private val pulsationInterpolator = AccelerateDecelerateInterpolator()
    private var pulseStableAmplitude = 0f
    private var isRunning: Boolean = false
    private var isStopped: Boolean = false

    /**
     * Анимированная амплитуда.
     */
    var animatedAmplitude = 0f
        private set

    @FloatRange(from = 0.0, to = 1.0)
    var amplitude: Float = 0f
        set(value) {
            val rangedValue = value.coerceAtMost(1f).coerceAtLeast(0f)
            if (field == rangedValue) return
            field = rangedValue
            val animatedAmplitudeDelta = field - animatedAmplitude
            val animationTime = if (field > animatedAmplitude) EXPAND_ANIMATION_TIME_MS else COLLAPSE_ANIMATION_TIME_MS
            amplitudeChangingSpeed = animatedAmplitudeDelta / animationTime
            checkPulsationAnimation(field)
            isRunning = true
            invalidateSelf()
        }

    /**
     * Остановить амплитуду.
     */
    fun stop() {
        amplitude = 0f
        isStopped = true
        if (animatePulsation) {
            animatePulsation = false
            pulsationProgress = 0f
            pulseStableAmplitude = 0f
            isPulseGrowing = false
        }
    }

    /**
     * Очистить состояние амплитуды.
     */
    fun clear() {
        amplitude = 0f
        animatedAmplitude = 0f
        amplitudeChangingSpeed = 0f
        isPulseGrowing = true
        animatePulsation = false
        isRunning = false
        isStopped = false
        setVisible(true, false)
    }

    /**
     * Установить цвет амплитуды.
     */
    fun setColor(@ColorInt color: Int, alpha: Int) {
        paint.color = color
        paint.alpha = alpha
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        val currentTime = System.currentTimeMillis()
        val dt = minOf(currentTime - lastDrawingTimeMs, FRAME_TIME_MS)
        lastDrawingTimeMs = currentTime
        updateAmplitude(dt)

        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        canvas.drawCircle(centerX, centerY, minRadius + amplitudeAvailableDiff * animatedAmplitude, paint)
        if (isRunning) invalidateSelf()
    }

    private fun checkPulsationAnimation(newAmplitude: Float) {
        if (animatePulsation) {
            val pulseStableRange = (pulseStableAmplitude - pulsationPick)..(pulseStableAmplitude + pulsationPick)
            animatePulsation = newAmplitude in pulseStableRange
        } else {
            val endRange = (newAmplitude - ANIMATION_END_POINT_DELTA)..(newAmplitude + ANIMATION_END_POINT_DELTA)
            val isLowAmplitudeSpeed = abs(amplitudeChangingSpeed) <= PULSE_AVAILABLE_ANIMATION_SPEED
            if (!isStopped && animatedAmplitude in endRange && isLowAmplitudeSpeed) {
                animatePulsation = true
                pulseStableAmplitude = newAmplitude
                pulsationProgress = 0f
                isPulseGrowing = true
            }
        }
    }

    private fun updateAmplitude(dt: Long) {
        if (animatePulsation) {
            updatePulsationAmplitude(dt)
        } else {
            updateAnimatedAmplitude(dt)
        }
    }

    private fun updatePulsationAmplitude(dt: Long) {
        pulsationProgress = minOf(pulsationProgress + dt / PULSATION_TIME_MS, 1f)
        val interpolation = pulsationInterpolator.getInterpolation(pulsationProgress)
        animatedAmplitude = pulseStableAmplitude + if (isPulseGrowing) {
            interpolation * pulsationPick
        } else {
            pulsationPick - interpolation * pulsationPick
        }
        if (pulsationProgress == 1f) {
            pulsationProgress = 0f
            isPulseGrowing = !isPulseGrowing
        }
    }

    private fun updateAnimatedAmplitude(dt: Long) {
        if (amplitude == animatedAmplitude) return
        animatedAmplitude += amplitudeChangingSpeed * dt
        val isSoBig = amplitudeChangingSpeed > 0 && animatedAmplitude > amplitude
        val isSoSmall = amplitudeChangingSpeed < 0 && animatedAmplitude < amplitude
        if (isSoBig || isSoSmall) {
            animatedAmplitude = amplitude
        }
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

private const val EXPAND_ANIMATION_TIME_MS = 150f
private const val COLLAPSE_ANIMATION_TIME_MS = 250f
private const val PULSATION_TIME_MS = 1000f
private const val PULSE_AVAILABLE_ANIMATION_SPEED = 0.005f
private const val ANIMATION_END_POINT_DELTA = 0.01f
private const val FRAME_TIME_MS = 17L