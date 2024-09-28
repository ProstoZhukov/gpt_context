package ru.tensor.sbis.design.audio_player_view.view.preview.children

import android.content.Context
import android.graphics.Canvas
import android.view.Gravity
import android.view.View
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.audio_player_view.R
import ru.tensor.sbis.design.audio_player_view.view.player.children.WaveformView
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.getDrawableFrom

/**
 * Вью для отображения осциллограммы и длительности аудиосообщения.
 *
 * @author dv.baranov
 */
internal class WaveformWithDuration(
    context: Context
) : View(context) {

    private val verticalPadding = Offset.X2S.getDimenPx(context)
    private val horizontalPadding = Offset.S.getDimenPx(context)

    private val waveformView = WaveformView(context)

    private val durationTextLayout = TextLayout {
        paint.color = TextColor.LABEL.getValue(context)
        paint.textSize = FontSize.XS.getScaleOnDimenPx(context).toFloat()
        includeFontPad = false
        isSingleLine = true
        verticalGravity = Gravity.CENTER_VERTICAL
        padding = TextLayout.TextLayoutPadding(start = Offset.XS.getDimenPx(context))
    }

    init {
        setWillNotDraw(false)
        background = getDrawableFrom(R.drawable.design_audio_preview_waveform_duration_bg)
    }

    /**
     * Отобразить осциллограмму аудио. Ожидаемый размер [WAVEFORM_SIZE].
     */
    fun setWaveform(waveform: ByteArray?) {
        waveformView.setWaveform(waveform ?: ByteArray(WAVEFORM_SIZE))
    }

    /**
     * Отобразить длительность аудио.
     */
    fun setDuration(durationSeconds: Int) {
        val minutes = durationSeconds / MINUTE_TO_SECONDS
        val seconds = durationSeconds % MINUTE_TO_SECONDS
        val isChanged = durationTextLayout.buildLayout { text = DURATION_FORMAT.format(minutes, seconds) }
        if (isChanged) invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        waveformView.measure(makeExactlySpec(dp(WAVEFORM_WIDTH)), makeExactlySpec(dp(WAVEFORM_HEIGHT)))
        val viewWidth = waveformView.measuredWidth + durationTextLayout.width + horizontalPadding * 2
        val viewHeight = maxOf(waveformView.measuredHeight, durationTextLayout.height) + verticalPadding * 2
        setMeasuredDimension(makeExactlySpec(viewWidth), makeExactlySpec(viewHeight))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val waveformTop = maxOf((measuredHeight - waveformView.measuredHeight) / 2, verticalPadding)
        waveformView.layout(horizontalPadding, waveformTop)
        val durationTop = (measuredHeight - durationTextLayout.height) / 2
        durationTextLayout.layout(waveformView.right, durationTop)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.withTranslation(horizontalPadding.toFloat(), verticalPadding.toFloat()) {
            waveformView.draw(canvas)
        }
        durationTextLayout.draw(canvas)
    }
}

private const val MINUTE_TO_SECONDS = 60
private const val DURATION_FORMAT = "%2d:%02d"
private const val WAVEFORM_SIZE = 8
private const val WAVEFORM_WIDTH = 32
private const val WAVEFORM_HEIGHT = 18