package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.custom_view_tools.utils.SimplePaint
import ru.tensor.sbis.design.custom_view_tools.utils.dp

/**
 * Кнопка отправки аудиосообщения со смайликом.
 *
 * @property type тип отправки.
 *
 * @author vv.chekurda
 */
@SuppressLint("ViewConstructor")
internal class SmileSendButton(
    context: Context,
    @ColorInt pressedColor: Int,
    private val type: AudioMessageEmotion
) : View(context) {

    private val pressedPaint = SimplePaint {
        color = pressedColor
        alpha = (0.5f * 255).toInt()
    }
    private var currentPressed = isPressed
    private val drawablePadding = dp(SMILE_DRAWABLE_PADDING_DP).toFloat()
    private val smileDrawable = getDrawableByType(type)

    /**
     * Внутренний отступ картинок смайликов.
     */
    val drawableInnerPadding = dp(1)

    /**
     * Слушатель нажатий на кнопку.
     */
    var smileClickListener: OnSmileClickListener? = null
        set(value) {
            field = value
            setOnClickListener { smileClickListener?.invoke(type) }
        }

    init {
        isClickable = true
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (isPressed != currentPressed) {
            currentPressed = isPressed
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        smileDrawable.setBounds(
            paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        smileDrawable.draw(canvas)
        if (!isPressed) return
        canvas.drawRoundRect(
            drawablePadding,
            drawablePadding,
            width.toFloat() - drawablePadding,
            height.toFloat() - drawablePadding,
            width / 2f - drawablePadding,
            height / 2f - drawablePadding,
            pressedPaint
        )
    }

    private fun getDrawableByType(type: AudioMessageEmotion): Drawable =
        ContextCompat.getDrawable(context, type.drawableResId!!)!!
}

/**
 * Слушатель нажатий на кнопку смайлика.
 */
internal typealias OnSmileClickListener = (AudioMessageEmotion) -> Unit

private const val SMILE_DRAWABLE_PADDING_DP = 1