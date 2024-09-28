package ru.tensor.sbis.widget_player.widget.column

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class ColumnLayoutWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = ColumnLayoutElementFactory(columnLayoutOptions),
        inflater = {
            GroupWidget(
                context = this,
                renderer = ColumnLayoutRenderer(this, columnLayoutOptions)
            )
        }
    )
}