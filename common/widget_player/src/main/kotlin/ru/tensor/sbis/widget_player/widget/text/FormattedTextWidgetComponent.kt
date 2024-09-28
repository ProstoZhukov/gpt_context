package ru.tensor.sbis.widget_player.widget.text

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal class FormattedTextWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            FormattedTextElement(tag, attributes, environment.resources)
        },
        inflater = {
            Widget(
                this,
                FormattedTextRenderer(this, textOptions, decoratedLinkOptions)
            )
        }
    )
}