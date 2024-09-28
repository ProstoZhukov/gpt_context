package ru.tensor.sbis.widget_player.widget.video

import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal class VideoMessageWidgetComponent(
    private val viewDataFactory: VideoMessageViewDataFactory
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = VideoMessageElementFactory(viewDataFactory),
        inflater = {
            Widget(
                context = this,
                renderer = VideoMessageRenderer(this, videoMessageOptions)
            )
        }
    )
}