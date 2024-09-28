package ru.tensor.sbis.widget_player.widget.list.item

import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.style.TextDecoration
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsBoolean
import ru.tensor.sbis.widget_player.widget.list.root.ListViewOptions

/**
 * @author am.boldinov
 */
internal class ListItemElementFactory(
    private val options: ListViewOptions
) : WidgetElementFactory<ListItemElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): ListItemElement {
        val checked = attributes.getAsBoolean("checked") ?: false
        return ListItemElement(tag, attributes, environment.resources, checked).apply {
            val defaultTextColor = style.textColor
            val defaultDecoration = style.textDecoration
            if (checked) { // TODO уточнить как на веб работает reduce для вложенных (применяется только для ListItemContent (нулевого индекса))
                styleReducer = {
                    textColor = options.checkedTextColor
                    textDecoration = TextDecoration.LINE_THROUGH
                }
            } else {
                styleReducer = {
                    textColor = defaultTextColor
                    textDecoration = defaultDecoration
                }
            }
        }
    }
}