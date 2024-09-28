package ru.tensor.sbis.design.video_message_view.player.children

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isInvisible
import androidx.media3.common.C
import androidx.media3.common.util.Assertions
import androidx.media3.ui.TimeBar
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import ru.tensor.sbis.design.R as RDesign

/**
 * Круглый тайм бар.
 *
 * @author da.zhukov
 */
internal class RoundTimeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build()),
    TimeBar {

    companion object {
        /**
         * сохранить состояние
         */
        private const val STATE_PARENT = "parent"
        private const val STATE_ANGLE = "angle"
        private const val DEFAULT_INCREMENT_COUNT = 20
    }

    /**
     * Перемотка разрешена или нет
     */
    var seekEnabled: Boolean = true

    /**
     * Ширина кольца
     */
    private val arcStrokeWidth = context.getDimen(RDesign.attr.borderThickness_3xl)

    private var arcPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getThemeColorInt(RDesign.attr.borderColor)
            style = Paint.Style.STROKE
            strokeWidth = arcStrokeWidth
            strokeCap = Paint.Cap.ROUND
        }

    private var pointerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = dp(5).toFloat()
        }

    /**
     * Прямоугольник, охватывающий прогресс.
     */
    private val progressRect = RectF()

    /**
     * Центр тайм бара.
     */
    private var center = 0f

    /**
     * Состояние проигрывания.
     */
    private var isPlaying = false

    /**
     * Состояние воспроизведения.
     */
    private var state: State = State.DEFAULT

    /**
     * Радиус кольца (повторно вычислить в onMeasure[.onMeasure])
     */
    private var progressRadius = 0f

    /**
     * Координаты точки на тайм баре.
     */
    private lateinit var progressPointerPosition: Pair<Float, Float>

    /**
     * Угол с которого начинает движение тайм бар.
     */
    private var startArc = 270

    /**
     * Угол в котором заканчивает движение тайм бар.
     */
    private var endWheel = 270f

    /**
     * Радианы для расчета последней точки тайм бара.
     */
    private var arcFinishRadians = endWheel

    /**
     * Угол для расчета последней точки тайм бара.
     */
    private var angle = calculateAngleFromRadians(endWheel)

    private val listeners = CopyOnWriteArraySet<TimeBar.OnScrubListener>()

    private var keyCountIncrement = DEFAULT_INCREMENT_COUNT
    private var keyTimeIncrement: Long = C.TIME_UNSET

    /**
     * Происходит ли сейчас перемотка.
     */
    private var scrubbing = false

    /**
     * Позиция хвоста таймбара во премя перемотки.
     */
    private var scrubPosition: Long = 0

    /**
     * Длительность видео в миллисекундаъ.
     */
    private var duration: Long = 0

    /**
     * Текущая позиция видео в миллисекундах.
     */
    private var position: Long = 0
    private val stopScrubbingRunnable: Runnable = Runnable {
        stopScrubbing( /* canceled= */false)
    }

    /**
     * Анимированная дельта радиуса.
     */
    private var animatedRadiusDelta = 0f

    /**
     * Дельта радиуса при паузе.
     */
    private val pauseRadiusDelta = -dp(12).toFloat()

    /**
     * Аниматор для изменения размера.
     */
    private var stateAnimator: ValueAnimator? = null

    init {
        isInvisible = true
    }

    override fun setDuration(duration: Long) {
        this.duration = duration
        if (scrubbing && duration == C.TIME_UNSET) {
            stopScrubbing(true)
        }
        update()
    }

    override fun setPosition(position: Long) {
        this.position = position
        update()
    }

    /**
     * Изменить состояние.
     */
    fun changeState(state: State, animated: Boolean = true) {
        val isChanged = this.state != state
        if (!isChanged) return
        this.state = state
        when (state) {
            State.DEFAULT -> {
                isPlaying = false
                isInvisible = true
            }
            State.PLAYING -> {
                isPlaying = true
                isInvisible = false
                if (animatedRadiusDelta != 0f) {
                    changeRadius(animated)
                }
            }
            State.PAUSED -> {
                isPlaying = false
                isInvisible = false
                if (animatedRadiusDelta != pauseRadiusDelta) {
                    changeRadius(animated)
                }
            }
            else -> Unit
        }
    }

    /**
     * Анимировано изменить радиус тайм бара.
     */
    private fun changeRadius(animated: Boolean) {
        stateAnimator?.cancel()
        if (animated) {
            stateAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 250
                if (isPlaying) {
                    val startPoint = animatedRadiusDelta
                    addUpdateListener {
                        animatedRadiusDelta = startPoint * (1f - it.animatedFraction)
                        updateProgressRect()
                        invalidate()
                    }
                } else {
                    if (animatedRadiusDelta == pauseRadiusDelta) return@apply
                    addUpdateListener {
                        animatedRadiusDelta = pauseRadiusDelta * it.animatedFraction
                        updateProgressRect()
                        invalidate()
                    }
                }
                start()
            }
        } else {
            animatedRadiusDelta = if (isPlaying) 0f else pauseRadiusDelta
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val min = minOf(width, height)
        setMeasuredDimension(min, min)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateProgressRect()
    }

    /**
     * Обновить прогресс тайм бара.
     */
    private fun updateProgressRect() {
        val halfWidth = measuredWidth / 2f
        center = halfWidth
        progressRadius = halfWidth - dp(8).toFloat() + animatedRadiusDelta
        progressRect.set(
            center - progressRadius,
            center - progressRadius,
            center + progressRadius,
            center + progressRadius
        )
        progressPointerPosition = calculatePointerPosition(angle)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(
            progressRect,
            startArc.toFloat(),
            arcFinishRadians,
            false,
            arcPaint
        )
        if (!isPlaying && seekEnabled) {
            canvas.drawCircle(
                progressPointerPosition.first,
                progressPointerPosition.second,
                dp(5).toFloat(),
                pointerPaint
            )
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        val state = Bundle()
        state.putParcelable(STATE_PARENT, superState)
        state.putFloat(STATE_ANGLE, angle)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle
        val superState = savedState.getParcelable<Parcelable>(STATE_PARENT)
        super.onRestoreInstanceState(superState)
        angle = savedState.getFloat(STATE_ANGLE)
        arcFinishRadians = calculateRadiansFromAngle(angle)
        progressPointerPosition = calculatePointerPosition(angle)
    }

    /**
     * Обновить тайм бар.
     */
    private fun update() {
        arcFinishRadians = calculateRadiansFromPosition(position)
        angle = calculateAngleFromRadians(arcFinishRadians)
        progressPointerPosition = calculatePointerPosition(angle)
        invalidate()
    }

    /**
     * Начало перемотки.
     */
    private fun startScrubbing(scrubPosition: Long) {
        this.scrubPosition = scrubPosition
        scrubbing = true
        isPressed = true
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(true)
        for (listener in listeners) {
            listener.onScrubStart(this, scrubPosition)
        }
    }

    /**
     * Обновить позицию перемотки.
     */
    private fun updateScrubbing(scrubPosition: Long) {
        if (this.scrubPosition == scrubPosition) {
            return
        }
        this.scrubPosition = scrubPosition
        for (listener in listeners) {
            listener.onScrubMove(this, scrubPosition)
        }
    }

    /**
     * Остановить перемотку.
     */
    private fun stopScrubbing(canceled: Boolean) {
        removeCallbacks(stopScrubbingRunnable)
        scrubbing = false
        isPressed = false
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(false)
        invalidate()
        for (listener in listeners) {
            listener.onScrubStop(this, scrubPosition, canceled)
        }
    }

    override fun addListener(listener: TimeBar.OnScrubListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: TimeBar.OnScrubListener) {
        listeners.remove(listener)
    }

    override fun setKeyTimeIncrement(time: Long) {
        Assertions.checkArgument(time > 0)
        keyCountIncrement = C.INDEX_UNSET
        keyTimeIncrement = time
    }

    override fun setKeyCountIncrement(count: Int) {
        Assertions.checkArgument(count > 0)
        keyCountIncrement = count
        keyTimeIncrement = C.TIME_UNSET
    }

    override fun getPreferredUpdateDelay(): Long {
        val timeBarWidthDp = (progressRect.width() * Math.PI).roundToInt()
        return if (timeBarWidthDp == 0 || duration == 0L || duration == C.TIME_UNSET) {
            Long.MAX_VALUE
        } else {
            duration / timeBarWidthDp
        }
    }

    override fun setAdGroupTimesMs(adGroupTimesMs: LongArray?, playedAdGroups: BooleanArray?, adGroupCount: Int) = Unit

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (scrubbing && !enabled) {
            stopScrubbing(true)
        }
    }

    /**
     * Расчитать радины через угол.
     */
    private fun calculateRadiansFromAngle(angle: Float): Float {
        var unit = angle / (2 * Math.PI)
        if (unit < 0) unit += 1L
        var radians = unit * 360 - 360 / 4 * 3
        if (radians < 0) radians += 360
        return radians.toFloat()
    }

    /**
     * Расчитать угол через радианы.
     */
    private fun calculateAngleFromRadians(radians: Float): Float {
        return ((radians + 270) * (2 * Math.PI) / 360).toFloat()
    }

    /**
     * Расчитать координаты точки на тайм баре.
     */
    private fun calculatePointerPosition(angle: Float): Pair<Float, Float> {
        val x = (progressRadius * cos(angle.toDouble())).toFloat() + center
        val y = (progressRadius * sin(angle.toDouble())).toFloat() + center
        return Pair(x, y)
    }

    /**
     * Расчитать радианы чере позицию.
     */
    private fun calculateRadiansFromPosition(position: Long = 0): Float {
        if (position == 0L || position >= duration) return 0f
        val onePercent: Float = duration / 100f
        val progress: Float = position / onePercent
        return (3.6 * progress).toFloat()
    }

    /**
     * Расчитать позицию чере радианы.
     */
    private fun calculatePositionFromRadians(radians: Float): Long {
        val onePercent: Float = duration / 100f
        val progress: Float = radians / 3.6f
        val position = progress * onePercent
        return position.toLong()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || duration <= 0 || isPlaying) {
            return false
        }
        val x = event.x - center
        val y = event.y - center
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInProgressbar(x, y)) return false
                if (seekEnabled) {
                    angle = kotlin.math.atan2(y.toDouble(), x.toDouble()).toFloat()
                    progressPointerPosition = calculatePointerPosition(angle)

                    arcFinishRadians = calculateRadiansFromAngle(angle)
                    if (arcFinishRadians > endWheel) arcFinishRadians = endWheel
                    startScrubbing(calculatePositionFromRadians(arcFinishRadians))
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (scrubbing) {
                    angle = kotlin.math.atan2(y.toDouble(), x.toDouble()).toFloat()
                    progressPointerPosition = calculatePointerPosition(angle)
                    arcFinishRadians = calculateRadiansFromAngle(angle)
                    updateScrubbing(calculatePositionFromRadians(arcFinishRadians))
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (scrubbing) {
                    stopScrubbing(event.action == MotionEvent.ACTION_CANCEL)
                    return true
                }
                isPlaying = true
            }
        }
        return false
    }

    /**
     * Находится ли нажатие в пределах нужного радиуса тайм бара.
     */
    private fun isInProgressbar(x: Float, y: Float): Boolean =
        x.pow(2) + y.pow(2) <= (progressRadius - dp(14)).pow(2)

    override fun setBufferedPosition(bufferedPosition: Long) = Unit
}