package ru.tensor.sbis.widget_player.widget.blockquote

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class BlockQuoteWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            BlockQuoteElement(tag, attributes, environment.resources)
        },
        inflater = {
            GroupWidget(
                context = this,
                renderer = BlockQuoteRenderer(this)
            )
        }
    )
}