package ru.tensor.sbis.widget_player.widget.table.model

import ru.tensor.sbis.jsonconverter.generated.PrecomputedTable
import ru.tensor.sbis.widget_player.widget.table.TableElement

/**
 * Вью-модель с данными для биндинга во View с таблицей
 *
 * @author am.boldinov
 */
internal class TableViewData(
    val root: TableElement,
    val table: PrecomputedTable,
    private val cells: List<TableCellElement>,
) {

    var isFullDataShowing = false

    val cellCount = table.items.size

    /**
     * Является ли таблица ограниченной по размеру
     */
    fun isShrink(): Boolean {
        return cellCount < cells.size
    }

    /**
     * Возвращает модель ячейки по позиции.
     */
    fun getCell(position: Int): TableCellElement {
        if (position < 0 || position >= cellCount) {
            throw IndexOutOfBoundsException("Attempt to get cell for position $position")
        }
        val ordinal = table.items[position].ordinal.toInt()
        return cells[ordinal]
    }

    /**
     * Возвращает позицию ячейки в таблице на основе ее порядкового номера
     */
    fun getCellPosition(ordinal: Int): Int {
        if (isShrink()) {
            val items = table.items
            if (ordinal < items.size && items[ordinal].ordinal.toInt() == ordinal) {
                return ordinal
            }
            items.forEachIndexed { index, item ->
                if (item.ordinal.toInt() == ordinal) {
                    return index
                }
            }
            return 0
        } else {
            return ordinal
        }
    }
}