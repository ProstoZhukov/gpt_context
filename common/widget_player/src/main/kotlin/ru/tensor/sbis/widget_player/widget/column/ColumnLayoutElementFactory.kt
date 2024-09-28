package ru.tensor.sbis.widget_player.widget.column

import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsInt
import ru.tensor.sbis.widget_player.converter.attributes.store.getAsJsonAttributesList

/**
 * @author am.boldinov
 */
internal class ColumnLayoutElementFactory(
    private val options: ColumnLayoutOptions
) : WidgetElementFactory<ColumnLayoutElement> {

    override fun create(
        tag: String,
        attributes: WidgetAttributes,
        environment: WidgetEnvironment
    ): ColumnLayoutElement {
        val proportions = attributes.getAsJsonAttributesList("itemsWidth").map {
            it.takeIf {
                it.get("type") == "proportion"
            }?.getAsInt("value") ?: options.defaultItemProportion
        }
        return ColumnLayoutElement(tag, attributes, environment.resources, proportions)
    }
}