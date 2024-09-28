package ru.tensor.sbis.design.audio_player_view.view.player.children

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Drawable реализация компонента для отображения осциллограммы в аудио сообщении.
 *
 * @author rv.krohalev
 */
class WaveformDrawable(private val parentView: View) {

    private var thumbX = 0
    private var thumbDX = 0
    private var startX = 0f
    private var startY = 0f
    private var startDragging = false
    private var width = 0
    private var height = 0
    private var delegate: SeekBarDelegate? = null
    private var waveformBytes: ByteArray? = null
    private var isActive = false
    private var isPaused = false
    private var isClickable = true
    private var seekBarColor = 0
    private var pendingProgress = 0f

    /** @SelfDocumented */
    var isDragging = false
        private set

    var isAvailable: (() -> Boolean)? = null

    init {
        initPaints(parentView)
    }

    /**
     * Установить делагат обработки перемотки аудио.
     */
    fun setDelegate(seekBarDelegate: SeekBarDelegate?) {
        delegate = seekBarDelegate
    }

    /**
     * Установить цвета осциллограммы.
     */
    fun setColors(outer: Int) {
        seekBarColor = outer
    }

    /**
     * Установить осциллограмму.
     */
    fun setWaveform(waveform: ByteArray?) {
        waveformBytes = waveform
    }

    /**
     * Установить состояние активности.
     */
    fun setActive(value: Boolean) {
        isActive = value
    }

    /**
     * Установить состояние кликабильность.
     */
    fun setClickable(value: Boolean) {
        isClickable = value
    }

    /**
     * Установить состояние паузы.
     */
    fun setPaused(value: Boolean) {
        isPaused = value
    }

    /** @SelfDocumented */
    fun onTouch(action: Int, x: Float, y: Float): Boolean =
        when {
            !isClickable || isAvailable?.invoke() == false -> false

            action == MotionEvent.ACTION_DOWN -> {
                if (x in 0f..width.toFloat() && y in 0f..height.toFloat()) {
                    startX = x
                    startY = y
                    isDragging = true
                    thumbDX = x.toInt()
                    startDragging = false
                    true
                } else {
                    false
                }
            }

            !isDragging -> false

            action == MotionEvent.ACTION_MOVE -> {
                if (startDragging) {
                    val startPoint = thumbDX
                    val delta = x.toInt() - startPoint
                    thumbX = maxOf(minOf(startPoint + delta, width), 0)
                    delegate?.onSeekBarDragging(progress)
                }
                if (startX != -1f && abs(x - startX) > abs(y - startY)) {
                    parentView.parent?.requestDisallowInterceptTouchEvent(true)
                    startDragging = true
                    setActive(true)
                    startX = -1f
                    startY = -1f
                }
                true
            }

            action == MotionEvent.ACTION_UP -> {
                delegate?.onSeekBarDragged(progress)
                isDragging = false
                true
            }

            action == MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                true
            }

            else -> false
        }

    /**
     * Прогресс воспроизведения.
     */
    var progress: Float
        get() = if (width > 0) thumbX / width.toFloat() else 0f
        set(progress) {
            if (width == 0) {
                pendingProgress = progress
            }
            if (!isDragging) {
                thumbX = maxOf(minOf((width * progress).roundToInt(), width), 0)
            }
        }

    /**
     * Установить размер.
     */
    fun setSize(w: Int, h: Int) {
        width = w
        height = h
        if (width > 0 && pendingProgress > 0) {
            progress = pendingProgress
            pendingProgress = 0f
        }
    }

    /** @SelfDocumented */
    fun draw(canvas: Canvas) {
        val waveformBytes = this.waveformBytes
        if (width == 0 || height == 0 || waveformBytes == null) {
            return
        }
        val totalBarsCount = getBarsCount(width)
        if (totalBarsCount == 0 || waveformBytes.size != totalBarsCount) {
            return
        }
        paintInner.color = seekBarColor
        paintInner.alpha = if (isActive) (255 * 0.4).toInt() else 255
        paintOuter.color = seekBarColor
        val verticalBordersWidth = parentView.dp(2 * VERTICAL_PADDING_DP)
        val y = (height - verticalBordersWidth) / 2
        for (index in 0 until totalBarsCount) {
            val value = min(waveformBytes[index].toInt(), MAX_WAVEFORM_VALUE)
            val x = index * (parentView.dp(BAR_WIDTH_DP) + parentView.dp(BAR_INTER_SPACE_DP))
            val h = value * y / MAX_WAVEFORM_VALUE
            if (x < thumbX && x + parentView.dp(BAR_WIDTH_DP) < thumbX) {
                drawLine(canvas, x.toFloat(), y, h.toFloat(), paintOuter)
            } else {
                drawLine(canvas, x.toFloat(), y, h.toFloat(), paintInner)
                if (x < thumbX) {
                    if (isPaused) {
                        drawLine(canvas, x.toFloat(), y, h.toFloat(), paintOuter)
                    } else {
                        canvas.save()
                        canvas.clipRect(
                            x - parentView.dp(BAR_WIDTH_DP / 2).toFloat(),
                            y.toFloat(),
                            thumbX.toFloat(),
                            (y + parentView.dp(2 * VERTICAL_PADDING_DP)).toFloat()
                        )
                        drawLine(canvas, x.toFloat(), y, h.toFloat(), paintOuter)
                        canvas.restore()
                    }
                }
            }
        }
    }

    private fun drawLine(canvas: Canvas, x: Float, y: Int, h: Float, paint: Paint) {
        if (h == 0f) {
            canvas.drawPoint(
                x + parentView.dp(BAR_WIDTH_DP / 2),
                (y + parentView.dp(VERTICAL_PADDING_DP)).toFloat(),
                paint
            )
        } else {
            canvas.drawLine(
                x + parentView.dp(BAR_WIDTH_DP / 2),
                y + parentView.dp(VERTICAL_PADDING_DP) - h,
                x + parentView.dp(BAR_WIDTH_DP / 2),
                y + parentView.dp(VERTICAL_PADDING_DP) + h,
                paint
            )
        }
    }

    /** @SelfDocumented */
    fun getBarsCount(width: Int): Int {
        return width / (parentView.dp(BAR_WIDTH_DP) + parentView.dp(BAR_INTER_SPACE_DP))
    }

    companion object {
        private lateinit var paintInner: Paint
        private lateinit var paintOuter: Paint

        private fun initPaints(view: View) {
            if (!this::paintInner.isInitialized) {
                paintInner = Paint(Paint.ANTI_ALIAS_FLAG)
                paintOuter = Paint(Paint.ANTI_ALIAS_FLAG)
                paintInner.style = Paint.Style.STROKE
                paintOuter.style = Paint.Style.STROKE
                paintInner.strokeWidth = view.dp(BAR_WIDTH_DP).toFloat()
                paintOuter.strokeWidth = view.dp(BAR_WIDTH_DP).toFloat()
                paintInner.strokeCap = Paint.Cap.ROUND
                paintOuter.strokeCap = Paint.Cap.ROUND
            }
        }
    }
}

// Ширина одного бара в осциллограмме
private const val BAR_WIDTH_DP = 2f
// Отступ между барами
private const val BAR_INTER_SPACE_DP = 2f
// Вертикальный отступ - сверху и снизу
private const val VERTICAL_PADDING_DP = 8f
// Максимальное значение амплитуды в осциллограмме. Осциллограмма приходит с облака в виде 6 битных значений, максимум = 63
private const val MAX_WAVEFORM_VALUE = 63