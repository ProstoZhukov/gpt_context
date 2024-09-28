package ru.tensor.sbis.widget_player.widget.embed

import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class EmbedElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val content: WidgetElement
) : WidgetElement(tag, attributes, resources)