package ru.tensor.sbis.design.message_panel.audio_recorder.view.send

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewApi
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.controller.AudioRecordSendViewController
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.AudioRecordSendLayout

/**
 * Компонент подготовки отправки аудиосообщения с возможностью управлению записью.
 * (Монстр созданный проектированием)
 * @see AudioRecordSendViewApi
 *
 * @author vv.chekurda
 */
internal class AudioRecordSendView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: AudioRecordSendViewController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    AudioRecordSendViewApi by controller {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttrs: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttrs, defStyleRes, AudioRecordSendViewController())

    private val layout = AudioRecordSendLayout(this)

    init {
        setWillNotDraw(false)
        layout.init()
        controller.attachLayout(layout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout.onMeasure(widthMeasureSpec)
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    override fun getSuggestedMinimumWidth(): Int =
        layout.getSuggestedMinimumWidth()

    override fun getSuggestedMinimumHeight(): Int =
        layout.getSuggestedMinimumHeight()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layout.onLayout()
    }

    override fun onDraw(canvas: Canvas) {
        layout.onDraw(canvas)
    }

    override fun setVisibility(visibility: Int) {
        val current = isVisible
        super.setVisibility(visibility)
        if (isVisible != current) {
            controller.onVisibilityChanged(isVisible)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        layout.onTouchEvent(event) || super.onTouchEvent(event)

    override fun onSaveInstanceState(): Parcelable =
        controller.onSaveInstanceState(super.onSaveInstanceState())

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(controller.onRestoreInstanceState(state))
    }
}