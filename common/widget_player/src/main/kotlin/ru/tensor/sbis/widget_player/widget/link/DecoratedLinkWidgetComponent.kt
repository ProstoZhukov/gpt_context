package ru.tensor.sbis.widget_player.widget.link

import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkType
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget
import ru.tensor.sbis.widget_player.widget.text.FormattedTextRenderer

/**
 * @author am.boldinov
 */
internal class DecoratedLinkWidgetComponent(
    private val linkType: DecoratedLinkType? = null
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = if (decoratedLinkOptions.decorationEnabled) {
        WidgetComponent.create(
            elementFactory = DecoratedLinkElementFactory(decoratedLinkOptions, linkType),
            inflater = {
                Widget(
                    context = this,
                    renderer = DecoratedLinkRenderer(this, decoratedLinkOptions, textOptions)
                )
            }
        )
    } else {
        WidgetComponent.create(
            elementFactory = TextLinkElementFactory(),
            inflater = {
                Widget(
                    context = this,
                    renderer = FormattedTextRenderer(this, textOptions, decoratedLinkOptions)
                )
            }
        )
    }
}