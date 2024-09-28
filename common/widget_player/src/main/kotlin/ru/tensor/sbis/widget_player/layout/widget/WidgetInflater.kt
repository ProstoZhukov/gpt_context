package ru.tensor.sbis.widget_player.layout.widget

import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
fun interface WidgetInflater<ELEMENT : WidgetElement> {

    fun WidgetContext.inflate(): Widget<ELEMENT>
}