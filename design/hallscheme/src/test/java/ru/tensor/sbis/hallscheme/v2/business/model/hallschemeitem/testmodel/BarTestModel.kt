package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.Booking
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.Bar
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import java.util.*

/**
 * @author aa.gulevskiy
 */
internal class BarTestModel(
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
    totalSum: Double,
    maxDishLatency: Long,
    dishesNumber: Int,
    itemSpec: HallSchemeSpecHolder.TableSpec,
    chairSpec: HallSchemeSpecHolder.ChairSpec,
    billSpec: HallSchemeSpecHolder.BillSpec,
    bookingSpec: HallSchemeSpecHolder.BookingSpec,
    assigneeSpec: HallSchemeSpecHolder.AssigneeSpec
) : Bar(
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
    tableInfo = TableInfo(
        places,
        totalSum,
        maxDishLatency,
        billNumber,
        bookings,
        dishesNumber.toString(),
        tableOutlines = emptyList()
    )
) {

    override val height: Int
        get() = tableSpecWidthInt

    override fun getChairType(chairNumber: Int): ChairType = ChairType.BOTTOM

}