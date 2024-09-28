package ru.tensor.sbis.widget_player.widget.blockquote

import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.FontStyle

/**
 * @author am.boldinov
 */
internal class BlockQuoteElement(
    tag: String, attributes: WidgetAttributes, resources: WidgetResources
) : GroupWidgetElement(tag, attributes, resources) {

    init {
        styleReducer = {
            fontStyle = FontStyle.ITALIC
        }
    }
}