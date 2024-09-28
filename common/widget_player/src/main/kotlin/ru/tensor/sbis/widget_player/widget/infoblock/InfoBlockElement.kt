package ru.tensor.sbis.widget_player.widget.infoblock

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class InfoBlockElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val emojiChar: String?
) : GroupWidgetElement(tag, attributes, resources)