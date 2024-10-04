package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Layout
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.drawable.updateBounds
import androidx.core.graphics.withScale
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.AmplitudeDrawable
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import kotlin.math.roundToInt

/**
 * Кнопка остановки записи закрепленного аудиосообщения с амплитудой громкости микрофона.
 *
 * @author vv.chekurda
 */
internal class RecordStopButton(context: Context) : View(context) {

    private val buttonSize = InlineHeight.X2S.getDimenPx(context)
    private val amplitudeMaxSize = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_stop_max_amplitude_diameter)
    private val amplitudeMinSize = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_stop_min_amplitude_diameter)
    private val pulsationDx = resources.getDimension(R.dimen.design_message_panel_audio_recorder_stop_pulsation_dx)
    private val maxButtonRadiusDelta = BACKGROUND_AMPLITUDE_SCALE * buttonSize / 2f
    @ColorInt
    private val iconColor: Int = IconColor.CONTRAST.getValue(context)
    @ColorInt
    private val buttonBackgroundColor: Int = StyleColor.INFO.getContrastBackgroundColor(context)

    private val backgroundPaint = SimplePaint {
        style = Paint.Style.FILL
        color = buttonBackgroundColor
    }
    private val backgroundRadius = buttonSize / 2f
    private var center = 0f to 0f

    private val amplitudeDrawable = AmplitudeDrawable(
        SimplePaint {
            color = buttonBackgroundColor
            style = Paint.Style.FILL
            alpha = AMPLITUDE_PAINT_ALPHA
        }
    ).apply {
        callback = this@RecordStopButton
        minRadius = amplitudeMinSize / 2f
        maxRadius = amplitudeMaxSize / 2f
        pulsationRadiusDx = pulsationDx
    }

    private val iconLayout = TextLayout {
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = IconSize.XL.getDimenPx(context).toFloat()
            color = iconColor
        }
        text = SbisMobileIcon.Icon.smi_stopAudio.character.toString()
        alignment = Layout.Alignment.ALIGN_CENTER
        includeFontPad = false
    }.apply {
        makeClickable(this@RecordStopButton)
    }
    private val clickableIconRect = Rect()

    private var backgroundScale: Float = 0f
    private val scaleInterpolator = DecelerateInterpolator()

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
            val fraction = value.coerceAtMost(1f).coerceAtLeast(0f)
            if (field == fraction) return
            field = fraction
            backgroundScale = scaleInterpolator.getInterpolation(fraction)
            invalidate()
        }

    /**
     * Очистить состояние кнопки записи.
     */
    fun clear() {
        showingFraction = 0f
        amplitude = 0f
        iconLayout.alpha = 1f
        amplitudeDrawable.clear()
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        isClickable = listener != null
        iconLayout.setOnClickListener { _, _ -> listener?.onClick(this) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + paddingEnd + amplitudeMaxSize

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + paddingBottom + amplitudeMaxSize

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
        clickableIconRect.set(
            (center.first - backgroundRadius).toInt(),
            (center.second - backgroundRadius).toInt(),
            (center.first + backgroundRadius).toInt(),
            (center.second + backgroundRadius).toInt()
        )
        iconLayout.setStaticTouchRect(clickableIconRect)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        iconLayout.layout(
            paddingStart + ((measuredWidth - horizontalPadding - iconLayout.width) / 2f).roundToInt(),
            paddingTop + ((measuredHeight - verticalPadding - iconLayout.height) / 2f).roundToInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (backgroundScale == 0f) return
        drawAmplitude(canvas)
        drawButtonBackground(canvas)
        iconLayout.draw(canvas)
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
            backgroundScale * backgroundRadius + amplitudeDrawable.animatedAmplitude * maxButtonRadiusDelta,
            backgroundPaint
        )
    }

    override fun verifyDrawable(who: Drawable): Boolean =
        when (who) {
            amplitudeDrawable -> true
            else -> super.verifyDrawable(who)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        iconLayout.onTouch(this, event) || super.onTouchEvent(event)
}

/**
 * Процент прозрачности амплитуды громкости микрофона.
 */
private const val AMPLITUDE_ALPHA_PERCENT = 0.4
/**
 * Прозрачность краски амплитуды громкости микрофона.
 */
private const val AMPLITUDE_PAINT_ALPHA = (PAINT_MAX_ALPHA * AMPLITUDE_ALPHA_PERCENT).toInt()

private const val BACKGROUND_AMPLITUDE_SCALE = 0.10f