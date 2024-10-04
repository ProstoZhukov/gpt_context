package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.UUID

/**
 * @author ve.arefev
 */
class TableOvalTest {
    private val chairSpecHeight: Int = 10
    private val chairSpecWidth: Int = 15
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25
    private val x = 2
    private val y = 7
    private val chairMargin = 4
    private val chairFactor = chairSpecWidth + chairMargin
    private val tableSpecWidth = (extraWidth.toFloat() / 2 + chairFactor + (extraWidth.toFloat() / 2 - chairMargin)).toInt()

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, chairSpecWidth)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    private fun getTableWithPlaces(places: Int): TableOval {
        return TableOval(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, x, y, 1, 1,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec,
            chairMargin,
            tableInfo = TableInfo(places, 0.0, 0, 0, emptyList(), null)
        )
    }

    @Test
    fun `added length when places 2`() {
        val places = 2
        val table = getTableWithPlaces(places)

        val addedLength = 0

        Assert.assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 3`() {
        val places = 3
        val table = getTableWithPlaces(places)

        val addedLength = chairFactor

        Assert.assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 4`() {
        val places = 4
        val table = getTableWithPlaces(places)

        val addedLength = chairFactor

        Assert.assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 5`() {
        val places = 5
        val table = getTableWithPlaces(places)

        val addedLength = chairFactor * 2

        Assert.assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 20`() {
        val places = 20
        val table = getTableWithPlaces(places)

        val addedLength = chairFactor * 9

        Assert.assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `chair type when chairNumber = 1`() {
        val chairNumber = 1
        val places = 3

        val table = getTableWithPlaces(places)

        Assert.assertEquals(ChairType.TOP, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 2`() {
        val chairNumber = 2
        val places = 3

        val table = getTableWithPlaces(places)

        Assert.assertEquals(ChairType.BOTTOM, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 3`() {
        val chairNumber = 3
        val places = 10

        val table = getTableWithPlaces(places)

        Assert.assertEquals(ChairType.TOP, table.getChairType(chairNumber))
    }

    @Test
    fun `chair bounds when number = 1`() {
        val chairNumber = 1
        val table = getTableWithPlaces(4)

        val x = defaultPadding + (tableSpecWidth - chairSpecWidth) / 2
        val y = defaultPadding - chairSpecHeight
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        Assert.assertEquals(chairBounds, table.getChairBounds(chairNumber, false))
    }

    @Test
    fun `chair bounds when number = 2`() {
        val chairNumber = 2
        val table = getTableWithPlaces(4)

        val specWidth = (chairFactor + extraWidth.toFloat() / 2 + extraWidth.toFloat() / 2 - chairMargin).toInt()

        val x = defaultPadding + tableSpec.extraWidth / 2
        val y = defaultPadding + specWidth

        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        Assert.assertEquals(chairBounds, table.getChairBounds(chairNumber, false))
    }
}