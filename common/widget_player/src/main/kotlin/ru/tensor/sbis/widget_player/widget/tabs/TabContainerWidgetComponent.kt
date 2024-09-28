package ru.tensor.sbis.widget_player.widget.tabs

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class TabContainerWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = TabContainerElementFactory(),
        inflater = {
            GroupWidget(
                context = this,
                renderer = TabContainerRenderer(this, tabOptions)
            )
        }
    )
}