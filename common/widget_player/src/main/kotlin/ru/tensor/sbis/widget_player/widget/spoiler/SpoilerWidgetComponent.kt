package ru.tensor.sbis.widget_player.widget.spoiler

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class SpoilerWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            SpoilerElement(tag, attributes, environment.resources)
        },
        inflater = {
            GroupWidget(
                context = this,
                renderer = SpoilerRenderer(this, spoilerOptions)
            )
        }
    )
}