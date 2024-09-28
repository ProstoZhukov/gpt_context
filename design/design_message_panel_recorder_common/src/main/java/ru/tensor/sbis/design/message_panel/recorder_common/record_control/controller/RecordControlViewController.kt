package ru.tensor.sbis.design.message_panel.recorder_common.record_control.controller

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.FloatRange
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.message_panel.decl.quote.QuoteView
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordControlButton
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordButtonControlContainer
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordHintDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordLockView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordTimeDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecorderFieldDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.children.RecordingIndicatorDrawable
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.ControlEventsHandler
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlEvent
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlViewApi
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.layout.RecordControlDimens
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.layout.RecordControlLayout
import androidx.core.view.doOnPreDraw
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlRecipientsActionListener
import kotlin.math.roundToInt

/**
 * Реализует логику компонента [RecordControlView].
 * @see RecordControlViewApi
 *
 * @author vv.chekurda
 */
internal class RecordControlViewController : RecordControlViewApi {

    private lateinit var layout: RecordControlLayout
    private lateinit var view: RecordControlView
    private lateinit var recipientsView: RecipientsView
    private lateinit var quoteView: QuoteView
    private lateinit var controlContainer: RecordButtonControlContainer
    private lateinit var recordButton: RecordControlButton
    private lateinit var lockView: RecordLockView
    private lateinit var lockContainer: ViewGroup
    private lateinit var recordingIndicator: RecordingIndicatorDrawable
    private lateinit var timerDrawable: RecordTimeDrawable
    private lateinit var hintDrawable: RecordHintDrawable
    private lateinit var cancelLayout: TextLayout
    private lateinit var decorAttachButton: TextLayout
    private lateinit var decorSendButton: View
    private lateinit var fieldDrawable: RecorderFieldDrawable

    private lateinit var dimens: RecordControlDimens

    private val lockHideInterpolator = AccelerateInterpolator()
    private val showInterpolator = DecelerateInterpolator()
    private var controlsAnimator: ValueAnimator? = null
    private var decorAnimator: ValueAnimator? = null
    private var cancelAnimator: ValueAnimator? = null
    private var unlockHapticAction = Runnable { recordButton.isHapticFeedbackEnabled = true }
    private var hintTranslationX = 0f
    private var hintAnimationTranslationX = 0f
    private var animatedIndicatorAlpha = 0
    private var showWithLock: Boolean = false
        set(value) {
            field = value
            if (value) isLocked = true
        }
    private var isShowRunning = false
        set(value) {
            field = value
            if (value) {
                hintTranslationX = 0f
                hintAnimationTranslationX = 0f
            } else {
                showWithLock = false
            }
        }

    private var isRecipientsEnabled = false
        set(value) {
            field = value
            recipientsView.apply {
                isEnabled = value
                isClickable = value
            }
        }

    private var isQuoteEnabled = false
        set(value) {
            field = value
            quoteView.apply {
                isEnabled = value
                isClickable = value
            }
        }

    private val isFirstButtonPosition: Boolean
        get() = recordButtonPosition == RecordControlButtonPosition.FIRST_ALIGN_END

    private var isStopped: Boolean = false

    override var isLocked: Boolean = false
        private set(value) {
            val isChanged = field != value
            field = value
            if (!isChanged) return
            lockView.apply {
                setLockState(value, !isShowRunning)
                if (value && !showWithLock) {
                    alpha = 1f
                    translateYLockView(0f)
                }
            }
            controlContainer.isLocked = value
            if (value) {
                animateCancelLayout()
            }
        }

    override val recordDuration: Int
        get() = timerDrawable.recordingTimeSeconds.toInt()

    override var decorData: RecorderDecorData = RecorderDecorData()
        set(value) {
            val isChanged = field != value
            field = value

            if (isChanged) {
                recipientsView.apply {
                    recipientsView.data = value.recipientsData ?: RecipientsView.RecipientsViewData()
                    isVisible = value.recipientsData != null
                }
                quoteView.apply {
                    data = value.quoteData
                    isVisible = value.quoteData != null
                }
            }
        }

