package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.Booking
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
internal class OrderableItemTestModel(
    id: UUID?,
    cloudId: Int?,
    category: String?,
    disposition: Int,
    kind: String,
    name: String?,
    places: Int,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    sofaStyle: Int,
    billNumber: Int = 0,
    bookings: List<Booking> = emptyList(),
    totalSum: Double?,
    maxDishLatency: Long,
    dishesNumber: Int,
    itemSpec: HallSchemeSpecHolder.TableSpec,
    chairSpec: HallSchemeSpecHolder.ChairSpec,
    billSpec: HallSchemeSpecHolder.BillSpec,
    bookingSpec: HallSchemeSpecHolder.BookingSpec,
    assigneeSpec: HallSchemeSpecHolder.AssigneeSpec
) : OrderableItem(
    id,
    cloudId,
    category,
    disposition,
    kind,
    name,
    type,
    x,
    y,
    z,
    sofaStyle,
    itemSpec,
    chairSpec,
    billSpec,
    bookingSpec,
    assigneeSpec,
    tableInfo = TableInfo(places, totalSum, maxDishLatency, billNumber, bookings, dishesNumber.toString())
) {

    override val addedLength: Int = 0

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        return SchemeItemBounds()
    }

    override fun getChairType(chairNumber: Int): ChairType = ChairType.BOTTOM

}