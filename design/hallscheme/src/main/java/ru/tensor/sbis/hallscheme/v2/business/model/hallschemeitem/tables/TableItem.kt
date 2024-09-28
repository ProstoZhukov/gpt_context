package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Абстракнтый класс, описывающий стол на схеме зала.
 * @author aa.gulevskiy
 */
internal abstract class TableItem(
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
    tableSpec,
    chairSpec,
    billSpec,
    bookingSpec,
    assigneeSpec,
    chairMargin,
    tableInfo
) {

    init {
        // Дефолтное условие, было в старой реализации
        if (tableInfo.totalPlaces == 0) tableInfo.totalPlaces = 4
    }

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(
            x,
            y,
            x + tableSpecWidthInt + addedLength + tablePadding.horizontal,
            y + tableSpecWidthInt + tablePadding.vertical
        )
    }

    /**
     * Возвращает true, если стул чётный.
     * @param chairNumber порядковый номер стула.
     */
    protected fun isChairEvenNumber(chairNumber: Int) = chairNumber % 2 == 0

    /**
     * Возвращает тип стула по его порядковому номеру.
     * @param chairNumber порядковый номер стула.
     * @return тип стула [ChairType]
     */
    protected fun getHorizontalChairType(chairNumber: Int): ChairType {
        return if (isChairEvenNumber(chairNumber)) {
            ChairType.BOTTOM
        } else {
            ChairType.TOP
        }
    }
}