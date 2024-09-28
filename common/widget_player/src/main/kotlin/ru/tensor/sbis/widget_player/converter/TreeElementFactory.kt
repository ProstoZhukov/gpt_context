package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
class TreeElementFactory<ELEMENT : WidgetElement>(
    private val root: WidgetElementFactory<ELEMENT>
) : WidgetElementFactory<ELEMENT> by root {

    internal val store = mutableMapOf<String, TreeElementFactory<out WidgetElement>>()

    fun <CHILD : WidgetElement> child(
        vararg widget: String,
        factory: WidgetElementFactory<CHILD>
    ): TreeElementFactory<CHILD> {
        val childTreeFactory = TreeElementFactory(factory)
        widget.forEach {
            store[it.lowercase()] = childTreeFactory
        }
        return childTreeFactory
    }
}