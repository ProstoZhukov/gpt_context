package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.record

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordControlViewEvent
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordTimeDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecorderFieldDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordingIndicatorDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.controller.CONTROLS_ANIMATION_DURATION_MS
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.controller.DECOR_BUTTONS_ANIMATION_DURATION_MS
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.utils.getDimenPx
import kotlin.math.roundToInt
import ru.tensor.sbis.design.message_panel.recorder_common.R as RRecorderCommon
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * View для управления закрепленной записью аудиосообщения.
 *
 * @author vv.chekurda
 */
internal class AudioRecordControlView(context: Context) : ViewGroup(context),
    AudioRecordControlViewApi {

    private val dimens = AudioRecordControlDimens.create(context)

    private var animatedIndicatorAlpha: Int = 0

    /**
     * Разметка кнопки отмены записи.
     */
    private val cancelLayout = TextLayout.createTextLayoutByStyle(
        context,
        RRecorderCommon.style.RecorderCancelTextDefaultStyle
    ).apply {
        textPaint.textSize = textPaint.textSize
            .coerceAtMost(dp(MAX_CANCEL_TEXT_SIZE_DP).toFloat())
            .coerceAtLeast(dp(MIN_CANCEL_TEXT_SIZE_DP).toFloat())
        makeClickable(this@AudioRecordControlView)
        alpha = 0f
        setOnClickListener { _, _ ->
            DebounceActionHandler.INSTANCE.handle {
                eventsHandler.invoke(AudioRecordControlViewEvent.OnRecordCanceled)
            }
        }
    }
    private val cancelClickableRect = Rect()

    /**
     * Индикатор записи (красный кружок).
     */
    private val recordingIndicator = RecordingIndicatorDrawable(context).apply {
        alpha = 0
        callback = this@AudioRecordControlView
    }

    /**
     * Таймер продолжительности записи.
     */
    private val timerDrawable = RecordTimeDrawable(context).apply {
        alpha = 0
        callback = this@AudioRecordControlView
    }

    /**
     * Кнопка остановки записи аудиосообщения.
     */
    private val recordStopButton = RecordStopButton(context).apply {
        id = R.id.design_message_panel_audio_record_stop_button
        setOnClickListener {
            DebounceActionHandler.INSTANCE.handle {
                eventsHandler.invoke(AudioRecordControlViewEvent.OnRecordStopped)
            }
        }
    }

    /**
     * Кнопка прикрепления вложений для анимации перехода к записи.
     */
    private val decorAttachButton = TextLayout {
        text = SbisMobileIcon.Icon.smi_attach.character.toString()
        paint.apply {
            typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            textSize = context.getDimenPx(RDesign.attr.iconSize_3xl).toFloat()
            color = ContextCompat.getColor(context, RMPCommon.color.design_message_panel_control_selectable_color)
        }
        val horizontalPadding = context.getDimenPx(RDesign.attr.offset_s)
        padding = TextLayout.TextLayoutPadding(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = context.getDimenPx(RDesign.attr.offset_2xs)
        )
    }

    /**
     * Скругленное поле анимируемого фона.
     */
    private val fieldDrawable = RecorderFieldDrawable(context).apply {
        callback = this@AudioRecordControlView
        collapsedPaddingStart = decorAttachButton.width.toFloat() - dimens.fieldHorizontalMargin
    }

    override var mode = AudioRecordMode.SIMPLE
        set(value) {
            field = value
            fieldDrawable.showShadow = mode != AudioRecordMode.MESSAGE_PANEL
            safeRequestLayout()
        }

    /**
     * Правый отступ для анимированного показа/скрытия.
     */
    var animatedEndSpacing: Int = 0
        set(value) {
            field = value
            fieldDrawable.collapsedPaddingEnd = -value.toFloat()
        }

    /**
     * Обработчик событий.
     */
    lateinit var eventsHandler: ControlEventsHandler

    init {
        setWillNotDraw(false)
        addView(recordStopButton)
    }

    override fun setAmplitude(amplitude: Float) {
        recordStopButton.amplitude = amplitude
    }

    private var controlsAnimator: ValueAnimator? = null
    private var decorAnimator: ValueAnimator? = null
    private var showInterpolator = DecelerateInterpolator()

    override fun show() {
        when (mode) {
            AudioRecordMode.SIMPLE -> showControls()
            AudioRecordMode.MESSAGE_PANEL -> animateShowing()
        }
    }

    override fun hide() {
        when (mode) {
            AudioRecordMode.SIMPLE -> hideControls()
            AudioRecordMode.MESSAGE_PANEL -> animateHiding()
        }
    }

    private fun showControls() {
        clearRecordAnimation()
        decorAttachButton.configure { isVisible = false }
        animateControlsVisibility(fraction = 1f)
    }

    private fun hideControls() {
        clearRecordAnimation()
        recordingIndicator.stop()
        animatedIndicatorAlpha = recordingIndicator.alpha
        eventsHandler.invoke(AudioRecordControlViewEvent.OnHidingEnd)
    }

    private fun animateShowing() {
        clearRecordAnimation()
        val recordControlsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            controlsAnimator = this
            duration = CONTROLS_ANIMATION_DURATION_MS
            addUpdateListener { animateControlsVisibility(it.animatedFraction) }
            doOnEnd {
                decorAttachButton.configure { isVisible = false }
            }
            start()
            pause()
        }
        val decorButtonsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            decorAnimator = this
            duration = DECOR_BUTTONS_ANIMATION_DURATION_MS
            addUpdateListener { animateDecorButtonsVisibility(it.animatedFraction) }
            start()
            pause()
        }
        doOnPreDraw {
            recordControlsAnimator.resume()
            decorButtonsAnimator.resume()
        }
    }

    private fun animateHiding() {
        cancelAllAnimations()
        recordingIndicator.stop()
        animatedIndicatorAlpha = recordingIndicator.alpha
        decorAttachButton.configure { isVisible = true }

        var isDecorStarted = false
        val decorButtonsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            decorAnimator = this
            duration = DECOR_BUTTONS_ANIMATION_DURATION_MS
            addUpdateListener { animateDecorButtonsVisibility(1f - it.animatedFraction) }
        }
        val recordControlsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            controlsAnimator = this
            duration = CONTROLS_ANIMATION_DURATION_MS
            val decorStartFraction = 1f * (CONTROLS_ANIMATION_DURATION_MS - DECOR_BUTTONS_ANIMATION_DURATION_MS) / CONTROLS_ANIMATION_DURATION_MS
            addUpdateListener {
                animateControlsVisibility(1f - it.animatedFraction, isShowing = false)
                if (!isDecorStarted && it.animatedFraction >= decorStartFraction) {
                    isDecorStarted = true
                    decorButtonsAnimator.start()
                }
            }
            doOnEnd { eventsHandler.invoke(AudioRecordControlViewEvent.OnHidingEnd) }
            start()
            pause()
            invalidate()
        }
        doOnPreDraw {
            recordControlsAnimator.resume()
        }
    }

    private fun animateControlsVisibility(fraction: Float, isShowing: Boolean = true) {
        val interpolation = showInterpolator.getInterpolation(fraction)
        val reversedInterpolation = 1f - interpolation
        val controlsPaintAlpha = (interpolation * PAINT_MAX_ALPHA).toInt()
        val controlsTranslation = reversedInterpolation * fieldDrawable.collapsedPaddingStart
        val recordButtonTranslation = reversedInterpolation * - fieldDrawable.collapsedPaddingEnd
        recordStopButton.showingFraction = fraction
        recordStopButton.translationX = recordButtonTranslation
        fieldDrawable.expandFraction = fraction
        recordingIndicator.apply {
            alpha = if (isShowing) controlsPaintAlpha else (animatedIndicatorAlpha * interpolation).roundToInt()
            translationX = controlsTranslation
        }
        timerDrawable.apply {
            alpha = controlsPaintAlpha
            translationX = controlsTranslation
        }
        cancelLayout.apply {
            translationX = controlsTranslation
            alpha = interpolation
        }
        invalidate()
    }

    private fun animateDecorButtonsVisibility(fraction: Float) {
        val hideInterpolation = showInterpolator.getInterpolation(1f - fraction)
        decorAttachButton.alpha = hideInterpolation
        invalidate()
    }

    override fun startRecordAnimation() {
        recordingIndicator.start()
        timerDrawable.start()
    }

    override fun clearRecordAnimation() {
        cancelAllAnimations()
        decorAttachButton.apply {
            configure { isVisible = true }
            alpha = 1f
        }
        recordingIndicator.apply {
            clear()
            translationX = fieldDrawable.collapsedPaddingStart
        }
        timerDrawable.apply {
            clear()
            translationX = fieldDrawable.collapsedPaddingStart
        }
        cancelLayout.alpha = 0f
        recordStopButton.clear()
        recordStopButton.translationX = 0f
        fieldDrawable.clear()
    }

    private fun cancelAllAnimations() {
        controlsAnimator?.cancel()
        controlsAnimator = null
        decorAnimator?.cancel()
        decorAnimator = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        recordStopButton.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        paddingStart + paddingEnd + recordStopButton.measuredWidth

    override fun getSuggestedMinimumHeight(): Int =
        paddingTop + paddingBottom + recordStopButton.measuredHeight

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val modeLeftSpace = if (mode == AudioRecordMode.MESSAGE_PANEL) dimens.fieldHorizontalMargin else 0
        val fieldStart = paddingStart + modeLeftSpace
        val fieldEnd = measuredWidth - paddingEnd - animatedEndSpacing
        val fieldBottom = measuredHeight - dimens.fieldVerticalMargin
        val fieldTop = fieldBottom - dimens.fieldHeight
        fieldDrawable.setBounds(fieldStart, fieldTop, fieldEnd, fieldBottom)

        val recordButtonInnerRadius = dimens.recordStopButtonSize / 2f
        val recordButtonCenter = fieldEnd - recordButtonInnerRadius to fieldTop + recordButtonInnerRadius
        recordStopButton.layout(
            (recordButtonCenter.first - recordStopButton.measuredWidth / 2f).roundToInt(),
            (recordButtonCenter.second - recordStopButton.measuredHeight / 2f).roundToInt()
        )

        val indicatorStart = fieldStart + dimens.recordIndicatorMarginStart
        val indicatorTop = fieldTop + dimens.recordIndicatorMarginTop
        recordingIndicator.setBounds(
            indicatorStart,
            indicatorTop,
            indicatorStart + dimens.recordIndicatorSize,
            indicatorTop + dimens.recordIndicatorSize
        )

        val timerStart = recordingIndicator.bounds.right + dimens.recordIndicatorMarginEnd
        val timerBottom = fieldBottom - dimens.timerTextMarginBottom
        timerDrawable.setBounds(
            timerStart,
            timerBottom - timerDrawable.intrinsicHeight,
            timerStart + timerDrawable.intrinsicWidth,
            timerBottom
        )
        val cancelAvailableSpace = fieldDrawable.bounds.width()
            .minus(dimens.recordStopButtonSize)
            .minus(timerDrawable.bounds.right - fieldStart)
        cancelLayout.layout(
            timerDrawable.bounds.right + ((cancelAvailableSpace - cancelLayout.width) / 2f).roundToInt(),
            fieldTop + ((fieldDrawable.bounds.height() - cancelLayout.height) / 2f).roundToInt()
        )
        updateCancelClickableRect()
        decorAttachButton.layout(paddingStart, fieldBottom - decorAttachButton.height)
    }

    override fun onDraw(canvas: Canvas) {
        decorAttachButton.draw(canvas)
        fieldDrawable.draw(canvas)
        cancelLayout.draw(canvas)
        recordingIndicator.draw(canvas)
        timerDrawable.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        cancelLayout.onTouch(this, event) || super.onTouchEvent(event)

    override fun verifyDrawable(who: Drawable): Boolean =
        when (who) {
            recordingIndicator,
            timerDrawable,
            fieldDrawable -> true
            else -> super.verifyDrawable(who)
        }

    private fun updateCancelClickableRect() {
        val cancelHalfWidth = cancelLayout.width / 2
        cancelClickableRect.set(
            cancelLayout.left - cancelHalfWidth,
            fieldDrawable.bounds.top,
            cancelLayout.right + cancelHalfWidth,
            fieldDrawable.bounds.bottom
        )
        cancelLayout.setStaticTouchRect(cancelClickableRect)
    }
}

