package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.children

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.withSave
import ru.tensor.sbis.design.message_panel.video_recorder.R
import ru.tensor.sbis.design.R as RDesign

/**
 * View для отобаржения полоски прогресса по окружности.
 * Конечная точка прогресса задается временным интервалом в мс при запуске анимации [start].
 *
 * @author vv.chekurda
 */
internal class RoundProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, RDesign.color.palette_color_black5)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.design_message_panel_video_recorder_progress_stroke_width).toFloat()
    }

    private var progress = 0f
    private var startTime = 0L
    private var fullDurationMs = 0L
    private var isRunning = false
    private var rectF = RectF()

    /**
     * Цвет полоски прогресса.
     */
    @get:ColorInt
    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
            if (isRunning) invalidate()
        }

    /**
     * Ширина полоски прогресса.
     */
    var strokeWidth: Float
        get() = paint.strokeWidth
        set(value) {
            paint.strokeWidth = value
            if (isRunning) invalidate()
        }

    var onProgressFinishedListener: ((Long) -> Unit)? = null

    /**
     * Начать изменение прогрреса записи.
     */
    fun start(durationMs: Long) {
        progress = 0f
        startTime = System.currentTimeMillis()
        fullDurationMs = durationMs
        isRunning = true
        invalidate()
    }

    /**
     * Остановить изменение прогрреса.
     */
    fun stop(withClear: Boolean = true) {
        isRunning = false
        if (withClear) {
            clear()
        } else {
            isRunning = false
        }
        invalidate()
    }

    /**
     * Получить продолжительность.
     */
    fun getDuration(): Long {
        return (progress * fullDurationMs).toLong()
    }

    private fun clear() {
        progress = 0f
        startTime = 0L
        fullDurationMs = 0L
        isRunning = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rectF.set(
            paddingStart.toFloat(),
            paddingTop.toFloat(),
            w - paddingEnd.toFloat(),
            h - paddingBottom.toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        updateProgress()
        if (progress != 0f) {
            canvas.withSave {
                drawArc(
                    rectF,
                    PROGRESS_START_ANGLE,
                    CIRCLE_DEGREES * progress,
                    false,
                    paint
                )
            }
        }
    }

    private fun updateProgress() {
        if (!isRunning) return

        val progressTime = System.currentTimeMillis() - startTime
        val oldProgress = progress
        progress = minOf(1f, progressTime / fullDurationMs.toFloat())

        if (oldProgress != progress) {
            invalidate()
            if (progress == 1f) {
                onProgressFinishedListener?.invoke(fullDurationMs)
            }
        }
    }
}

private const val PROGRESS_START_ANGLE = -90f
private const val CIRCLE_DEGREES = 360f