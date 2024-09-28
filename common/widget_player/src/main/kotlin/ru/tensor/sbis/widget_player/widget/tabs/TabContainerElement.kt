package ru.tensor.sbis.widget_player.widget.tabs

import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement

/**
 * @author am.boldinov
 */
internal class TabContainerElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val data: TabContainerData
) : GroupWidgetElement(tag, attributes, resources)