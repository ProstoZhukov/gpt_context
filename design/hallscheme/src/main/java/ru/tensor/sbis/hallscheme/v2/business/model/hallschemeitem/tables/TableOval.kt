package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.UUID

/**
 * Овальный стол со стульями по двум сторонам.
 * id = 3
 */
internal class TableOval(
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
            tableInfo.totalPlaces > 2 -> chairFactor * ((tableInfo.totalPlaces - 1) / 2)
            else -> 0
        }

    override fun getChairType(chairNumber: Int): ChairType {
        return getHorizontalChairType(chairNumber)
    }

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        var x: Int = tablePadding.left + tableSpec.extraWidth / 2
        if (chairNumber > 2) x += getChairMargin(chairNumber)
        val y: Int =
            if (isChairEvenNumber(chairNumber)) {
                getBottomChairPosition(chairHeight)
            } else {
                getTopChairPosition(chairHeight)
            }

        return SchemeItemBounds(x, y, x + chairSpecWidth, y + chairHeight)
    }

    private fun getChairMargin(number: Int) = chairFactor * ((number - 1) / 2)

    /**
     * У увального занятого стола столешница увеличивается на треть от высоты стула.
     */
    override fun getOccupiedEnhancement(): Int = chairSpecHeight / 3

    override fun getInfoViewXCanvas(): Float {
        val middleX = rotatedRect.width / 2F
        return middleX - getInfoViewWidth() / 2
    }

    override fun getInfoViewYCanvas(): Float {
        val middleY = rotatedRect.height / 2F
        return middleY - getInfoViewHeight() / 2
    }

    override fun getInfoViewWidth(): Int = tableSpecWidthInt
}