    override var isAudioRecord: Boolean
        get() = recordButton.isAudioRecord
        set(value) {
            recordButton.isAudioRecord = value
        }

    override lateinit var eventsHandler: ControlEventsHandler

    override var recipientsActionListener: RecordControlRecipientsActionListener? = null

    override var quoteActionListener: RecordControlQuoteActionListener? = null

    override var recordButtonPosition: RecordControlButtonPosition
        get() = layout.recordButtonPosition
        set(value) {
            layout.recordButtonPosition = value
        }

    override var isSendButtonEnabled: Boolean = true
        set(value) {
            field = value
            decorSendButton.isEnabled = value
        }

    override fun setAmplitude(amplitude: Float) {
        if (isStopped) return
        recordButton.amplitude = amplitude
    }

    override fun animateShowing(withLock: Boolean) {
        isRecipientsEnabled = false
        isQuoteEnabled = false
        startShowingAnimation()
        controlContainer.isEnabled = true
        if (withLock) {
            showWithLock = true
        } else {
            controlContainer.stealTouch()
        }
    }

    override fun animateHiding() {
        startCancelAnimation()
    }

    override fun startRecordAnimation() {
        recordingIndicator.start()
        timerDrawable.start()
        hintDrawable.start()
    }

    override fun stopRecordAnimation() {
        isStopped = true
        recordingIndicator.stop()
        recordingIndicator.alpha = 0
        timerDrawable.stop()
        hintDrawable.stop()
        lockView.hide(STOP_ANIMATION_DURATION_MS)
        recordButton.stopAmplitude()
        isRecipientsEnabled = recipientsActionListener != null
        isQuoteEnabled = quoteActionListener != null
    }

    override fun clearRecordAnimation() {
        cancelAllAnimations()
        isStopped = false
        isLocked = false
        view.alpha = 1f
        recordButton.isVisible = true
        controlContainer.isEnabled = false
        lockView.apply {
            isVisible = true
            alpha = 0f
        }
        cancelLayout.apply {
            alpha = 0f
            translationX = 0f
        }
        decorAttachButton.apply {
            configure { isVisible = true }
            alpha = 1f
        }
        decorSendButton.apply {
            alpha = 1f
            isVisible = true
        }
        recordingIndicator.apply {
            clear()
            translationX = fieldDrawable.collapsedPaddingStart
        }
        timerDrawable.apply {
            clear()
            translationX = fieldDrawable.collapsedPaddingStart
        }
        hintDrawable.apply {
            clear()
            translationX = fieldDrawable.collapsedPaddingStart
        }
        recordButton.clear()
        fieldDrawable.clear()
        translateYLockView(1f)
    }

    /**
     * Прикрепить разметку.
     */
    fun attachLayout(layout: RecordControlLayout) {
        this.layout = layout
        view = layout.view
        recipientsView = layout.recipientsView.apply {
            setOnClickListener { recipientsActionListener?.onRecipientsClicked() }
            recipientsClearListener = { recipientsActionListener?.onClearButtonClicked() }
        }
        quoteView = layout.quoteView.apply {
            setCloseListener { quoteActionListener?.onClearButtonClicked() }
        }
        controlContainer = layout.controlContainer
        recordButton = controlContainer.recordButton
        lockView = layout.lockView
        lockContainer = layout.lockContainer
        recordingIndicator = layout.recordingIndicator
        timerDrawable = layout.timerDrawable
        hintDrawable = layout.hintDrawable
        cancelLayout = layout.cancelLayout
        decorAttachButton = layout.decorAttachButton
        decorSendButton = layout.decorSendButton
        fieldDrawable = layout.fieldDrawable
        dimens = layout.dimens
        initViews()
    }

