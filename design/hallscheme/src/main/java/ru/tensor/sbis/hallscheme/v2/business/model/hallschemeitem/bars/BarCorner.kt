package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.TablePadding
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.UUID

/**
 * Угловой бар.
 * type = 102
 * |____
 * @author aa.gulevskiy
 */
internal class BarCorner constructor(
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
    private val barHeightFactor: Float,
    topEdgeWidthFactor: Float,
    defaultPlacesCount: Int = 5,
    chairMargin: Int = getDefaultMargin(tableSpec, CHAIR_MARGIN_DIVIDER),
    tableInfo: TableInfo
) : Bar(
    id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
    tableSpec, chairSpec, billSpec, bookingSpec, assigneeSpec, defaultPlacesCount,
    // Один стул расположен сбоку
    defaultPlacesCount - 1, chairMargin = chairMargin,
    tableInfo = tableInfo
) {

    override val occupationOffset by unsafeLazy {
        TablePadding(
            0,
            occupationEnhancement,
            0,
            occupationEnhancement
        )
    }

    /**
     * Ширина верхней части бара.
     */
    internal val topEdgeWidth: Float = topEdgeWidthFactor * tableSpecWidth - occupationOffset.horizontal

    override val height = (tableSpecWidth * barHeightFactor).toInt()

    override val tablePadding = TablePadding(
        left = defaultPadding,
        top = (chairSpecHeight * barHeightFactor).toInt(),
        right = (chairSpecHeight * barHeightFactor).toInt(),
        bottom = (defaultPadding * barHeightFactor).toInt()
    )

    override val indexOfFirstHorizontalChair = 2

    override val firstHorizontalChairX = sideMargin + tablePadding.left

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        return when (chairNumber) {
            1 -> getLeftChairBounds(chairHeight)
            else -> getHorizontalChairBounds(chairNumber, chairHeight)
        }
    }

    private fun getLeftChairBounds(chairHeight: Int): SchemeItemBounds {
        val chairX: Int = getLeftChairPosition(chairHeight)
        val chairY: Int = (edgesHeightsDiff + tableSpec.extraWidth / 2 + chairSpecHeight * barHeightFactor).toInt()
        return SchemeItemBounds(
            chairX,
            chairY,
            chairX + chairHeight,
            chairY + chairSpecWidth
        )
    }

    override fun getChairType(chairNumber: Int): ChairType {
        return (when (chairNumber) {
            1 -> ChairType.LEFT
            else -> ChairType.BOTTOM
        })
    }

    override fun getInfoViewYCanvas(): Float {
        return when (itemRotation) {
            0 -> tablePadding.top + edgesHeightsDiff
            180 -> tablePadding.bottom.toFloat() + depth
            else -> tablePadding.left + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left
        }
    }

    override fun getInfoViewXCanvas(): Float {
        val tableLeft = tablePadding.left
        return when (itemRotation) {
            90 -> tablePadding.bottom.toFloat() + depth
            270 -> tableLeft + height - tableSpecWidth
            else -> tableLeft + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left
        }
    }

    override fun getBillViewY(): Float {
        val yPos = super.getBillViewY()
        return when (itemRotation) {
            0 -> yPos + edgesHeightsDiff
            else -> yPos
        }
    }

    override fun getBookingExtraVerticalOffset() =
        when (itemRotation) {
            180 -> -edgesHeightsDiff
            else -> 0F
        }
}