package ru.tensor.sbis.design.video_message_view.preview.children

import android.content.Context
import android.graphics.Canvas
import android.view.Gravity
import android.view.View
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.getDrawableFrom
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью для отображения продолжительности видеосообщения в превью.
 *
 * @author dv.baranov
 */
internal class VideoPreviewDurationView(
    context: Context
) : View(context) {

    private val viewHeight = dp(DURATION_VIEW_HEIGHT)
    private val durationHorizontalPadding = Offset.X2S.getDimenPx(context)

    private val durationTextLayout = TextLayout {
        paint.color = TextColor.CONTRAST.getValue(context)
        paint.textSize = FontSize.X3S.getScaleOffDimen(context)
        includeFontPad = false
        isSingleLine = true
        verticalGravity = Gravity.CENTER_VERTICAL
        minHeight = viewHeight
        maxHeight = viewHeight
    }

    private val playIcon = createTextLayoutByStyle(context, RDesign.style.MobileFontStyle) {
        text = SbisMobileIcon.Icon.smi_dayForward.character.toString()
        paint.color = IconColor.CONTRAST.getValue(context)
        paint.textSize = IconSize.S.getDimenPx(context).toFloat()
        isSingleLine = true
        includeFontPad = false
        verticalGravity = Gravity.CENTER_VERTICAL
        minHeight = viewHeight
        maxHeight = viewHeight
    }

    init {
        setWillNotDraw(false)
        background = getDrawableFrom(R.drawable.video_message_preview_duration_bg).apply {
            this?.alpha = (DURATION_VIEW_BACKGROUND_ALPHA * 255).toInt()
        }
    }

    /**
     * Отобразить длительность видео.
     */
    fun setDuration(durationSeconds: Int) {
        val minutes = durationSeconds / MINUTE_TO_SECONDS
        val seconds = durationSeconds % MINUTE_TO_SECONDS
        val isChanged = durationTextLayout.buildLayout { text = DURATION_FORMAT.format(minutes, seconds) }
        if (isChanged) invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED,
            MeasureSpec.AT_MOST -> {
                val width = playIcon.width + durationTextLayout.width + durationHorizontalPadding * 2
                setMeasuredDimension(width, viewHeight)
            }
            MeasureSpec.EXACTLY -> {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        playIcon.layout(durationHorizontalPadding, 0)
        durationTextLayout.layout(playIcon.right, 0)
    }

    override fun onDraw(canvas: Canvas) {
        playIcon.draw(canvas)
        durationTextLayout.draw(canvas)
    }
}

private const val DURATION_FORMAT = "%2d:%02d"
private const val MINUTE_TO_SECONDS = 60
private const val DURATION_VIEW_BACKGROUND_ALPHA = 0.3
private const val DURATION_VIEW_HEIGHT = 22