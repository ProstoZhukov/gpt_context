package ru.tensor.sbis.design.view_ext.drawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.core.graphics.withRotation
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.R as RDesign
/**
 * Drawable часов с анимацией стрелок.
 *
 * Для анимированной отрисовки часов необходимо использовать ее в качестве фона [View.setBackground] или [View.setForeground].
 * Для кастомных [View] есть альтернативное решение - необходимо установить в [ClockDrawable] колбэк [Drawable.setCallback]
 * и переопределить метод [View.verifyDrawable] у вашей кастомной [View],
 * чтобы подтвердить причастность данной [ClockDrawable] ко [View],
 * в этом случае область с часами будет перерисовываться самостоятельно во время анимации.
 *
 * @author vv.chekurda
 */
class ClockDrawable(context: Context) : Drawable() {

    private companion object {
        private var startTime = System.currentTimeMillis()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var hourDurationTimeMs: Int = DEFAULT_HOUR_ROTATION_TIME_MS
    private var minuteDurationTimeMs: Int = (DEFAULT_HOUR_ROTATION_TIME_MS * DEFAULT_MINUTE_ROTATION_RATIO).toInt()
    private val circleDiameter: Int
        get() = (size - paint.strokeWidth).toInt()

    /**
     * Установить/получить размер часов в px.
     */
    @get:Px
    var size: Int
        get() = minOf(bounds.width(), bounds.height())
        set(value) {
            if (value == size) return
            setBounds(
                bounds.left,
                bounds.top,
                bounds.left + value,
                bounds.top + value
            )
        }

    /**
     * Установить/получить цвет часов.
     */
    @get:ColorInt
    var color: Int
        get() = paint.color
        set(value) {
            if (value == paint.color) return
            paint.color = value
        }

    /**
     * Базовая линия на которой размещаетя окружность часов.
     */
    val baseline: Int
        get() = (bounds.centerY() - bounds.top + circleDiameter / 2f + paint.strokeWidth / 2f).toInt()

    init {
        size = context.getDimenPx(RDesign.attr.iconSize_xs)
    }

    /**
     * Установить периоды вращения стрелок часов.
     *
     * @param hourRotationMs период вращения часовой стрелки в мс.
     * @param minuteRotationMs период вращения минутной стрелки в мс.
     */
    fun setRotationDuration(
        hourRotationMs: Int = DEFAULT_HOUR_ROTATION_TIME_MS,
        minuteRotationMs: Int = (hourRotationMs * DEFAULT_MINUTE_ROTATION_RATIO).toInt()
    ) {
        hourDurationTimeMs = hourRotationMs
        minuteDurationTimeMs = minuteRotationMs
    }

    override fun setAlpha(@IntRange(from = 0, to = PAINT_MAX_ALPHA.toLong()) alpha: Int) {
        if (alpha == paint.alpha) return
        paint.alpha = alpha
    }

    override fun onBoundsChange(bounds: Rect) {
        paint.strokeWidth = maxOf(size * STROKE_WIDTH_RATIO, 1f)
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return

        val (centerX, centerY) = bounds.centerX().toFloat() to bounds.centerY().toFloat()
        val diameter = circleDiameter
        val currentTime = System.currentTimeMillis()

        with(canvas) {
            // Окружность часов
            drawCircle(centerX, centerY, diameter / 2f, paint)
            // Минутная стрелка
            drawArrow(
                currentTime = currentTime,
                rotateTime = minuteDurationTimeMs,
                startX = centerX,
                startY = centerY,
                endY = centerY - diameter * MINUTE_ARROW_WIDTH_RATIO
            )
            // Часовая стрелка
            drawArrow(
                currentTime = currentTime,
                rotateTime = hourDurationTimeMs,
                startX = centerX,
                startY = centerY,
                endX = centerX + diameter * HOUR_ARROW_WIDTH_RATIO
            )
        }
        invalidateSelf()
    }

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = size

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int =
        PixelFormat.TRANSPARENT

    /**
     * Нарисовать стрелку часов.
     *
     * @param currentTime текущее время.
     * @param rotateTime время полного оборота.
     * @param startX начало стрелки по х.
     * @param startY начало стрелки по y.
     * @param endX конец стрелки по x.
     * @param endY конец стрелки по y.
     */
    private fun Canvas.drawArrow(
        currentTime: Long,
        rotateTime: Int,
        startX: Float,
        startY: Float,
        endX: Float = startX,
        endY: Float = startY
    ) {
        withRotation(
            degrees = CIRCLE_DEGREES * ((currentTime - startTime) % rotateTime) / rotateTime.toFloat(),
            pivotX = startX,
            pivotY = startY
        ) {
            drawLine(
                startX,
                startY,
                endX,
                endY,
                paint
            )
        }
    }
}

/**
 * Максимальная alpha краски.
 */
private const val PAINT_MAX_ALPHA = 255

/**
 * Стандартное время оборота часовой стрелки.
 */
private const val DEFAULT_HOUR_ROTATION_TIME_MS = 4500

/**
 * Стандартное соотношение оборотов минутной стрелки к часовой.
 */
private const val DEFAULT_MINUTE_ROTATION_RATIO = 1 / 3f

/**
 * Стандартное соотношение ширины линии краски к размеру часов.
 */
private const val STROKE_WIDTH_RATIO = 1 / 13f

/**
 * Стандартное соотношение длины минутной стрелки к размеру часов.
 */
private const val MINUTE_ARROW_WIDTH_RATIO = 1 / 3.25f

/**
 * Стандартное соотношение длины часовой стрелки к размеру часов.
 */
private const val HOUR_ARROW_WIDTH_RATIO = 1 / 4f

/**
 * Количество градусов в окружности.
 */
private const val CIRCLE_DEGREES = 360