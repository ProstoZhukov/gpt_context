package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.emotion_picker

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.CRYING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.POUTING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.SMILING
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion.THINKING
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.SmileSendButton
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getThemeColorInt
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.design.R as RDesign

/**
 * Панель выбора эмоции для сообщения.
 *
 * @author rv.krohalev
 */
class MessageEmotionPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : LinearLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
), KeyboardEventListener {
    private val emotions = listOf(SMILING, POUTING, THINKING, CRYING)
    private val smileButtons: List<SmileSendButton>

    private val smileSize = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_smile_size)
    private val smileSpacing = resources.getDimensionPixelSize(R.dimen.design_message_panel_audio_recorder_smile_spacing)
    private val horizontalPadding = Offset.M.getDimenPx(context)
    private val verticalPadding = Offset.S.getDimenPx(context)

    private val smilesPressedColor = context.getThemeColorInt(RDesign.attr.paleActiveColor)
    private val backgroundColor = BackgroundColor.DEFAULT.getValue(context)

    private var fadeAnimator: ValueAnimator? = null
    private val disposer = SerialDisposable()

    private var listener: EmotionPickerListener? = null

    init {
        orientation = HORIZONTAL
        background = ColorDrawable(backgroundColor)
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(
                    paddingStart,
                    paddingTop,
                    paddingStart + measuredWidth,
                    paddingTop + measuredHeight,
                    (measuredHeight - paddingTop - paddingBottom) / 2f
                )
            }
        }
        clipToOutline = true
        smileButtons = addButtons()
    }

    /**
     * Показать панель выбора эмоции.
     *
     * @param listener слушатель результата выбора.
     */
    fun show(listener: EmotionPickerListener) {
        if (isVisible) return
        this.listener = listener
        disposer.set(null)
        animateFade(showing = true)
    }

    /**
     * Скрыть панель выбора эмоции.
     */
    fun hide() {
        if (!isVisible) return
        disposer.set(null)
        listener = null
        animateFade(showing = false)
    }

    /**
     * Очистить при завершении работы с панелью.
     */
    fun clear() {
        isVisible = false
        listener = null
        disposer.set(null)
        fadeAnimator?.cancel()
        fadeAnimator = null
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        translationY = -keyboardHeight.toFloat()
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        translationY = 0f
        return true
    }

    private fun addButtons(): List<SmileSendButton> {
        val smileLayoutParams = ViewGroup.LayoutParams(smileSize, smileSize)
        return emotions.map {
            createSmileButtonView(it).also { smileButton ->
                addView(smileButton, smileLayoutParams)
            }
        }
    }

    private fun createSmileButtonView(type: AudioMessageEmotion): SmileSendButton =
        SmileSendButton(context, smilesPressedColor, type).apply {
            smileClickListener = ::onSendClicked
        }

    private fun onSendClicked(emotion: AudioMessageEmotion) {
        listener?.invoke(emotion)
        listener = null
        hide()
    }

    private fun animateFade(showing: Boolean) {
        fadeAnimator?.cancel()
        fadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = FADE_DURATION_MS
            if (showing) {
                interpolator = DecelerateInterpolator()
                isVisible = true
                alpha = 0f
                addUpdateListener { alpha = it.animatedValue as Float }
                doOnEnd { startHideTimer() }
                start()
                pause()
                doOnPreDraw { resume() }
            } else {
                interpolator = AccelerateInterpolator()
                addUpdateListener { alpha = 1f - it.animatedValue as Float }
                doOnEnd { isVisible = false }
                start()
            }
        }
    }

    private fun startHideTimer() {
        Observable.timer(SHOW_TIME_MS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { hide() }
            .storeIn(disposer)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = emotions.fold(0) { w, _ -> w + smileSize + 2 * smileSpacing } + 2 * horizontalPadding -
                2 * smileSpacing // не учитываем пропуски для крайних смайлов (слева и справа)
        val height = smileSize + 2 * verticalPadding
        smileButtons.forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val initialLeft = horizontalPadding - smileSpacing
        val top = verticalPadding
        smileButtons.scan(initialLeft) { left, button ->
            button.layout(left + smileSpacing, top, left + smileSpacing + smileSize, top + smileSize)
            left + smileSpacing * 2 + smileSize
        }
    }
}

/**
 * Слушатель результата выбора эмоции.
 * null - эмоция не была выбрана пользователем за время показа.
 */
typealias EmotionPickerListener = (AudioMessageEmotion?) -> Unit

private const val SHOW_TIME_MS = 3000L
private const val FADE_DURATION_MS = 150L