private data class AudioRecordControlDimens(
    val fieldHorizontalMargin: Int,
    val fieldVerticalMargin: Int,
    val fieldHeight: Int,
    val recordIndicatorMarginStart: Int,
    val recordIndicatorMarginTop: Int,
    val recordIndicatorMarginEnd: Int,
    val timerTextMarginBottom: Int,
    val recordIndicatorSize: Int,
    val recordStopButtonSize: Int
) {
    companion object {
        fun create(context: Context) = with(context) {
            AudioRecordControlDimens(
                fieldHorizontalMargin = context.getDimenPx(RDesign.attr.offset_m),
                fieldVerticalMargin = context.getDimenPx(RDesign.attr.offset_xs),
                fieldHeight = context.getDimenPx(RDesign.attr.inlineHeight_2xs),
                recordIndicatorMarginStart = context.getDimenPx(RDesign.attr.offset_m),
                recordIndicatorMarginTop = resources.getDimensionPixelSize(RRecorderCommon.dimen.design_message_panel_recorder_common_record_indicator_padding_top),
                recordIndicatorMarginEnd = context.getDimenPx(RDesign.attr.offset_s),
                timerTextMarginBottom = context.getDimenPx(RDesign.attr.offset_m),
                recordIndicatorSize = context.getDimenPx(RDesign.attr.offset_xs),
                recordStopButtonSize = context.getDimenPx(RDesign.attr.inlineHeight_2xs)
            )
        }
    }
}

private const val MAX_CANCEL_TEXT_SIZE_DP = 18
private const val MIN_CANCEL_TEXT_SIZE_DP = 14