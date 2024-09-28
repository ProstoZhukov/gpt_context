package ru.tensor.sbis.widget_player.widget.video

import ru.tensor.sbis.design.video_message_view.message.VideoMessageView
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class VideoMessageRenderer(
    context: WidgetContext,
    options: VideoMessageOptions
) : WidgetRenderer<VideoMessageElement> {

    override val view = VideoMessageView(context).apply {
        setDefaultWidgetLayoutParams().apply {
            topMargin = options.marginTop.getValuePx(context)
            bottomMargin = options.marginBottom.getValuePx(context)
        }
    }

    override fun render(element: VideoMessageElement) {
        with(view) {
            data = element.data
        }
    }
}