package ru.tensor.sbis.widget_player.widget.video

import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
internal class VideoMessageElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val data: VideoMessageViewData
) : WidgetElement(tag, attributes, resources)