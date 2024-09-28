package ru.tensor.sbis.widget_player.widget.image.icon

import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.widget.image.ImageRequest

/**
 * @author am.boldinov
 */
internal class IconElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val request: ImageRequest
) : WidgetElement(tag, attributes, resources)