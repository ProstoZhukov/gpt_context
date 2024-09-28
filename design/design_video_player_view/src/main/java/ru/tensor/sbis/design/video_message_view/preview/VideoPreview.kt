package ru.tensor.sbis.design.video_message_view.preview

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.attachments.ui.view.AttachmentPreviewView
import ru.tensor.sbis.attachments.ui.viewmodel.base.preview.AttachmentPreviewVM
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.preview.children.VideoPreviewDurationView
import ru.tensor.sbis.design.video_message_view.preview.contract.VideoPreviewApi
import ru.tensor.sbis.design.video_message_view.preview.data.VideoPreviewData
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Компонент превью видеосообщения.
 * @see VideoPreviewApi
 *
 * @author dv.baranov
 */
class VideoPreview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    VideoPreviewApi {

    private val videoPreviewSize = dp(VIDEO_PREVIEW_SIZE)
    private val spaceBetweenPreviewAndRecognizedText = Offset.M.getDimenPx(context)

    override var data: VideoPreviewData? = null
        set(value) {
            field = value
            value?.apply {
                setDuration(durationSeconds)
                setPreview(previewVM)
                recognizedText?.let { setRecognizedText(it) }
            }
            safeRequestLayout()
        }

    /**
     * Вью для отображения превью видео
     */
    private val previewView = AttachmentPreviewView(context, attrs).apply {
        id = R.id.design_video_player_view_preview_attachment_preview_id
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                with(view) {
                    val width = measuredWidth - paddingStart - paddingEnd
                    val height = measuredHeight - paddingTop - paddingBottom
                    val size = minOf(width, height)
                    val start = ((measuredWidth - width) / 2f).roundToInt()
                    val top = ((measuredHeight - height) / 2f).roundToInt()
                    outline.setOval(start, top, start + size, top + size)
                }
            }

        }
        clipToOutline = true
    }

    private val durationView = VideoPreviewDurationView(context).apply {
        id = R.id.design_video_player_view_preview_duration_id
    }

    private val recognizedText = SbisTextView(context).apply {
        id = R.id.design_video_player_view_preview_recognized_text_id
        textSize = FontSize.M.getScaleOnDimen(context)
        setTextColor(TextColor.DEFAULT.getValue(context))
        maxLines = RECOGNIZED_TEXT_MAX_LINES
        includeFontPadding = false
    }

    /**
     * Троеточие, которое отображаем, если текст расшифровки не умещается на 3 строки.
     */
    private val dots = SbisTextView(context, RDesign.style.MobileFontStyle).apply {
        id = R.id.design_video_player_view_preview_dots_id
        textSize = IconSize.XL.getDimenPx(context).toFloat()
        setTextColor(TextColor.DEFAULT.getValue(context))
        text = SbisMobileIcon.Icon.smi_yet.character.toString()
        gravity = Gravity.BOTTOM
        includeFontPadding = false
    }

    init {
        addView(previewView)
        addView(durationView)
        addView(recognizedText)
        addView(dots)
    }

    private fun setDuration(durationSeconds: Int) {
        durationView.setDuration(durationSeconds)
    }

    private fun setPreview(previewVM: AttachmentPreviewVM) {
        previewView.setViewModel(previewVM)
    }

    private fun setRecognizedText(text: String) {
        recognizedText.text = text
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        previewView.measure(makeExactlySpec(videoPreviewSize), makeExactlySpec(videoPreviewSize))
        durationView.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        val recognizedTextAvailableWidth = width - paddingStart - paddingEnd -
            previewView.measuredWidth - spaceBetweenPreviewAndRecognizedText
        dots.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        recognizedText.measure(
            makeExactlySpec(recognizedTextAvailableWidth),
            makeUnspecifiedSpec()
        )
        dots.visibility = if (recognizedText.lineCount > RECOGNIZED_TEXT_MAX_LINES) VISIBLE else INVISIBLE
        setMeasuredDimension(
            width,
            maxOf(previewView.measuredHeight, recognizedText.measuredHeight + dots.measuredHeight)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        previewView.layout(paddingStart, 0)
        durationView.layout(previewView.left, previewView.bottom - durationView.measuredHeight)
        recognizedText.layout(previewView.right + spaceBetweenPreviewAndRecognizedText, previewView.top)
        dots.safeLayout(recognizedText.left, recognizedText.bottom)
    }
}

private const val VIDEO_PREVIEW_SIZE = 80
private const val RECOGNIZED_TEXT_MAX_LINES = 4