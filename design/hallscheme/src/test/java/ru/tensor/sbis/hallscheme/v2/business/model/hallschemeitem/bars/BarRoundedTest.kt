package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.ArcInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBoundsF
import java.util.UUID
import kotlin.math.pow

/**
 * @author aa.gulevskiy
 */
class BarRoundedTest {
    private var barRounded: BarRounded
    private val chairWidth = 25
    private val height = 98F
    private val chairSpecHeight: Int = 10
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25
    private val chairMargin = 3
    private val tableWidth = 50

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, chairWidth)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    init {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(0, 0.0, 0, 0, emptyList(), null)
        )
    }

    @Test
    fun `large arc info`() {
        val barSpecWidth = getBarSpecWidth()
        val radiusLargeCircle = getLargeCircleRadius(barSpecWidth)
        val barMiddleSpecWidth = barSpecWidth / 2
        val largeCircleBounds = getLargeCircleBounds(barMiddleSpecWidth, radiusLargeCircle)

        val sinus = (radiusLargeCircle - (height - cornerRadius)) / radiusLargeCircle
        val startAngleOfLargeArc = 180 - Math.toDegrees(Math.asin(sinus)).toFloat()
        val sweepAngleOfLargeArc = -(startAngleOfLargeArc - 90)

        assertEquals(
            ArcInfo(largeCircleBounds, startAngleOfLargeArc, sweepAngleOfLargeArc),
            barRounded.getMainLayerLargeArcInfo()
        )
    }

    private fun getBarSpecWidth() =
        extraWidth + (chairMargin + chairWidth) * 4 + extraWidth - chairMargin

    private fun getLargeCircleBounds(barMiddleSpecWidth: Int, radiusLargeCircle: Double): SchemeItemBoundsF {
        val largeCircleBounds = SchemeItemBoundsF(
            (barMiddleSpecWidth - radiusLargeCircle).toFloat(), (height - 2 * radiusLargeCircle).toFloat(),
            (barMiddleSpecWidth + radiusLargeCircle).toFloat(), height
        )

        largeCircleBounds.offset(barRounded.tablePadding.left.toFloat(), barRounded.tablePadding.top.toFloat())
        return largeCircleBounds
    }

    private fun getLargeCircleRadius(barSpecWidth: Int): Double {
        val a = height - cornerRadius
        val b = barSpecWidth.toDouble()

        // Решение квадратичного уравнения
        return a / 2 + b.pow(2.0) / (8 * a)
    }

    @Test
    fun `small arc info`() {
        val barSpecWidth = getBarSpecWidth()
        val radiusLargeCircle = getLargeCircleRadius(barSpecWidth)
        val barMiddleSpecWidth = barSpecWidth / 2
        val largeCircleBounds = getLargeCircleBounds(barMiddleSpecWidth, radiusLargeCircle)

        val smallCircleBounds = SchemeItemBoundsF(
            largeCircleBounds.left + tableWidth, largeCircleBounds.top + tableWidth,
            largeCircleBounds.right - tableWidth, largeCircleBounds.bottom - tableWidth
        )

        val radius = (smallCircleBounds.width / 2).toDouble()
        // арксинус от (отношение противолежащего катета к гипотенузе)

        val sinus = (getLargeCircleBounds(barMiddleSpecWidth, radiusLargeCircle).width / 2 - height) / radius
        val startAngleOfSmallArc = Math.toDegrees(Math.asin(sinus)).toFloat()
        val sweepAngleOfSmallCircle = 90 - startAngleOfSmallArc

        assertEquals(
            ArcInfo(smallCircleBounds, startAngleOfSmallArc, sweepAngleOfSmallCircle),
            barRounded.getMainLayerSmallArcInfo()
        )
    }

    @Test
    fun `depth arc info`() {
        val depth = 4
        barRounded.depth = depth

        val barSpecWidth = getBarSpecWidth()
        val a = height - depth - cornerRadius
        val b = barSpecWidth.toDouble()
        val radiusDepthCircle = a / 2 + b.pow(2.0) / (8 * a)
        val bottom = height - depth

        val barMiddleSpecWidth = barSpecWidth / 2
        val depthLayerCircleBounds = SchemeItemBoundsF(
            (barMiddleSpecWidth - radiusDepthCircle).toFloat(), bottom - 2 * radiusDepthCircle.toFloat(),
            (barMiddleSpecWidth + radiusDepthCircle).toFloat(), bottom
        )
        depthLayerCircleBounds.offset(barRounded.tablePadding.left.toFloat(), barRounded.tablePadding.top.toFloat())

        // арксинус от (отношение противолежащего катета к гипотенузе)
        val sinus = ((radiusDepthCircle - a) / radiusDepthCircle)
        val startAngleOfArc = Math.toDegrees(Math.asin(sinus)).toFloat()
        val sweepAngleOfArc = 90 - startAngleOfArc

        return assertEquals(
            ArcInfo(depthLayerCircleBounds, startAngleOfArc, sweepAngleOfArc),
            barRounded.getDepthArcInfo()
        )
    }

    @Test
    fun `chair type when number 5`() {
        assertEquals(
            ChairType.BOTTOM,
            barRounded.getChairType(5)
        )
    }

    @Test
    fun `chair type when number is greater than 5`() {
        assertEquals(
            ChairType.BOTTOM,
            barRounded.getChairType(7)
        )
    }

    @Test
    fun `info view width`() {
        assertEquals(tableWidth, barRounded.getInfoViewWidth())
    }

    @Test
    fun `info view height`() {
        assertEquals(tableWidth, barRounded.getInfoViewHeight())
    }

    @Test
    fun `bill view X coordinate when haven't rotated`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(187F, barRounded.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 180`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 2, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(177F, barRounded.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 90`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 1, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )
        barRounded.depth = 1

        assertEquals(136F, barRounded.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 270`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 3, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(136F, barRounded.getBillViewX())
    }

    @Test
    fun `bill view Y coordinate when haven't rotated`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(27f, barRounded.getBillViewY())
    }

    @Test
    fun `bill view Y coordinate when rotated to 180`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 2, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(27f, barRounded.getBillViewY())
    }

    @Test
    fun `bill view Y coordinate when rotated to 90`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 1, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )
        barRounded.depth = 1

        assertEquals(17f, barRounded.getBillViewY())
    }

    @Test
    fun `bill view Y coordinate when rotated to 270`() {
        barRounded = BarRounded(
            UUID.randomUUID(), 123, "", 3, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, chairMargin,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(
            7f,
            barRounded.getBillViewY()
        )
    }
}