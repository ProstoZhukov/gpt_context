package ru.tensor.sbis.widget_player.widget.list.root

import ru.tensor.sbis.widget_player.api.WidgetComponentFactory
import ru.tensor.sbis.widget_player.config.WidgetOptions
import ru.tensor.sbis.widget_player.converter.WidgetComponent
import ru.tensor.sbis.widget_player.layout.widget.GroupWidget

/**
 * @author am.boldinov
 */
internal class ListViewWidgetComponent : WidgetComponentFactory {

    override fun WidgetOptions.create() = WidgetComponent.create(
        elementFactory = ListViewElementFactory(listViewOptions),
        inflater = {
            GroupWidget(
                context = this,
                renderer = ListViewRenderer(this)
            )
        }
    )
}