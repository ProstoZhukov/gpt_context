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
class BarSquareBracketTest {
    private var barSquareBracket: BarSquareBracket
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
        barSquareBracket = BarSquareBracket(
            UUID.randomUUID(), 123, "", 0, "",
            "", 7, 0, 2, 7, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            2f, 55F,
            tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
        )
    }

    @Test
    fun `chair bounds when number 1`() {
        val left = chairSpecHeight + 25
        val top = 120
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barSquareBracket.getChairBounds(1, false))
    }

    @Test
    fun `chair bounds when number 2`() {
        val left = chairSpecHeight + 25 + 1 * (25 + barSquareBracket.chairMargin)
        val top = 120
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barSquareBracket.getChairBounds(2, false))
    }

    @Test
    fun `chair bounds when number 6`() {
        val left = chairSpecHeight + 25 + 5 * (25 + barSquareBracket.chairMargin)
        val top = 120
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        Assert.assertEquals(chairBounds, barSquareBracket.getChairBounds(6, false))
    }

    @Test
    fun `char type res name when number 1`() {
        Assert.assertEquals(ChairType.BOTTOM, barSquareBracket.getChairType(1))
    }

    @Test
    fun `char type res name when number 2`() {
        Assert.assertEquals(ChairType.BOTTOM, barSquareBracket.getChairType(2))
    }

    @Test
    fun `char type res name when number 4`() {
        Assert.assertEquals(ChairType.BOTTOM, barSquareBracket.getChairType(4))
    }

    @Test
    fun `char type res name when number 777`() {
        Assert.assertEquals(ChairType.BOTTOM, barSquareBracket.getChairType(7))
    }
}