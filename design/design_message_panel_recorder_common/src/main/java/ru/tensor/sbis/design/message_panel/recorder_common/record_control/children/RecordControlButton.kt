package ru.tensor.sbis.design.message_panel.recorder_common.record_control.children

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.KEYBOARD_TAP
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.FloatRange
import androidx.core.graphics.drawable.updateBounds
import androidx.core.graphics.withScale
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.custom_view_tools.utils.animation.ColorAnimationUtils
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordControlButton.SecondaryIconState.*
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Кнопка записи аудио/видео сообщений с амплитудой громкости микрофона.
 *
 * @author vv.chekurda
 */
internal class RecordControlButton(context: Context) : View(context) {

    /**
     * Состояние второстепенной иконки, к которой будет происходить анимация перехода.
     */
    private enum class SecondaryIconState {
        /** Скрыта. */
        HIDDEN,
        /** Отправка аудио. */
        SEND,
        /** Отмена записи. */
        CANCEL
    }

    private val dimens = RecordButtonDimens.create(resources)
    private val defaultBackgroundColor = StyleColor.INFO.getContrastBackgroundColor(context)
    private val cancelBackgroundColor = StyleColor.DANGER.getContrastBackgroundColor(context)
    private val iconColor = defaultBackgroundColor
    private val activatedIconColor = IconColor.CONTRAST.getValue(context)

    private var isLockHapticPerformed = false

