package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.widget.WidgetInflater

/**
 * @author am.boldinov
 */
interface WidgetComponent<ELEMENT : WidgetElement> {

    val elementFactory: WidgetElementFactory<ELEMENT>

    val inflater: WidgetInflater<ELEMENT>

    val dataUpdater: WidgetElementUpdater<ELEMENT>?
        get() = null

    companion object {

        fun <ELEMENT : WidgetElement> create(
            elementFactory: WidgetElementFactory<ELEMENT>,
            inflater: WidgetInflater<ELEMENT>,
            dataUpdater: WidgetElementUpdater<ELEMENT>? = null
        ) = object : WidgetComponent<ELEMENT> {
            override val elementFactory = elementFactory
            override val inflater = inflater
            override val dataUpdater = dataUpdater
        }
    }
}

