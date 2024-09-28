package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.business.TablePadding
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPoint
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPointF
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBoundsF
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Абстракнтый класс, представляющий элементы схемы, на которых можно сделать заказ (например, бар или стол).
 * @author aa.gulevskiy
 */
internal abstract class OrderableItem(
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
    val sofaStyle: Int,
    val tableSpec: HallSchemeSpecHolder.TableSpec,
    val chairSpec: HallSchemeSpecHolder.ChairSpec,
    val billSpec: HallSchemeSpecHolder.BillSpec,
    val bookingSpec: HallSchemeSpecHolder.BookingSpec,
    val assigneeSpec: HallSchemeSpecHolder.AssigneeSpec,
    val chairMargin: Int = getDefaultMargin(tableSpec),
    var tableInfo: TableInfo
) : HallSchemeItem(id, cloudId, category, disposition, kind, name, type, x, y, z) {

    companion object {
        /**
         * Дефолтное расстояние между стульями.
         */
        fun getDefaultMargin(tableSpec: HallSchemeSpecHolder.TableSpec, divider: Double = 13.0): Int {
            return (tableSpec.extraWidth / divider).toInt()
        }
    }

    init {
        if (tableInfo.totalPlaces <= 0) tableInfo.totalPlaces = 0
    }

    /**
     *  Высота "обрезанного" стула.
     */
    val chairSpecHeight = chairSpec.height

    /**
     *  Высота "не обрезанного" стула.
     */
    val chairSpecFullHeight = chairSpec.fullHeight

    /**
     *  Отступ по умодлчанию от границы вью до столешницы.
     */
    val defaultPadding = tableSpec.padding

    /**
     *  Отступ по умодлчанию от границы вью до столешницы во float.
     */
    val defaultPaddingF = defaultPadding.toFloat()

    /**
     *  Отступы от границ вью до столешницы стола.
     */
    open val tablePadding = TablePadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding)

    /**
     *  Отступы до края столешницы в зависимости от угла поворота.
     */
    val rotatedPadding
        get() = when (itemRotation) {
            90 -> TablePadding(
                tablePadding.bottom,
                tablePadding.left,
                tablePadding.top,
                tablePadding.right
            )
            180 -> TablePadding(
                tablePadding.right,
                tablePadding.bottom,
                tablePadding.left,
                tablePadding.top
            )
            270 -> TablePadding(
                tablePadding.top,
                tablePadding.right,
                tablePadding.bottom,
                tablePadding.left
            )
            else -> tablePadding
        }

    /**
     * Радиус угла столешницы.
     */
    val tableCornerRadius = tableSpec.cornerRadius

    /**
     * Толщина(высота) столешницы.
     */
    var depth = tableCornerRadius / 2

    /**
     * Толщина(высота) столешницы во float.
     */
    val floatDepth = depth.toFloat()

    /**
     * Длина столешницы.
     */
    val tableTopWidth: Float
        get() = tableRect.width.toFloat()

    /**
     * Ширина столешницы.
     */
    val tableTopHeight: Float
        get() = tableRect.height.toFloat()

    /**
     * Расстояние, добавленное к элементу в соответствии с количеством стульев.
     */
    abstract val addedLength: Int

    /**
     * Длина столешницы без закруглений.
     */
    val horizontalLineSize: Float by unsafeLazy {
        tableTopWidth - tableCornerRadius * 2
    }

    /**
     * Ширина столешницы без закруглений.
     */
    val verticalLineSize: Float by unsafeLazy {
        tableTopHeight - tableCornerRadius * 2
    }

    /**
     * Длина стульев.
     */
    protected val chairSpecWidth by unsafeLazy { chairSpec.width }

    /**
     * Расстояние, выделяемое под стул вместе с отступом до следующего стула.
     */
    protected val chairFactor by unsafeLazy { chairSpec.width + chairMargin }

    /**
     * Величина увеличения столешницы занятого стола.
     * По умолчанию равна половине высоты стула.
     */
    protected open fun getOccupiedEnhancement(): Int = chairSpecHeight / 2

    /**
     * Величина увеличения столешницы стола.
     */
    protected val occupationEnhancement by unsafeLazy {
        when (tableInfo.tableStatus) {
            is TableStatus.Occupied, is TableStatus.HasReadyDishes -> getOccupiedEnhancement()
            else -> 0
        }
    }

    /**
     * Увеличение столешницы с каждой из сторон.
     */
    open val occupationOffset by unsafeLazy {
        TablePadding(
            occupationEnhancement,
            occupationEnhancement,
            occupationEnhancement,
            occupationEnhancement
        )
    }

    /**
     * Прямоугольник, определяющий границы окружности, представляющей закруглённый угол элемента.
     */
    val cornerCircleBounds by unsafeLazy {
        val xPos = tablePadding.left.toFloat() - occupationOffset.left
        val yPos = tablePadding.top.toFloat() - occupationOffset.top
        SchemeItemBoundsF(
            BoundsPointF(xPos, yPos),
            BoundsPointF(xPos + tableCornerRadius * 2, yPos + tableCornerRadius * 2)
        )
    }

    /**
     * Длина стола.
     */
    protected val tableSpecWidthInt = tableSpec.extraWidth + chairFactor - chairMargin

    /**
     * Длина стола во float.
     */
    protected val tableSpecWidth = tableSpecWidthInt.toFloat()

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(x, y, x + tableSpecWidthInt, y + tableSpecWidthInt)
    }

    /**
     * Прямоугольник, определяющий границы столешницы.
     */
    protected val tableRect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(
            BoundsPoint(
                rect.left + tablePadding.left - occupationOffset.left,
                rect.top + tablePadding.top - occupationOffset.top
            ),
            BoundsPoint(
                rect.right - tablePadding.right + occupationOffset.right,
                rect.bottom - tablePadding.bottom + occupationOffset.bottom
            )
        )
    }

    /**
     * Возвращает горизонтальную координату столешницы.
     */
    fun getTableRectLeft() = tableRect.left

    /**
     * Возвращает вертикальную координату столешницы.
     */
    fun getTableRectTop() = tableRect.top

    /**
     * Возвращает границы стула.
     */
    abstract fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds

    /**
     * Возвращает тип стула.
     */
    abstract fun getChairType(chairNumber: Int): ChairType

    /**
     * Возвращает угол поворота стула.
     */
    open fun getChairAngle(chairNumber: Int): Float = 0f

    /**
     * Возвращает границы части дивана.
     */
    open fun getSofaPartBounds(sofaPartType: SofaPartType, index: Int): SchemeItemBounds = SchemeItemBounds()

    /**
     * Возвращает список частей диванов.
     */
    open fun getSofaParts(): List<SofaPartType> = emptyList()

    /**
     * Предоставляет координату X для отображения счетов.
     */
    open fun getBillViewX() = rotatedRect.right.toFloat() - rotatedPadding.right + billSpec.offset

    /**
     * Предоставляет координату Y для отображения счетов.
     */
    open fun getBillViewY() = rotatedRect.top.toFloat() + rotatedPadding.top - chairSpecHeight

    /**
     * Предоставляет координату X для отображения информационной вью на канвасе.
     */
    open fun getInfoViewXCanvas(): Float = tablePadding.left.toFloat()

    /**
     * Предоставляет координату Y для отображения информационной вью на канвасе.
     */
    open fun getInfoViewYCanvas(): Float = tablePadding.top.toFloat()

    /**
     * Предоставляет ширину информационной вью.
     */
    open fun getInfoViewWidth() = rotatedRect.width - tablePadding.left - tablePadding.right

    /**
     * Предоставляет высоту информационной вью.
     */
    open fun getInfoViewHeight() = tableSpecWidthInt

    /**
     * Возвращает дополнительный отступ для счёта.
     */
    open fun getAdditionalBillOffset(): Int = 0

    /**
     * Возвращает дополнительный отступ по горизонтали для плашки брони.
     */
    open fun getBookingExtraHorizontalOffset(): Float = 0f

    /**
     * Возвращает дополнительный отступ по вертикали для плашки брони.
     */
    open fun getBookingExtraVerticalOffset(): Float = 0f

    /**@SelfDocumented */
    protected fun getLeftChairPosition(chairHeight: Int) =
        (tablePadding.left - minOf(chairHeight, defaultPadding)).coerceAtLeast(0)

    /**@SelfDocumented */
    protected open fun getRightChairPosition(chairHeight: Int) =
        rect.width - tablePadding.right - (chairHeight - defaultPadding).coerceAtLeast(0)

    /**@SelfDocumented */
    protected fun getTopChairPosition(chairHeight: Int) =
        (tablePadding.top - minOf(chairHeight, defaultPadding)).coerceAtLeast(0)

    /**@SelfDocumented */
    protected open fun getBottomChairPosition(chairHeight: Int) =
        rect.height - tablePadding.bottom - (chairHeight - defaultPadding).coerceAtLeast(0)

    /**@SelfDocumented */
    fun hasDishes() = !tableInfo.dishesNumber.isNullOrBlank()

    /**@SelfDocumented */
    fun hasReadyDishes() = tableInfo.tableStatus is TableStatus.HasReadyDishes

    /**@SelfDocumented */
    fun needDrawBell() = tableInfo.bellCount != 0.toShort()

    /**@SelfDocumented */
    fun needShowCallButton() = tableInfo.showCallButton

    /**@SelfDocumented */
    fun getPayment() = tableInfo.payment
}