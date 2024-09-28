package ru.tensor.sbis.widget_player.widget.image

import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class ImageElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val request: ImageRequest
) : WidgetElement(tag, attributes, resources) {

    val previewUrl = request.previewUrl
}