    private fun initViews() {
        recordButton.apply {
            setOnClickListener { onSendClicked() }
            isClickable = false
        }
        lockView.apply {
            setOnClickListener { onRecordStopped() }
            alpha = 0f
            isClickable = false
        }
        controlContainer.apply {
            sendListener = ::onRecordStopped
            cancelListener = ::onRecordCancelled
            buttonTranslationChangedListener = ::onButtonTranslationChanged
        }
        cancelLayout.setOnClickListener { _, _ ->
            if (isLocked) onRecordCancelled()
        }
    }

    private fun onRecordStopped() {
        isRecipientsEnabled = recipientsActionListener != null
        isQuoteEnabled = quoteActionListener != null
        eventsHandler.invoke(RecordControlEvent.OnRecordStopped)
    }

    private fun onRecordCancelled() {
        eventsHandler.invoke(RecordControlEvent.OnRecordCanceled)
    }

    private fun onSendClicked() {
        isRecipientsEnabled = recipientsActionListener != null
        isQuoteEnabled = quoteActionListener != null
        eventsHandler.invoke(RecordControlEvent.OnSendClicked)
    }

    // region animations

    private fun startShowingAnimation() {
        clearRecordAnimation()
        isShowRunning = true
        cancelLayout.alpha = 0f
        val recordControlsAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            controlsAnimator = this
            duration = CONTROLS_ANIMATION_DURATION_MS
            addUpdateListener { animateControlsVisibility(it.animatedFraction) }
            doOnEnd {
                eventsHandler.invoke(RecordControlEvent.OnReady)
                decorAttachButton.configure { isVisible = false }
                if (isFirstButtonPosition) decorSendButton.isVisible = false
                isShowRunning = false
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
        view.doOnPreDraw {
            if (!isLocked || showWithLock) {
                lockView.alpha = 1f
                translateYLockView(1f)
            }
            recordControlsAnimator.resume()
            decorButtonsAnimator.resume()
        }
        recordButton.isHapticFeedbackEnabled = false
        view.postDelayed(unlockHapticAction, CONTROLS_ANIMATION_DURATION_MS)
    }

    private fun startCancelAnimation() {
        cancelAllAnimations()
        recordingIndicator.stop()
        animatedIndicatorAlpha = recordingIndicator.alpha
        decorAttachButton.configure { isVisible = true }
        if (isFirstButtonPosition) decorSendButton.isVisible = true

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
            doOnEnd { eventsHandler.invoke(RecordControlEvent.OnHidingEnd) }
            start()
            pause()
        }
        view.doOnPreDraw {
            recordControlsAnimator.resume()
        }
    }

    private fun animateControlsVisibility(fraction: Float, isShowing: Boolean = true) {
        val interpolation = showInterpolator.getInterpolation(fraction)
        val reversedInterpolation = 1f - interpolation
        val controlsPaintAlpha = (interpolation * PAINT_MAX_ALPHA).toInt()
        val controlsTranslation = reversedInterpolation * fieldDrawable.collapsedPaddingStart
        recordButton.showingFraction = fraction
        fieldDrawable.expandFraction = fraction
        recordingIndicator.apply {
            alpha = if (isShowing) controlsPaintAlpha else (animatedIndicatorAlpha * interpolation).roundToInt()
            translationX = controlsTranslation
        }
        timerDrawable.apply {
            alpha = controlsPaintAlpha
            translationX = controlsTranslation
        }

        if (isLocked) {
            cancelLayout.translationX = controlsTranslation
            if (!isShowing) cancelLayout.alpha = interpolation
        } else {
            hintDrawable.apply {
                alpha = controlsPaintAlpha
                translateXHintDrawable(controlsTranslation, fromAnimation = true)
            }
        }

        val lockViewFraction = if (isShowing) {
            reversedInterpolation
        } else {
            // Нужно скрыть замочек быстрее, пока не пропала кнопка записи, иначе будет уродство.
            (reversedInterpolation * 2)
                .coerceAtLeast(0f)
                .coerceAtMost(1f)
        }
        translateYLockView(lockViewFraction)
        view.invalidate()
    }
    
    private fun animateDecorButtonsVisibility(fraction: Float) {
        val hideInterpolation = showInterpolator.getInterpolation(1f - fraction)
        decorAttachButton.alpha = hideInterpolation
        if (isFirstButtonPosition) decorSendButton.alpha = hideInterpolation
        view.invalidate()
    }

