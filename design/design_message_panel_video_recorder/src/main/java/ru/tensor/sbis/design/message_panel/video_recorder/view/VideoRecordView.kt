package ru.tensor.sbis.design.message_panel.video_recorder.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewApi
import ru.tensor.sbis.design.message_panel.video_recorder.view.controller.VideoRecordViewController
import ru.tensor.sbis.design.message_panel.video_recorder.view.layout.VideoRecordViewLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Компонент записи круглых видео.
 * @see VideoRecordViewApi
 *
 * @author vv.chekurda
 */
class VideoRecordView private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    private val controller: VideoRecordViewController
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
), VideoRecordViewApi by controller {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttrs: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(context, attrs, defStyleAttrs, defStyleRes, VideoRecordViewController())

    private val layout = VideoRecordViewLayout(this)

    init {
        layout.init()
        controller.attachLayout(layout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
}