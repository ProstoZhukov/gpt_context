package ru.tensor.sbis.widget_player.widget.textlayout

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget
import ru.tensor.sbis.widget_player.layout.widget.viewGroupRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams

/**
 * @author am.boldinov
 */
internal class TextLayoutWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = { tag, attributes, environment ->
            TextLayoutElement(tag, attributes, environment.resources)
        },
        inflater = {
            GroupWidget(
                context = this,
                renderer = viewGroupRenderer {
                    VerticalBlockLayout(this).apply {
                        setDefaultWidgetLayoutParams()
                    }
                }
            )
        }
    )
}