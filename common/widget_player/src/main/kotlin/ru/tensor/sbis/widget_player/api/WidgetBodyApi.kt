package ru.tensor.sbis.widget_player.api

import ru.tensor.sbis.widget_player.config.WidgetBodyDecorationBuilder

/**
 * @author am.boldinov
 */
interface WidgetBodyApi {

    fun decorate(decoration: WidgetBodyDecorationBuilder.() -> Unit)
}