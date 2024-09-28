package ru.tensor.sbis.widget_player.widget.list.item

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class ListItemElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val checked: Boolean
) : GroupWidgetElement(tag, attributes, resources)