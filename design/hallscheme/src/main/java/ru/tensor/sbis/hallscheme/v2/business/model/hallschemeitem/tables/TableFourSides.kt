package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * Стол со стульями с четырёх сторон.
 * id = 0
 * @author aa.gulevskiy
 */
internal open class TableFourSides(
    id: UUID?,
    cloudId: Int?,
    category: String?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    sofaStyle: Int,
    tableSpec: HallSchemeSpecHolder.TableSpec,
    chairSpec: HallSchemeSpecHolder.ChairSpec,
    billSpec: HallSchemeSpecHolder.BillSpec,
    bookingSpec: HallSchemeSpecHolder.BookingSpec,
    assigneeSpec: HallSchemeSpecHolder.AssigneeSpec,
    chairMargin: Int = getDefaultMargin(tableSpec),
    tableInfo: TableInfo
) : TableItem(
    id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
    tableSpec, chairSpec, billSpec, bookingSpec, assigneeSpec, chairMargin, tableInfo
) {

    override val addedLength: Int
        get() = when {
            tableInfo.totalPlaces > 4 -> chairFactor * ((tableInfo.totalPlaces - 3) / 2)
            else -> 0
        }

    companion object {
        private val chairTypes = mapOf(
            1 to ChairType.TOP,
            2 to ChairType.BOTTOM,
            3 to ChairType.LEFT,
            4 to ChairType.RIGHT
        )

        private val chairTypesWhen3Places = mapOf(
            1 to ChairType.TOP,
            2 to ChairType.LEFT,
            3 to ChairType.RIGHT
        )
    }

    override fun getChairType(chairNumber: Int): ChairType {
        if (tableInfo.totalPlaces == 3) return getChairTypeWhenPlaces3(chairNumber)

        return getChairTypeDefault(chairNumber)
    }

    private fun getChairTypeWhenPlaces3(chairNumber: Int): ChairType {
        return chairTypesWhen3Places[chairNumber] ?: throw IndexOutOfBoundsException()
    }

    private fun getChairTypeDefault(chairNumber: Int): ChairType {
        return chairTypes[chairNumber] ?: getHorizontalChairType(chairNumber)
    }

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        val chairType = getChairType(chairNumber)
        return getChairBoundsByType(chairType, chairNumber, chairHeight)
    }

    /**
     * Возвращает границы стула по его типу и порядковому номеру.
     * @param chairNumber тип стула.
     * @param chairNumber порядковый номер стула.
     * @param chairHeight "высота" стула.
     * @return [SchemeItemBounds]
     */
    fun getChairBoundsByType(chairType: ChairType, chairNumber: Int, chairHeight: Int): SchemeItemBounds {
        var x: Int
        val y: Int

        return when (chairType) {
            ChairType.LEFT -> {
                y = rect.height / 2 - chairSpecWidth / 2
                x = getLeftChairPosition(chairHeight)
                SchemeItemBounds(x, y, x + chairHeight, y + chairSpecWidth)
            }
            ChairType.TOP -> {
                x = tablePadding.left + tableSpec.extraWidth / 2
                if (chairNumber > 4) x += getChairMargin(chairNumber)
                y = getTopChairPosition(chairHeight)
                SchemeItemBounds(x, y, x + chairSpecWidth, y + chairHeight)
            }
            ChairType.RIGHT -> {
                y = rect.height / 2 - chairSpecWidth / 2
                x = getRightChairPosition(chairHeight)
                SchemeItemBounds(x, y, x + chairHeight, y + chairSpecWidth)
            }
            ChairType.BOTTOM -> {
                x = tablePadding.left + tableSpec.extraWidth / 2
                if (chairNumber > 5) x += getChairMargin(chairNumber - 1)
                y = getBottomChairPosition(chairHeight)
                SchemeItemBounds(x, y, x + chairSpecWidth, y + chairHeight)
            }
        }
    }

    private fun getChairMargin(number: Int) = (number - 2) / 2 * chairFactor
}