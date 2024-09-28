package ru.tensor.sbis.widget_player.widget.embed

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget
import ru.tensor.sbis.widget_player.widget.embed.youtube.YouTubeRenderer
import ru.tensor.sbis.widget_player.widget.text.FormattedTextRenderer

/**
 * @author am.boldinov
 */
internal class EmbedWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = EmbedElementFactory(),
        inflater = {
            Widget(
                context = this,
                renderer = EmbedRenderer(
                    context = this,
                    paragraphOptions = paragraphOptions,
                    youTubeRenderer = lazy(LazyThreadSafetyMode.NONE) {
                        YouTubeRenderer(this, youTubeOptions)
                    },
                    textLinkRenderer = lazy(LazyThreadSafetyMode.NONE) {
                        FormattedTextRenderer(this, textOptions, decoratedLinkOptions)
                    }
                )
            )
        }
    )
}