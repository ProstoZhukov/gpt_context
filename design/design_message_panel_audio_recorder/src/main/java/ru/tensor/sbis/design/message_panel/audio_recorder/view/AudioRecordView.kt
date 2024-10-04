package ru.tensor.sbis.design.message_panel.audio_recorder.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import ru.tensor.sbis.design.message_panel.audio_recorder.R
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewApi
import ru.tensor.sbis.design.message_panel.audio_recorder.view.controller.AudioRecordViewController
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Компонент записи аудиосообщения.
 * @see AudioRecordViewApi
 *
 * @author vv.chekurda
 */
class AudioRecordView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: AudioRecordViewController
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
), AudioRecordViewApi by controller {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.audioRecordViewTheme,
        @StyleRes defStyleRes: Int = R.style.AudioRecordViewDefaultStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, AudioRecordViewController())

    private val controlView = RecordControlView(getContext()).apply {
        id = R.id.design_message_panel_audio_record_control_view
    }
    private val sendView = AudioRecordSendView(getContext()).apply {
        id = R.id.design_message_panel_audio_record_send_view
        isVisible = false
    }

    init {
        addView(controlView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(sendView, LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM))
        controller.attachViews(this, controlView, sendView)
    }

    override fun onSaveInstanceState(): Parcelable =
        controller.onSaveInstanceState(super.onSaveInstanceState())

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(controller.onRestoreInstanceState(state))
    }
}