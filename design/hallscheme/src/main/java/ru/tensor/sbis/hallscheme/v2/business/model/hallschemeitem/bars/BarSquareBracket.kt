package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.TablePadding
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.UUID

/**
 * Бар "скобкой".
 * type = 100
 * |____|
 * @author aa.gulevskiy
 */
internal class BarSquareBracket internal constructor(
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
    barHeightFactor: Float,
    topEdgeWidthFactor: Float,
    chairMargin: Int = getDefaultMargin(tableSpec, CHAIR_MARGIN_DIVIDER),
    tableInfo: TableInfo
) :
    Bar(
        id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
        tableSpec, chairSpec, billSpec, bookingSpec, assigneeSpec, chairMargin = chairMargin,
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
    val topEdgeWidth: Float = topEdgeWidthFactor * tableSpecWidth - occupationOffset.horizontal

    override val height = (tableSpecWidthInt * barHeightFactor).toInt()

    override val tablePadding = TablePadding(
        left = chairSpecHeight,
        top = (chairSpecHeight * barHeightFactor).toInt(),
        right = chairSpecHeight,
        bottom = (defaultPadding * barHeightFactor).toInt()
    )

    override fun getChairType(chairNumber: Int): ChairType = ChairType.BOTTOM

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
}