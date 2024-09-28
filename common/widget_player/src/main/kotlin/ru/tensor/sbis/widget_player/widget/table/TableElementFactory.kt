package ru.tensor.sbis.widget_player.widget.table

import ru.tensor.sbis.jsonconverter.generated.TableShrinkParams
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment

/**
 * @author am.boldinov
 */
internal class TableElementFactory(
    options: TableOptions
) : WidgetElementFactory<TableElement> {

    private val shrinkParams = with(options.tableSize) {
        TableShrinkParams(columnLimit, rowLimit)
    }

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): TableElement {
        return TableElement(tag, attributes, environment.resources, shrinkParams)
    }
}