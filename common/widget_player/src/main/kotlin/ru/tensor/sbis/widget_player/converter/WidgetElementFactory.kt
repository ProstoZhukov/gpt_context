package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
fun interface WidgetElementFactory<ELEMENT : WidgetElement> {

    fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): ELEMENT

    companion object {

        fun <ELEMENT : WidgetElement> tree(
            factory: WidgetElementFactory<ELEMENT>,
            children: TreeElementFactory<ELEMENT>.() -> Unit
        ): WidgetElementFactory<ELEMENT> {
            return TreeElementFactory { tag, attributes, resources ->
                factory.create(tag, attributes, resources)
            }.apply(children)
        }
    }
}