    private val primaryIconLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = FontSize.X2L.getScaleOffDimenPx(context).toFloat()
            color = iconColor
        }
        alignment = Layout.Alignment.ALIGN_CENTER
        layoutWidth = dimens.amplitudeMaxSize
        includeFontPad = false
    }
    private val secondaryIconLayout = primaryIconLayout.copy {
        paint.color = activatedIconColor
    }.apply { alpha = 0f }

    private val buttonBackgroundPaint = SimplePaint {
        color = defaultBackgroundColor
        style = Paint.Style.FILL
    }

    private var secondaryIconState: SecondaryIconState = HIDDEN
        private set(value) {
            val isChanged = value != field
            field = value
            if (!isChanged) return
            secondaryIconLayout.buildLayout {
                text = when (value) {
                    SEND -> SbisMobileIcon.Icon.smi_BtArrow
                    CANCEL -> SbisMobileIcon.Icon.smi_navBarClose
                    else -> null
                }?.character?.toString().orEmpty()
            }
            invalidate()
        }

    private val recordIcon: String
        get() = if (isAudioRecord) {
            SbisMobileIcon.Icon.smi_MicrophoneNull
        } else {
            SbisMobileIcon.Icon.smi_CameraVideoMessage
        }.character.toString()

    private var center = 0f to 0f

    private val amplitudeDrawable = AmplitudeDrawable(
        SimplePaint {
            color = defaultBackgroundColor
            style = Paint.Style.FILL
            alpha = AMPLITUDE_PAINT_ALPHA
        }
    ).apply {
        callback = this@RecordControlButton
        minRadius = dimens.amplitudeMinSize / 2f
        maxRadius = dimens.amplitudeMaxSize.toFloat() / 2f
        pulsationRadiusDx = dimens.amplitudePulseRadiusDx.toFloat()
    }

    private var backgroundScale: Float = 0f
    private val scaleOnInterpolator = DecelerateInterpolator()
    private val scaleOffInterpolator = AccelerateInterpolator()

    /**
     * Признак записи аудио или видео сообщения.
     */
    var isAudioRecord: Boolean = true
        set(value) {
            field = value
            val isChanged = primaryIconLayout.buildLayout { text = recordIcon }
            if (isChanged) invalidate()
        }

    /**
     * Признак закрепления управления записью.
     */
    var isLocked: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                isClickable = value
                if (value) setSendState()
            }
        }

    /**
     * Признак активации отмены записи на кнопке.
     */
    var isCancelModeActivated: Boolean = false
        private set

    /**
     * Амплитуда громкости микрофона.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    var amplitude: Float
        get() = amplitudeDrawable.amplitude
        set(value) {
            amplitudeDrawable.amplitude = value
        }

    /**
     * Доля анимации появления кнопки записи.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    var showingFraction: Float = 0f
        set(value) {
            val fraction = minOf(value, 1f)
            if (field == fraction) return
            val oldFraction = field
            field = fraction
            if (oldFraction == 0f || fraction == 0f) {
                primaryIconLayout.configure {
                    paint.color = if (field == 0f) iconColor else activatedIconColor
                }
            }
            val scaleOnEndPoint = 2 / 3f
            val scaleDiff = MAX_BUTTON_SCALE - 1f
            backgroundScale = if (field <= scaleOnEndPoint) {
                val scaleOnFraction = field / scaleOnEndPoint
                val interpolation = scaleOnInterpolator.getInterpolation(scaleOnFraction)
                MAX_BUTTON_SCALE * interpolation
            } else {
                val scaleOfFraction = (field - scaleOnEndPoint) / (1f - scaleOnEndPoint)
                val interpolation = scaleOffInterpolator.getInterpolation(scaleOfFraction)
                MAX_BUTTON_SCALE - scaleDiff * interpolation
            }
            invalidate()
        }

    init {
        primaryIconLayout.configure { text = recordIcon }
        isHapticFeedbackEnabled = false
    }

    /**
     * Остановить амплитуду.
     */
    fun stopAmplitude() {
        amplitudeDrawable.stop()
    }

    /**
     * Очистить состояние кнопки записи.
     */
    fun clear() {
        showingFraction = 0f
        amplitude = 0f
        isCancelModeActivated = false
        isLockHapticPerformed = false
        secondaryIconState = HIDDEN
        primaryIconLayout.alpha = 1f
        amplitudeDrawable.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val iconTop = paddingTop + ((dimens.amplitudeMaxSize - primaryIconLayout.height) / 2f).roundToInt()
        primaryIconLayout.layout(paddingStart, iconTop)
        secondaryIconLayout.layout(paddingStart, iconTop)
    }

    override fun onDraw(canvas: Canvas) {
        if (backgroundScale == 0f) return
        drawAmplitude(canvas)
        drawButtonBackground(canvas)
        primaryIconLayout.draw(canvas)
        secondaryIconLayout.draw(canvas)
    }

    private fun drawAmplitude(canvas: Canvas) {
        canvas.withScale(backgroundScale, backgroundScale, center.first, center.second) {
            amplitudeDrawable.draw(canvas)
        }
    }

    private fun drawButtonBackground(canvas: Canvas) {
        canvas.drawCircle(
            center.first,
            center.second,
            backgroundScale * dimens.buttonRadius + amplitudeDrawable.animatedAmplitude * dimens.maxButtonRadiusDelta,
            buttonBackgroundPaint
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + paddingEnd + dimens.amplitudeMaxSize

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + paddingBottom + dimens.amplitudeMaxSize

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val availableWidth = w - paddingStart - paddingEnd
        val availableHeight = h - paddingTop - paddingBottom
        val centerX = availableWidth / 2f
        val centerY = availableHeight / 2f
        center = paddingStart + centerX to paddingTop + centerY
        amplitudeDrawable.updateBounds(
            left = paddingStart,
            top = paddingTop,
            right = measuredWidth - paddingEnd,
            bottom = measuredHeight - paddingBottom
        )
    }

    override fun setTranslationX(translationX: Float) {
        val isChanged = this.translationX != translationX
        super.setTranslationX(translationX)
        if (!isLocked && isChanged) {
            updateViewOnCancelling()
        }
    }

    override fun setTranslationY(translationY: Float) {
        val isChanged = this.translationY != translationY
        super.setTranslationY(translationY)
        if (!isLocked && isChanged) {
            updateViewOnLocking()
        }
    }

    private fun updateViewOnCancelling() {
        if (translationY == 0f) {
            secondaryIconState = if (translationX >= -dimens.cancelIgnoreDx) HIDDEN else CANCEL
        }
        val dx = translationX + dimens.cancelIgnoreDx
        val fraction = minOf(-dx / dimens.cancelAnimatedDistance, 1f).coerceAtLeast(0f)
        updateIconOnCancelling(fraction)
        updateBackgroundOnCancelling(fraction)
        checkCancelHapticFeedback()
    }
    
    private fun updateIconOnCancelling(@FloatRange(from = 0.0, to = 1.0) fraction: Float) {
        primaryIconLayout.alpha = 1f - fraction
        secondaryIconLayout.alpha = fraction
        invalidate()
    }

    private fun updateBackgroundOnCancelling(@FloatRange(from = 0.0, to = 1.0) fraction: Float) {
        val backgroundColor = ColorAnimationUtils.getAnimatedColor(
            defaultBackgroundColor,
            cancelBackgroundColor,
            fraction
        )
        buttonBackgroundPaint.color = backgroundColor
        amplitudeDrawable.setColor(backgroundColor, AMPLITUDE_PAINT_ALPHA)
        invalidate()
    }

    private fun updateViewOnLocking() {
        if (translationX >= -dimens.cancelIgnoreDx) {
            secondaryIconState = if (translationY == 0f) HIDDEN else SEND
        }
        updateIconOnLocking()
        checkLockHapticFeedback()
    }

    private fun updateIconOnLocking() {
        val fraction = minOf(-translationY / dimens.lockRecordAnimatedDistance, 1f)
        primaryIconLayout.alpha = 1f - fraction
        secondaryIconLayout.alpha = fraction
        invalidate()
    }

    private fun setSendState() {
        secondaryIconState = SEND
        primaryIconLayout.alpha = 0f
        secondaryIconLayout.alpha = 1f
        invalidate()
    }

    private fun checkCancelHapticFeedback() {
        if (!isCancelModeActivated && translationX <= -dimens.cancelDistance) {
            isCancelModeActivated = true
            performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)
        } else if (isCancelModeActivated && translationX > -dimens.cancelDistance) {
            isCancelModeActivated = false
        }
    }

    private fun checkLockHapticFeedback() {
        if (!isLockHapticPerformed && translationY <= -dimens.lockRecordDistance) {
            isLockHapticPerformed = true
            performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)
        } else if (isLockHapticPerformed && translationY > -dimens.lockRecordDistance) {
            isLockHapticPerformed = false
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        who == amplitudeDrawable || super.verifyDrawable(who)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (event.action == MotionEvent.ACTION_DOWN && !isInAmplitudeRect(event)) {
            false
        } else {
            super.onTouchEvent(event)
        }

    private fun isInAmplitudeRect(event: MotionEvent): Boolean {
        val amplitudeRect = amplitudeDrawable.bounds
        val x = event.x - amplitudeRect.centerX()
        val y = event.y - amplitudeRect.centerY()
        return x.pow(2) + y.pow(2) <= ((amplitudeRect.width() / 2f)).pow(2)
    }

    /**
     * Размеры внутренней разметки кнопки записи [RecordControlButton].
     */
    private data class RecordButtonDimens(
        val buttonSize: Int,
        val buttonRadius: Float,
        val maxButtonRadiusDelta: Float,
        val amplitudeMinSize: Int,
        val amplitudeMaxSize: Int,
        val availableAmplitudeRadiusDx: Float,
        val amplitudePulseRadiusDx: Int,
        val cancelDistance: Int,
        val cancelAnimatedDistance: Int,
        val cancelIgnoreDx: Float,
        val lockRecordDistance: Int,
        val lockRecordAnimatedDistance: Int
    ) {
        companion object {
            fun create(resources: Resources) = with(resources) {
                val buttonSize = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_size)
                val buttonRadius = buttonSize / 2f
                val amplitudeMinSize = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_volume_indicator_min_size)
                val amplitudeMaxSize = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_volume_indicator_max_size)
                RecordButtonDimens(
                    buttonSize = buttonSize,
                    buttonRadius = buttonRadius,
                    maxButtonRadiusDelta = buttonRadius * BACKGROUND_AMPLITUDE_SCALE,
                    amplitudeMinSize = amplitudeMinSize,
                    amplitudeMaxSize = amplitudeMaxSize,
                    availableAmplitudeRadiusDx = (amplitudeMaxSize - amplitudeMinSize) / 2f,
                    amplitudePulseRadiusDx = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_volume_pulse_radius_dx),
                    cancelDistance = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_cancel_distance),
                    cancelAnimatedDistance = resources.getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_cancel_animated_distance),
                    cancelIgnoreDx = getDimension(R.dimen.design_message_panel_recorder_common_record_button_horizontal_movement_distance) * IGNORE_HORIZONTAL_MOVEMENT_DISTANCE_PERCENT,
                    lockRecordDistance = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_lock_record_distance),
                    lockRecordAnimatedDistance = getDimensionPixelSize(R.dimen.design_message_panel_recorder_common_record_button_lock_record_animated_distance),
                )
            }
        }
    }
}

/**
 * Процент прозрачности амплитуды громкости микрофона.
 */
private const val AMPLITUDE_ALPHA_PERCENT = 0.4
/**
 * Прозрачность краски амплитуды громкости микрофона.
 */
private const val AMPLITUDE_PAINT_ALPHA = (PAINT_MAX_ALPHA * AMPLITUDE_ALPHA_PERCENT).toInt()
/**
 * Максимальный масштаб кнопки во время анимации.
 */
private const val MAX_BUTTON_SCALE = 1.1f

private const val BACKGROUND_AMPLITUDE_SCALE = 0.10f