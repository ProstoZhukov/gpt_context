package ru.tensor.sbis.design.video_message_view.message.children.recognize.drawables

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Drawable для отображения анимируемых точек загрузки.
 * @see DotsParams
 *
 * @author da.zhukov
 */
internal class LoadingDotsDrawable : Drawable() {

    /**
     * Параметры анимируемых точек.
     *
     * @property durationMs общее время продолжительности анимации.
     * @property count количество точек.
     * @property size размер точек в px.
     * @property accentSize размер акцентной точки в px.
     * @property spacing отстыпы между точками в px.
     */
    data class DotsParams(
        val durationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
        val durationChangeSizeMs: Int = DEFAULT_ANIMATION_CHANGE_SIZE_DURATION_MS,
        val count: Int = DEFAULT_DOTS_COUNT,
        @Px val size: Int = DEFAULT_DOTS_SIZE_PX,
        @Px val accentSize: Int = DEFAULT_DOTS_ACCENT_SIZE_PX,
        @Px val spacing: Int = size
    ) {
        val oneStepDurationMs: Int = durationMs / count
        val dotRadius: Float = size / 2f
        val brightDotRadius: Float = accentSize / 2f
    }

    /**
     * Установить/получить параметры с настройками анимируемых точек.
     * @see DotsParams
     */
    var params = DotsParams()
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                clearSteps()
                invalidateSelf()
            }
        }

    @get:ColorInt
    var textColor: Int = Color.GRAY
        set(value) {
            field = value
            paint.color = value
            brightPaint.color = value
        }

    /**
     * Основная краска, которой рисуются отображаемые точки.
     */
    private val paint = Paint().apply {
        isAntiAlias = true
        color = textColor
    }

    /**
     * Вспомогательная краска для отрисовки перебегания точки.
     */
    private val brightPaint = Paint().apply {
        isAntiAlias = true
        color = textColor
    }

    /**
     * Интерполятор анимации изменения размера точек.
     */
    private val changeSizeInterpolator = LinearInterpolator()

    /**
     * Время обновления шага анимации в мс.
     */
    private var stepUpdateTimeMs = 0L

    /**
     * Номер самого последнего шага анимации.
     */
    private val lastStep: Int
        get() = params.count

    /**
     * Текущий шаг анимации.
     */
    private var step = 0
        set(value) {
            field = value % (lastStep + 1)
        }

    /**
     * Сбросить все шаги анимации к исходному состоянию.
     */
    private fun clearSteps() {
        step = 0
        stepUpdateTimeMs = System.currentTimeMillis()
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean =
        super.setVisible(visible, restart).also {
            if (restart) clearSteps()
        }

    override fun getIntrinsicWidth(): Int =
        with(params) { size * count + spacing * (count - 1) }

    override fun getIntrinsicHeight(): Int =
        params.size

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        val currentTime = System.currentTimeMillis()

        val interpolation = minOf((currentTime - stepUpdateTimeMs) / params.oneStepDurationMs.toFloat(), 1f)
        val deltaRadius =
            (params.dotRadius - params.brightDotRadius) * changeSizeInterpolator.getInterpolation(interpolation)

        repeat(params.count) { dotIndex ->
            val dotPaint: Paint
            val dotRadius: Float
            val size: Int
            when {
                // Точка увеличивается
                dotIndex == step -> {
                    dotPaint = brightPaint
                    dotRadius = params.dotRadius - deltaRadius
                    size = params.size
                }
                // Точка уменьшается
                dotIndex == step - 1 || (lastStep == dotIndex && step != lastStep) -> {
                    dotPaint = brightPaint
                    dotRadius = params.brightDotRadius + deltaRadius
                    size = params.size
                }
                // Точка просто отображается без анимаций
                dotIndex <= step || dotIndex >= step -> {
                    dotPaint = paint
                    dotRadius = params.dotRadius
                    size = params.size
                }
                // Для остальных очередь не дошла - не рисуем
                else -> return@repeat
            }
            val dotHorizontalCenter = bounds.left + dotRadius + size * dotIndex + params.spacing * dotIndex
            canvas.drawCircle(dotHorizontalCenter, bounds.top + params.brightDotRadius, dotRadius, dotPaint)
        }

        val isNextStep = currentTime - stepUpdateTimeMs >= params.oneStepDurationMs
        if (isNextStep) {
            step++
            stepUpdateTimeMs = currentTime
        }
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        brightPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        brightPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

/**
 * Стандартное количество анимируемых точек.
 */
private const val DEFAULT_DOTS_COUNT = 3

/**
 * Стандартная продолжительность анимации появления всех точек.
 */
private const val DEFAULT_ANIMATION_DURATION_MS = 1000

/**
 * Стандартная продолжительность анимации появления всех точек.
 */
private const val DEFAULT_ANIMATION_CHANGE_SIZE_DURATION_MS = 300

/**
 * Стандартный размер точек в px.
 */
private const val DEFAULT_DOTS_SIZE_PX = 50
/**
 * Стандартный размер акцентной точки в px.
 */
private const val DEFAULT_DOTS_ACCENT_SIZE_PX = 60