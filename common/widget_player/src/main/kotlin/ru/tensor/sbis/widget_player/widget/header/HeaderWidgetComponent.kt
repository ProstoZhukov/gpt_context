package ru.tensor.sbis.widget_player.widget.header

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class HeaderWidgetComponent(
    private val level: HeaderLevel? = null
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = HeaderElementFactory(headerOptions, level),
        inflater = {
            GroupWidget(
                context = this,
                renderer = HeaderRenderer(this, headerOptions)
            )
        }
    )
}