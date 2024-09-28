package ru.tensor.sbis.widget_player.widget.header

import ru.tensor.sbis.widget_player.converter.element.ContentNavigationElement
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.element.TextWrapperElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class HeaderElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    val level: HeaderLevel
) : GroupWidgetElement(tag, attributes, resources), TextWrapperElement, ContentNavigationElement