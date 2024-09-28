package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class BarLineTest {
    private var barLine: BarLine
    private val chairSpecHeight: Int = 10
    private val chairSpecWidth: Int = 15
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, chairSpecWidth)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    init {
        barLine = BarLine(
            UUID.randomUUID(), 123, "", 0, "",
            "", 6, 0, 2, 7, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            60,
            tableInfo = TableInfo(6, 0.0, 0, 0, emptyList(), null)
        )
    }

    @Test
    fun `set places 0 if places = 0`() {
        val barCorner = BarCorner(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 0, 2, 7, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            1.5F, 55F,
            tableInfo = TableInfo(0, 0.0, 0, 0, emptyList(), null)
        )
        Assert.assertEquals(0, barCorner.tableInfo.totalPlaces)
    }

    @Test
    fun `get places if places != 0`() {
        Assert.assertEquals(6, barLine.tableInfo.totalPlaces)
    }

    @Test
    fun `chair bounds when number 1`() {
        val left = extraWidth + chairSpecHeight
        val top = 50
        val chairBounds = SchemeItemBounds(left, top, left + chairSpecWidth, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barLine.getChairBounds(1, false))
    }

    @Test
    fun `chair bounds when number 2`() {
        val left = 1 * (50 + barLine.chairMargin)
        val top = 50
        val chairBounds = SchemeItemBounds(left, top, left + chairSpecWidth, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barLine.getChairBounds(2, false))
    }

    @Test
    fun `chair bounds when number 6`() {
        val left = 410
        val top = 50
        val chairBounds = SchemeItemBounds(left, top, left + chairSpecWidth, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barLine.getChairBounds(6, false))
    }

    @Test
    fun `char type res name when number 1`() {
        Assert.assertEquals(ChairType.BOTTOM, barLine.getChairType(1))
    }

    @Test
    fun `char type res name when number 2`() {
        Assert.assertEquals(ChairType.BOTTOM, barLine.getChairType(2))
    }

    @Test
    fun `char type res name when number 4`() {
        Assert.assertEquals(ChairType.BOTTOM, barLine.getChairType(4))
    }

    @Test
    fun `char type res name when number 777`() {
        Assert.assertEquals(ChairType.BOTTOM, barLine.getChairType(6))
    }
}