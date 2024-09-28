package ru.tensor.sbis.widget_player.widget.embed.youtube

import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class YouTubeElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val videoId: String?
) : WidgetElement(tag, attributes, resources)