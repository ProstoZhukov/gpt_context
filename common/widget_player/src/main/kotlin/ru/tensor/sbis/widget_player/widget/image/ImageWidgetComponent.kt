package ru.tensor.sbis.widget_player.widget.image

import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory
import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.Widget

/**
 * @author am.boldinov
 */
internal class ImageWidgetComponent(
    private val viewerSliderIntentFactory: ViewerSliderIntentFactory?
) : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = ImageElementFactory(imageOptions),
        inflater = {
            val renderer = ImageRenderer(this, imageOptions)
            Widget(
                context = this,
                renderer = renderer,
                controller = {
                    ImageWidgetController(
                        context = this,
                        viewerSliderIntentFactory = viewerSliderIntentFactory,
                        eventProvider = renderer
                    )
                }
            )
        }
    )
}