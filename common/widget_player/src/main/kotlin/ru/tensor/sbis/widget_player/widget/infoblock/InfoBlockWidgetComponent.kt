package ru.tensor.sbis.widget_player.widget.infoblock

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotEmpty
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class InfoBlockWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            val emojiChar = attributes.getNotEmpty("icon")
            InfoBlockElement(tag, attributes, environment.resources, emojiChar)
        },
        inflater = {
            GroupWidget(
                context = this,
                renderer = InfoBlockRenderer(this, infoBlockOptions)
            )
        }
    )
}