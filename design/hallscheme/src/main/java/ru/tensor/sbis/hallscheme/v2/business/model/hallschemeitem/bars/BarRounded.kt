package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.TablePadding
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.ArcInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPoint
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPointF
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBoundsF
import java.util.UUID
import kotlin.math.asin
import kotlin.math.pow

/**
 * Расстояние между стульями в градусах.
 */
private const val CHAIR_ANGLE_INTERVAL = 36F

/**
 * Полукруглый бар.
 * type = 101
 * (___)
 * @author aa.gulevskiy
 */
internal class BarRounded constructor(
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
    chairMargin: Int = getDefaultMargin(tableSpec, CHAIR_MARGIN_DIVIDER),
    tableInfo: TableInfo
) : Bar(
    id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
    tableSpec, chairSpec, billSpec, bookingSpec, assigneeSpec, chairMargin = chairMargin,
    tableInfo = tableInfo
) {

    override val height = (tableSpecWidth * barHeightFactor).toInt() - tableCornerRadius

    override val tablePadding = TablePadding(
        left = when (itemRotation) {
            180, 270 -> (defaultPadding * barHeightFactor).toInt()
            else -> (chairSpecHeight * barHeightFactor).toInt()
        },
        top = (defaultPadding * barHeightFactor).toInt(),
        right = when (itemRotation) {
            180, 270 -> chairSpecHeight
            else -> (chairSpecHeight * barHeightFactor).toInt()
        },
        bottom = (defaultPadding * barHeightFactor).toInt()
    )

    private val heightFloat = height.toFloat()
    private val middleSpecWidth: Int = specWidth / 2

    private var radiusLargeCircle = 0.0

    private lateinit var largeCircleBounds: SchemeItemBoundsF
    private lateinit var smallCircleBounds: SchemeItemBoundsF

    override val indexOfFirstHorizontalChair = 5
    override val firstHorizontalChairX =
        tablePadding.left + (rect.width - tablePadding.horizontal - addedLength) / 2 + chairSpecHeight

    init {
        calculateLargeCircleBounds()
        calculateSmallCircle()
    }

    private fun calculateLargeCircleBounds() {
        val a = heightFloat - tableCornerRadius
        val b = specWidth.toDouble()
        // Решение квадратичного уравнения
        radiusLargeCircle = a / 2 + b.pow(2.0) / (8 * a)
        largeCircleBounds = SchemeItemBoundsF(
            BoundsPointF(
                (middleSpecWidth - radiusLargeCircle).toFloat(),
                (tableRect.height - 2 * radiusLargeCircle).toFloat()
            ),
            BoundsPointF(
                (middleSpecWidth + radiusLargeCircle).toFloat(),
                tableRect.height.toFloat()
            )
        )

        largeCircleBounds.offset(tablePadding.left.toFloat(), tablePadding.top.toFloat())
    }

    private fun calculateSmallCircle() {
        smallCircleBounds = SchemeItemBoundsF(
            largeCircleBounds.left + tableSpecWidth, largeCircleBounds.top + tableSpecWidth,
            largeCircleBounds.right - tableSpecWidth, largeCircleBounds.bottom - tableSpecWidth
        )
    }

    /**
     * Возвращает данные для длинной (внешней) дуги бара.
     */
    fun getMainLayerLargeArcInfo(): ArcInfo {
        // арксинус от (отношение противолежащего катета к гипотенузе)
        val sinus = (radiusLargeCircle - (tableRect.height - tableCornerRadius)) / radiusLargeCircle
        val startAngleOfLargeArc = 180 - Math.toDegrees(asin(sinus)).toFloat()
        val sweepAngleOfLargeArc = -(startAngleOfLargeArc - 90)
        return ArcInfo(largeCircleBounds, startAngleOfLargeArc, sweepAngleOfLargeArc)
    }

    /**
     * Возвращает данные для короткой (внутренней) дуги бара.
     */
    fun getMainLayerSmallArcInfo(): ArcInfo {
        val radius = (smallCircleBounds.width / 2).toDouble()
        // арксинус от (отношение противолежащего катета к гипотенузе)
        val sinus = (largeCircleBounds.width / 2 - tableRect.height) / radius
        val startAngleOfSmallArc = Math.toDegrees(asin(sinus)).toFloat()
        val sweepAngleOfSmallCircle = 90 - startAngleOfSmallArc
        return ArcInfo(smallCircleBounds, startAngleOfSmallArc, sweepAngleOfSmallCircle)
    }

    /**
     * Возвращает данные для дуги "глубины" столешницы бара.
     */
    fun getDepthArcInfo(): ArcInfo {
        val a = heightFloat - depth - tableCornerRadius
        val b = specWidth.toDouble()
        val radiusDepthCircle = a / 2 + b.pow(2.0) / (8 * a)
        val bottom = tableRect.height - depth

        val depthLayerCircleBounds = SchemeItemBoundsF(
            (middleSpecWidth - radiusDepthCircle).toFloat(), bottom - 2 * radiusDepthCircle.toFloat(),
            (middleSpecWidth + radiusDepthCircle).toFloat(), bottom.toFloat()
        )
        depthLayerCircleBounds.offset(tablePadding.left.toFloat(), tablePadding.top.toFloat())

        // арксинус от (отношение противолежащего катета к гипотенузе)
        val sinus = ((radiusDepthCircle - a) / radiusDepthCircle)
        val startAngleOfArc = Math.toDegrees(asin(sinus)).toFloat()
        val sweepAngleOfArc = 90 - startAngleOfArc

        return ArcInfo(depthLayerCircleBounds, startAngleOfArc, sweepAngleOfArc)
    }

    override fun getChairAngle(chairNumber: Int): Float =
        when (chairNumber) {
            1 -> CHAIR_ANGLE_INTERVAL * 1.5F
            2 -> CHAIR_ANGLE_INTERVAL / 2
            3 -> 360 - CHAIR_ANGLE_INTERVAL / 2
            4 -> 360 - CHAIR_ANGLE_INTERVAL * 1.5F
            else -> 0F
        }

    override fun getChairBounds(chairNumber: Int, fullHeight: Boolean): SchemeItemBounds {
        val chairHeight = if (fullHeight) chairSpecFullHeight else chairSpecHeight
        return when (chairNumber) {
            1, 2 -> {
                val chairX = tablePadding.left + (tableRect.width - addedLength) / 2 - chairSpecWidth / 2
                val chairY = getBottomChairPosition(chairHeight)
                SchemeItemBounds(
                    BoundsPoint(chairX, chairY),
                    BoundsPoint(chairX + chairSpecWidth, chairY + chairHeight)
                )
            }
            3, 4 -> {
                val chairX = tablePadding.left + (tableRect.width + addedLength) / 2 - chairSpecWidth / 2
                val chairY = getBottomChairPosition(chairHeight)
                SchemeItemBounds(
                    BoundsPoint(chairX, chairY),
                    BoundsPoint(chairX + chairSpecWidth, chairY + chairHeight)
                )
            }
            else -> {
                getHorizontalChairBounds(chairNumber, chairHeight)
            }
        }
    }
    override fun getChairType(chairNumber: Int): ChairType = ChairType.BOTTOM

    override fun getAdditionalBillOffset(): Int =
        when (itemRotation) {
            180 -> -defaultPadding * 2
            else -> 0
        }

    override fun getBookingExtraHorizontalOffset(): Float =
        when (itemRotation) {
            0 -> defaultPaddingF * 2
            90 -> defaultPaddingF
            else -> 0f
        }

    override fun getBookingExtraVerticalOffset(): Float =
        when (itemRotation) {
            0, 90 -> -chairSpecHeight.toFloat()
            else -> 0f
        }

    override fun getInfoViewYCanvas(): Float =
        when (itemRotation) {
            0 -> tablePadding.top + edgesHeightsDiff
            90 -> tablePadding.left + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left
            180 -> tablePadding.bottom.toFloat() + depth
            else -> tablePadding.right + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.right
        }

    override fun getInfoViewXCanvas(): Float =
        when (itemRotation) {
            90 -> tablePadding.bottom.toFloat() + depth
            180 -> tablePadding.right + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left
            270 -> tablePadding.top + height - tableSpecWidth
            else -> tablePadding.left + tableTopWidth / 2 - tableSpecWidth / 2 - occupationOffset.left
        }
}