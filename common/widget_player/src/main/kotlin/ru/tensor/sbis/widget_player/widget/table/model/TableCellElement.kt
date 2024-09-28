package ru.tensor.sbis.widget_player.widget.table.model

import ru.tensor.sbis.jsonconverter.generated.Cell
import ru.tensor.sbis.jsonconverter.generated.MeasureConstraint
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes

/**
 * @author am.boldinov
 */
internal class TableCellElement(
    tag: String,
    attributes: WidgetAttributes,
    resources: WidgetResources,
    colSpan: Int?,
    rowSpan: Int?
) : GroupWidgetElement(tag, attributes, resources) {

    companion object {
        private const val DEFAULT_SPAN = 1

        val MEASURE_CONSTRAINT_BY_CONTENT = MeasureConstraint()
    }

    val cell = Cell(
        MEASURE_CONSTRAINT_BY_CONTENT,
        MEASURE_CONSTRAINT_BY_CONTENT,
        colSpan ?: DEFAULT_SPAN,
        rowSpan ?: DEFAULT_SPAN
    )
}