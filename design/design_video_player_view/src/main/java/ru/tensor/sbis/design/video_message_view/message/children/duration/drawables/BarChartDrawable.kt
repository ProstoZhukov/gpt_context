package ru.tensor.sbis.design.video_message_view.message.children.duration.drawables

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Px
import java.util.Random

/**
 * Drawable для отображения анимируемой гистограммы.
 * @see GraphParams
 *
 * @author da.zhukov
 */
internal class BarChartDrawable : Drawable() {

    /**
     * Параметры анимируемой гистограммы.
     *
     * @property durationMs общее время продолжительности анимации.
     * @property count количество стобцов.
     * @property width ширина столбца в px.
     * @property maxHeight максимальная высота столбца в px.
     * @property spacing расстояние между стобцами в px.
     */
    data class GraphParams(
        val durationMs: Int = DEFAULT_ANIMATION_DURATION_MS,
        val count: Int = DEFAULT_GRAPHS_COUNT,
        @Px val width: Int = DEFAULT_GRAPHS_WIDTH_PX,
        @Px val maxHeight: Int = DEFAULT_GRAPHS_HEIGHT_PX,
        @Px val spacing: Int = width
    )

    private var isRunning: Boolean = false

    /**
     * Установить/получить параметры с настройками анимируемых точек.
     * @see GraphParams
     */
    var params = GraphParams()
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                invalidateSelf()
            }
        }

    @get:ColorInt
    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            paint.color = value
        }

    /**
     * Краска, которой рисуются отображаемые столбцы.
     */
    private val paint = Paint().apply {
        isAntiAlias = true
        color = textColor
        strokeCap = Paint.Cap.BUTT
    }

    private val random = Random()
    private fun IntRange.random() = random.nextInt((endInclusive + 1) - start) + start
    private val randomNumber = (1..99).random()

    private val accelerateInterpolator = AccelerateInterpolator()
    private val decelerateInterpolator = DecelerateInterpolator()
    private val linearInterpolator = LinearInterpolator()
    private val accelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()

    /**
     * Рандомный набор интерполяторов.
     */
    private val listOfInterpolators = mutableListOf(
        accelerateInterpolator,
        decelerateInterpolator,
        linearInterpolator,
        accelerateDecelerateInterpolator
    ).apply {
        val currentNumber = randomNumber
        if (currentNumber % 2 == 0) reversed()
        when (currentNumber) {
            in 1..30 -> { remove(decelerateInterpolator) }
            in 31..60 -> { remove(linearInterpolator) }
            in 61..90 -> { remove(accelerateInterpolator) }
            else -> { remove(accelerateDecelerateInterpolator) }
        }
    }

    /**
     * Время обновления шага анимации в мс.
     */
    private var stepUpdateTimeMs = 0f

    /**
     * Текущее время анимации в мс.
     */
    private var currentTime = 0f

    /**
     * Нужно ли изменить направление столбцов.
     */
    private var changeDirection = false

    override fun getIntrinsicWidth(): Int =
        with(params) { width * count + spacing * (count - 1) }

    override fun getIntrinsicHeight(): Int =
        params.maxHeight

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        if (isRunning) currentTime += 16.6f
        val interpolation = minOf((currentTime - stepUpdateTimeMs) / (params.durationMs / 2).toFloat(), 1f)

        repeat(params.count) { graphIndex ->
            canvas.drawLine(
                bounds.left.toFloat() + graphIndex * (params.width + params.spacing),
                bounds.bottom.toFloat(),
                bounds.left.toFloat() + graphIndex * (params.width + params.spacing),
                bounds.bottom - (params.maxHeight * getInterpolation(interpolation, changeDirection, graphIndex)),
                paint.apply { strokeWidth = params.width.toFloat() }
            )
        }
        val isNextStep = currentTime - stepUpdateTimeMs >= params.durationMs / 2
        if (isNextStep) {
            stepUpdateTimeMs = currentTime
            changeDirection = !changeDirection
        }
        invalidateSelf()
    }

    fun start() {
        isRunning = true
        invalidateSelf()
    }

    fun stop() {
        isRunning = false
    }

    private fun getInterpolation(interpolation: Float, needChange: Boolean, graphIndex: Int): Float =
        when {
            listOfInterpolators.size < graphIndex && graphIndex % 2 == 0 -> {
                if (needChange) 1f - listOfInterpolators.first().getInterpolation(interpolation)
                else linearInterpolator.getInterpolation(interpolation)
            }
            listOfInterpolators.size < graphIndex && graphIndex % 2 != 0 -> {
                if (needChange) 1f - listOfInterpolators.last().getInterpolation(interpolation)
                else linearInterpolator.getInterpolation(interpolation)
            }
            else -> {
                when {
                    graphIndex % 2 == 0 && needChange -> linearInterpolator.getInterpolation(interpolation)
                    graphIndex % 2 == 0 && !needChange -> 1f - listOfInterpolators[graphIndex].getInterpolation(
                        interpolation
                    )
                    else -> if (needChange) 1f - listOfInterpolators[graphIndex].getInterpolation(interpolation)
                    else linearInterpolator.getInterpolation(interpolation)
                }
            }
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
 * Стандартное количество анимируемых столбцов.
 */
private const val DEFAULT_GRAPHS_COUNT = 3

/**
 * Стандартная продолжительность анимации.
 */
private const val DEFAULT_ANIMATION_DURATION_MS = 1000

/**
 * Стандартная высота столбцов в px.
 */
private const val DEFAULT_GRAPHS_HEIGHT_PX = 40

/**
 * Стандартная ширина столбцов в px.
 */
private const val DEFAULT_GRAPHS_WIDTH_PX = 10