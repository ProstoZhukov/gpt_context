package ru.tensor.sbis.widget_player.api

import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
fun interface WidgetComponentFactory {

    fun WidgetOptions.create(): WidgetComponent<out WidgetElement>
}