    private fun cancelAllAnimations() {
        controlsAnimator?.cancel()
        controlsAnimator = null
        decorAnimator?.cancel()
        decorAnimator = null
        cancelAnimator?.cancel()
        cancelAnimator = null
        isShowRunning = false
        view.removeCallbacks(unlockHapticAction)
    }

    private fun onButtonTranslationChanged(translationX: Float, translationY: Float) {
        if (isStopped) return
        translateXHintDrawable(translationX / 2)
        animateLockView(translationX, translationY)
        if (translationY.toInt() <= -dimens.lockRecordDistance) {
            isLocked = true
        }
    }

    private fun animateLockView(translationX: Float, translationY: Float) {
        val buttonDx = translationX + dimens.cancelIgnoreDx
        val fraction = (-buttonDx / (dimens.lockViewHideDistance - dimens.cancelIgnoreDx))
            .coerceAtMost(1f)
            .coerceAtLeast(0f)
        lockView.alpha = 1 - lockHideInterpolator.getInterpolation(fraction)
        lockView.activateFraction = minOf(-translationY / dimens.lockRecordDistance, 1f)
    }

    private fun translateYLockView(@FloatRange(from = 0.0, to = 1.0) fraction: Float) {
        lockView.translationY = fraction * lockContainer.height
    }

    private fun translateXHintDrawable(translationX: Float, fromAnimation: Boolean = false) {
        if (fromAnimation) {
            hintAnimationTranslationX = translationX
        } else {
            hintTranslationX = translationX
        }
        hintDrawable.translationX = hintTranslationX + hintAnimationTranslationX
    }

    private fun animateCancelLayout() {
        cancelAnimator?.end()
        cancelLayout.alpha = 0f
        ValueAnimator.ofFloat(0f, 1f).apply {
            cancelAnimator = this
            duration = CANCEL_LAYOUT_SHOWING_DURATION_MS
            val hintDrawableAlpha = hintDrawable.alpha
            addUpdateListener {
                hintDrawable.alpha = (hintDrawableAlpha * (1f - it.animatedFraction)).toInt()
                cancelLayout.alpha = it.animatedFraction
                view.invalidate()
            }
        }.start()
    }

    /**
     * Сохранение состояния состояние.
     */
    fun onSaveInstanceState(superState: Parcelable?): Parcelable =
        Bundle().apply {
            putParcelable(SUPER_STATE_KEY, superState)
            putBoolean(IS_CONTROLS_ENABLED_KEY, controlContainer.isEnabled)
            putFloat(LOCK_VIEW_TRANSLATION_Y_KEY, lockView.translationY)
        }

    /**
     * Восстановление состояния.
     */
    fun onRestoreInstanceState(state: Parcelable): Parcelable? =
        if (state is Bundle) {
            with(state) {
                controlContainer.isEnabled = getBoolean(IS_CONTROLS_ENABLED_KEY)
                lockView.translationY = getFloat(LOCK_VIEW_TRANSLATION_Y_KEY)
                getParcelable(SUPER_STATE_KEY)
            }
        } else null
}

/**
 * Продолжительность анимации кнопок для декора в мс.
 */
const val DECOR_BUTTONS_ANIMATION_DURATION_MS = 80L

/**
 * Продолжительность анимации появления контролов управления записью в мс.
 */
const val CONTROLS_ANIMATION_DURATION_MS = 240L

/**
 * Продолжительность анимации остановки записи.
 */
private const val STOP_ANIMATION_DURATION_MS = 240L

/**
 * Продолжительность анимации появления кнопки отмены записи в мс.
 */
private const val CANCEL_LAYOUT_SHOWING_DURATION_MS = 100L

private const val SUPER_STATE_KEY = "super_state"
private const val IS_CONTROLS_ENABLED_KEY = "controls_enable"
private const val LOCK_VIEW_TRANSLATION_Y_KEY = "lock_view_translation"