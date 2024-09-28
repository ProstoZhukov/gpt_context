package ru.tensor.sbis.widget_player.widget.image.icon

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal class IconWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = IconElementFactory(iconOptions),
        inflater = {
            Widget(
                context = this,
                renderer = IconRenderer(this)
            )
        }
    )
}