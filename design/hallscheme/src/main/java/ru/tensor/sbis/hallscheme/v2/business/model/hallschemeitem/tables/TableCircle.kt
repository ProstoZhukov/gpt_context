package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPoint
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Коэффициент увеличения для круглых столов с количеством стульев >= 8.
 */
private const val BIG_SIZE_MULTIPLIER = 1.5f

/**
 * Круглый стол с максимум четырьмя стульями.
 * @author aa.gulevskiy
 * id = 2
 */
internal class TableCircle(
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

    override val rect: SchemeItemBounds by unsafeLazy {
        val scaleFactor =
            if (tableInfo.totalPlaces >= 8) BIG_SIZE_MULTIPLIER
            else 1.0f

        val size = ((tableSpecWidthInt) * scaleFactor).toInt() + defaultPadding * 2

        SchemeItemBounds(x, y, x + size, y + size)
    }

    override val addedLength: Int = 0

    /**
     * У круглого занятого стола столешница увеличивается на треть от высоты стула.
     */
    override fun getOccupiedEnhancement(): Int = chairSpecHeight / 3

    override fun getChairType(chairNumber: Int): ChairType = ChairType.TOP

    override fun getChairAngle(chairNumber: Int): Float {
        when {
            tableInfo.totalPlaces == 3 -> {
                if (chairNumber == 2) return 270f
                if (chairNumber == 3) return 90f
            }
            tableInfo.totalPlaces <= 4 ->
                when (chairNumber) {
                    2 -> return 180f
                    3 -> return 270f
                    4 -> return 90f
                }
            else -> return 360f / tableInfo.totalPlaces * (chairNumber - 1)
        }

        return 0f
    }

    /**
     * Все стулья изначально сверху посередине, дальше их положение будет изменено
     * поворотом канвы на нужный градус.
     */
    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        val x = rect.width / 2 - chairSpecWidth / 2
        val y = getTopChairPosition(chairHeight)
        return SchemeItemBounds(
            BoundsPoint(x, y),
            BoundsPoint(x + chairSpecWidth, y + chairHeight)
        )
    }

    override fun getInfoViewXCanvas(): Float =
        defaultPadding + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left

    override fun getInfoViewYCanvas(): Float =
        defaultPadding + tableTopHeight / 2 - tableSpecWidth / 2 - occupationOffset.top

    override fun getInfoViewWidth(): Int = tableSpecWidthInt

    override fun getAdditionalBillOffset(): Int = -billSpec.width / 2
}
