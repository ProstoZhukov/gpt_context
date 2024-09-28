package ru.tensor.sbis.widget_player.widget.table.model

import ru.tensor.sbis.jsonconverter.generated.Row
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class TableRowElement(
    tag: String, attributes: WidgetAttributes, resources: WidgetResources
) : GroupWidgetElement(tag, attributes, resources) {

    val row = Row()

    override fun onChildAdded(element: WidgetElement) {
        super.onChildAdded(element)
        if (element is TableCellElement) {
            row.cells.add(element.cell)
        }
    }

    override fun onChildRemoved(element: WidgetElement) {
        super.onChildRemoved(element)
        if (element is TableCellElement) {
            row.cells.remove(element.cell)
        }
    }
}