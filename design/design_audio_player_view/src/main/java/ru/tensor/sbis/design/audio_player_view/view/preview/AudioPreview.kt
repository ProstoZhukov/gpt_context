package ru.tensor.sbis.design.audio_player_view.view.preview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.audio_player_view.R
import ru.tensor.sbis.design.audio_player_view.view.preview.children.WaveformWithDuration
import ru.tensor.sbis.design.audio_player_view.view.preview.contract.AudioPreviewApi
import ru.tensor.sbis.design.audio_player_view.view.preview.data.AudioPreviewData
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding

/**
 * Компонент превью аудиосообщения.
 * @see AudioPreviewApi
 *
 * @author dv.baranov
 */
class AudioPreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    AudioPreviewApi {

    private val recognizedText = SbisTextView(context).apply {
        id = R.id.design_audio_message_view_preview_recognized_text_id
        setTextColor(TextColor.LABEL.getValue(context))
        textSize = FontSize.M.getScaleOnDimenPx(context).toFloat()
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
        includeFontPadding = false
        setHorizontalPadding(Offset.S.getDimenPx(context), 0)
    }

    private val waveformWithDuration = WaveformWithDuration(context).apply {
        id = R.id.design_audio_message_view_preview_waveform_with_duration_id
    }

    override var data: AudioPreviewData? = null
        set(value) {
            field = value
            value?.apply {
                setWaveform(waveform)
                setDuration(durationSeconds)
                recognizedText?.let { setRecognizedText(recognizedText) }
            }
            safeRequestLayout()
        }

    init {
        addView(waveformWithDuration)
        addView(recognizedText)
    }

    /**
     * Отобразить осциллограмму аудио.
     */
    private fun setWaveform(waveform: ByteArray?) {
        waveformWithDuration.setWaveform(waveform)
    }

    /**
     * Отобразить длительность аудио.
     */
    private fun setDuration(durationSeconds: Int) {
        waveformWithDuration.setDuration(durationSeconds)
    }

    private fun setRecognizedText(text: String?) {
        if (text != recognizedText.text) {
            recognizedText.text = text
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        waveformWithDuration.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        val recognizedTextAvailableWidth = width - waveformWithDuration.measuredWidth - paddingStart - paddingEnd
        recognizedText.measure(makeExactlySpec(recognizedTextAvailableWidth), makeUnspecifiedSpec())
        setMeasuredDimension(width, maxOf(recognizedText.height, waveformWithDuration.measuredHeight))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        waveformWithDuration.layout(paddingStart, 0)
        val recognizedTextTop = (measuredHeight - recognizedText.measuredHeight) / 2
        recognizedText.layout(waveformWithDuration.right, recognizedTextTop)
    }
}