package ru.tensor.sbis.widget_player.widget.column

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class ColumnLayoutElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val childrenProportions: List<Int>
) : GroupWidgetElement(tag, attributes, resources)