package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class BarCornerTest {
    private var barCorner: BarCorner
    private val chairSpecHeight: Int = 10
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, 25)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    init {
        barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 0, "",
            "",  0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            1.5F, 55F,
            tableInfo = TableInfo(6, 0.0, 0, 0, emptyList(), null)
        )
    }

    @Test
    fun `correct top edge height`() {
        assertEquals(25F, barCorner.edgesHeightsDiff)
    }

    @Test
    fun `char type res name when number 1`() {
        assertEquals(ChairType.LEFT, barCorner.getChairType(1))
    }

    @Test
    fun `char type res name when number 2`() {
        assertEquals(ChairType.BOTTOM, barCorner.getChairType(2))
    }

    @Test
    fun `char type res name when number 4`() {
        assertEquals(ChairType.BOTTOM, barCorner.getChairType(4))
    }

    @Test
    fun `char type res name when number 777`() {
        assertEquals(ChairType.BOTTOM, barCorner.getChairType(6))
    }

    @Test
    fun `bill view X coordinate when haven't rotated`() {
        assertEquals(250F, barCorner.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 90 degrees (disposition 1)`() {
        barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 1, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, 55F,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(138F, barCorner.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 180 degrees (disposition 2)`() {
        barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 2, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, 55F,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(217F, barCorner.getBillViewX())
    }

    @Test
    fun `bill view X coordinate when rotated to 270 degrees (disposition 3)`() {
        barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 3, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, 55F,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(128F, barCorner.getBillViewX())
    }

    @Test
    fun `chair bounds when number 1`() {
        val left = defaultPadding - chairSpecHeight
        val top = (75 - (75 - 25) + extraWidth / 2 + defaultPadding)
        val chairBounds = SchemeItemBounds(left, top, left + chairSpecHeight, top + 25)
        assertEquals(chairBounds, barCorner.getChairBounds(1, false))
    }

    @Test
    fun `chair bounds when number 2`() {
        val left = defaultPadding + extraWidth
        val top = barCorner.height + defaultPadding
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        assertEquals(chairBounds, barCorner.getChairBounds(2, false))
    }

    @Test
    fun `chair bounds when number 5`() {
        val chairNumbersWithoutLeftAndFirstBottom = 3
        val left = defaultPadding + extraWidth + chairNumbersWithoutLeftAndFirstBottom * (25 + barCorner.chairMargin)
        val top = barCorner.height + defaultPadding
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        assertEquals(chairBounds, barCorner.getChairBounds(5, false))
    }

    @Test
    fun `info view height when haven't rotated`() {
        barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 2, 7, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2F, 55F,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )

        assertEquals(50, barCorner.getInfoViewHeight())
    }
}