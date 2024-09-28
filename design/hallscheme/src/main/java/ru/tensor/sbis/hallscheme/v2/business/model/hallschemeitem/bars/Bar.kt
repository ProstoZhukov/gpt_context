package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Коэффициент для приведения размеров баров в соответствии с вебом.
 * Чем он больше, тем меньше расстояние между стульями.
 */
internal const val CHAIR_MARGIN_DIVIDER = 1.8

/**
 * Абстрактный класс, описывающий бар (барную стойку).
 * @author aa.gulevskiy
 */
internal abstract class Bar internal constructor(
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
    private val defaultPlacesCount: Int = 4,
    lastChairIndexForDefault: Int = defaultPlacesCount,
    chairMargin: Int = getDefaultMargin(tableSpec, CHAIR_MARGIN_DIVIDER),
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

    /**
     * Отступ от края столешницы до первого стула.
     */
    protected val sideMargin = tableSpec.extraWidth

    /**
     * Высота бара.
     */
    abstract val height: Int

    /**
     * Координата X первого стула, расположенного горизонтально.
     */
    protected open val firstHorizontalChairX = sideMargin + chairSpecHeight

    /**
     * Разность высоты бара и высоты центральной части бара.
     */
    val edgesHeightsDiff: Float
        get() = height - tableSpecWidth

    /**
     * Индекс первого стула, расположенного горизонтально.
     */
    protected open val indexOfFirstHorizontalChair = 1

    /**
     * Ширина бара с дефолтным количеством мест.
     */
    protected val specWidth: Int = sideMargin +
            chairFactor * lastChairIndexForDefault +
            (sideMargin - chairMargin)

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(
            x,
            y,
            x + specWidth + addedLength + tablePadding.horizontal,
            y + height + tablePadding.vertical
        )
    }

    override val addedLength: Int
        get() = when {
            tableInfo.totalPlaces > defaultPlacesCount -> (tableInfo.totalPlaces - defaultPlacesCount) * chairFactor
            else -> 0
        }

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        return getHorizontalChairBounds(chairNumber, chairHeight)
    }

    /**
     * Возвращает границы стула, расположенного горизонтально.
     * @param chairNumber порядковый номер стула.
     * @param chairHeight "высота" стула.
     * @return [SchemeItemBounds]
     */
    protected fun getHorizontalChairBounds(chairNumber: Int, chairHeight: Int): SchemeItemBounds {
        val chairX = (firstHorizontalChairX + ((chairNumber - indexOfFirstHorizontalChair) * chairFactor))
        val chairY = getBottomChairPosition(chairHeight)
        return SchemeItemBounds(
            chairX,
            chairY,
            chairX + chairSpecWidth,
            chairY + chairHeight
        )
    }

    override fun getInfoViewWidth(): Int {
        return tableSpecWidthInt
    }
}