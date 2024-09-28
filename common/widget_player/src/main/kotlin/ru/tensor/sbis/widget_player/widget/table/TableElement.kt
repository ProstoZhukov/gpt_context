package ru.tensor.sbis.widget_player.widget.table

import ru.tensor.sbis.jsonconverter.generated.*
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.converter.element.GroupWidgetElement
import ru.tensor.sbis.widget_player.converter.WidgetResources
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.widget.table.model.TableCellElement
import ru.tensor.sbis.widget_player.widget.table.model.TableRowElement
import ru.tensor.sbis.widget_player.widget.table.model.TableViewData
import kotlin.math.max

/**
 * @author am.boldinov
 */
internal class TableElement(
    tag: String, attributes: WidgetAttributes,
    resources: WidgetResources,
    private val shrinkParams: TableShrinkParams?
) : GroupWidgetElement(tag, attributes, resources) {

    private val rows = ArrayList<Row>()
    private val cells = mutableListOf<TableCellElement>()

    val viewData by lazy(LazyThreadSafetyMode.NONE) {
        computeViewData(shrinkParams)
    }

    override fun onChildAdded(element: WidgetElement) {
        super.onChildAdded(element)
        if (element is TableRowElement) {
            rows.add(element.row)
        }
    }

    override fun onChildRemoved(element: WidgetElement) {
        super.onChildRemoved(element)
        if (element is TableRowElement) {
            rows.remove(element.row)
        }
    }

    fun toFullTableElement(): TableElement {
        return TableElement(tag, attributes, resources, null).also { full ->
            children.forEach {
                full.addChild(it)
            }
        }
    }

    fun computeViewData() {
        viewData // init lazy value
    }

    private fun computeViewData(params: TableShrinkParams?): TableViewData {
        var shrinkParams: TableShrinkParams? = null
        children.forEach {
            if (it is TableRowElement) {
                it.children.forEach { cell ->
                    if (cell is TableCellElement) {
                        cells.add(cell)
                    }
                }
            }
        }
        if (params != null) {
            val desiredCells = params.rowsLimit * params.columnsLimit
            if (desiredCells == 0) { // одна из сторон может быть без ограничений
                shrinkParams = params
            } else if (cells.size > desiredCells) {
                var rowsLimit = params.rowsLimit
                var columnsLimit = params.columnsLimit
                if (rows.size < rowsLimit) {
                    rowsLimit = max(rows.size, 1)
                    columnsLimit = desiredCells / rowsLimit
                } else if (rows.size > rowsLimit) {
                    val columnsSize = calculateColumnCount()
                    if (columnsSize < columnsLimit) {
                        columnsLimit = max(columnsSize, 1)
                        rowsLimit = desiredCells / columnsLimit
                    }
                }
                shrinkParams = TableShrinkParams(columnsLimit, rowsLimit)
            }
        }
        val table = TablesController.getPrecomputedTable(
            table = Table(
                rows,
                TableCellElement.MEASURE_CONSTRAINT_BY_CONTENT,
                TableCellElement.MEASURE_CONSTRAINT_BY_CONTENT
            ),
            shrinkParams = shrinkParams
        )
        return TableViewData(this, table, cells)
    }

    private fun calculateColumnCount(): Int {
        var max = 0
        rows.forEach {
            max = max(it.cells.size, max)
        }
        return max
    }
}