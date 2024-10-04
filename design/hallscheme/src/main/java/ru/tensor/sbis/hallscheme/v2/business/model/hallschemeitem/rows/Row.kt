package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.iterateTo
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*
import kotlin.math.abs

/**
 * Модель, описывающая ряд/массив рядов (концертный зал).
 * @author aa.gulevskiy
 */
internal class Row(
    id: UUID?,
    category: String?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    private val placeFrom: Int, // Номер первого места в ряду
    private val placeTo: Int, // Номер последнего места в ряду
    private val rowFrom: Int, // Номер первого ряда
    private val rowTo: Int, // Номер последнего ряда
    val showLeftLabel: Boolean = false, //  Отображать ли метку ряда слева
    val showRightLabel: Boolean = false, //  Отображать ли метку ряда справа
    private val placeSize: Int,
    private val placeMargin: Int
) : HallSchemeItem(id, null, category, disposition, kind, name, type, x, y, z) {

    /**
     *  Размер (длина) ряда.
     */
    val labelSize = placeSize

    /**
     * Список мест [RowPlace] в ряду.
     */
    val items: MutableList<List<RowPlace>> = mutableListOf()

    override val rect: SchemeItemBounds by unsafeLazy {
        calculateRect()
    }

    init {
        fillRowItems()
    }

    private fun fillRowItems() {
        for (rowNumber in rowFrom iterateTo rowTo) {
            items.add(fillRow(rowNumber))
        }
    }

    private fun fillRow(rowNumber: Int): List<RowPlace> {
        val rowAsString = rowNumber.toString()
        val row = mutableListOf<RowPlace>()

        for (placeNumber in placeFrom iterateTo placeTo) {
            row.add(RowPlace(rowAsString, placeNumber.toString()))
        }

        return row
    }

    private fun calculateRect(): SchemeItemBounds {
        val width = calculateRowWidth()
        val height = calculateColumnHeight()

        return SchemeItemBounds(x, y, x + width, y + height)
    }

    private fun calculateRowWidth(): Int {
        val placeNumbers = abs(placeTo - placeFrom) + 1
        val leftLabelWidth = if (showLeftLabel) labelSize else 0
        val rightLabelWidth = if (showRightLabel) labelSize else 0

        return leftLabelWidth + placeSize * placeNumbers +
                placeMargin * (placeNumbers - 1) + rightLabelWidth
    }

    private fun calculateColumnHeight(): Int {
        val rowsNumbers = abs(rowTo - rowFrom) + 1
        return placeMargin + rowsNumbers * placeSize + placeMargin * (rowsNumbers - 1) + placeMargin
    }
}