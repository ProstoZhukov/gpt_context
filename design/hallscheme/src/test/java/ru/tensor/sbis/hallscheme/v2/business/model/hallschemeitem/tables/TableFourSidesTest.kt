package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.ChairType
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class TableFourSidesTest {
    private val chairSpecHeight: Int = 10
    private val chairSpecWidth: Int = 15
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25
    private val tableWidth = 50
    private val tableHeight = 65
    private val x = 2
    private val y = 7
    private val chairMargin = 4

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, chairSpecWidth)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    @Test
    fun `set places 4 if places = 0`() {
        val table = getTableWithPlaces(0)
        assertEquals(4, table.tableInfo.totalPlaces)
    }

    @Test
    fun `added length when places 2`() {
        val places = 2
        val table = getTableWithPlaces(places)

        val addedLength = 0

        assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 3`() {
        val places = 3
        val table = getTableWithPlaces(places)

        val addedLength = 0

        assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 4`() {
        val places = 4
        val table = getTableWithPlaces(places)

        val addedLength = 0

        assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 5`() {
        val places = 5
        val table = getTableWithPlaces(places)

        val addedLength = (chairSpecWidth + chairMargin) * 1

        assertEquals(addedLength, table.addedLength)
    }

    @Test
    fun `added length when places 20`() {
        val places = 20
        val table = getTableWithPlaces(places)

        val addedLength = (chairSpecWidth + chairMargin) * 8

        assertEquals(addedLength, table.addedLength)
    }

    private fun getTableWithPlaces(places: Int): TableFourSides {
        return TableFourSides(
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
    fun `item left bound`() {
        val places = 1
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + tableWidth + 2 * chairSpecHeight,
            y + tableHeight + 2 * chairSpecHeight
        )

        assertEquals(rect.left, table.rect.left)
    }

    @Test
    fun `item top bound`() {
        val places = 1
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + tableWidth + 2 * chairSpecHeight,
            y + tableHeight + 2 * chairSpecHeight
        )

        assertEquals(rect.top, table.rect.top)
    }

    @Test
    fun `item right bound when places = 1`() {
        val places = 1
        val specWidth = getSpecWidth(chairSpecWidth + chairMargin)
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item bottom bound when places = 1`() {
        val places = 1
        val table = getTableWithPlaces(places)
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)

        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + 2 * defaultPadding,
            y + specWidth + 2 * defaultPadding
        )

        assertEquals(rect.bottom, table.rect.bottom)
    }

    @Test
    fun `item right bound when places = 4`() {
        val places = 4
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item bottom bound when places = 4`() {
        val places = 4
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)

        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + 2 * defaultPadding,
            y + specWidth + 2 * defaultPadding
        )

        assertEquals(rect.bottom, table.rect.bottom)
    }

    @Test
    fun `item right bound when places = 5`() {
        val places = 5
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = chairFactor * (places - 3) / 2
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + addedLength + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item right bound when places = 6`() {
        val places = 6
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = getAddedLength(chairFactor, places)
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + addedLength + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item right bound when places = 7`() {
        val places = 7
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = chairFactor * (places - 3) / 2
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + addedLength + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item right bound when places = 8`() {
        val places = 8
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = getAddedLength(chairFactor, places)
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + addedLength + 2 * defaultPadding,
            y + tableHeight + 2 * defaultPadding
        )

        assertEquals(rect.right, table.rect.right)
    }

    @Test
    fun `item right bound when places = 12`() {
        val places = 12
        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = getAddedLength(chairFactor, places)
        val table = getTableWithPlaces(places)
        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + addedLength + table.tablePadding.horizontal,
            y + tableHeight + table.tablePadding.vertical
        )

        assertEquals(rect.right, table.rect.right)
    }

    private fun getAddedLength(chairFactor: Int, places: Int) =
        chairFactor * ((places - 3) / 2)

    @Test
    fun `item bottom bound when places = 5`() {
        val places = 5
        val table = getTableWithPlaces(places)

        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)

        val rect = SchemeItemBounds(
            x, y,
            x + specWidth + 2 * defaultPadding,
            y + specWidth + 2 * defaultPadding
        )

        assertEquals(rect.bottom, table.rect.bottom)
    }

    @Test
    fun `chair type when chairNumber = 1 and places = 3`() {
        val chairNumber = 1
        val places = 3

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.TOP, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 2 and places = 3`() {
        val chairNumber = 2
        val places = 3

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.LEFT, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 3 and places = 3`() {
        val chairNumber = 3
        val places = 3

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.RIGHT, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 3 and places = 4`() {
        val chairNumber = 3
        val places = 4

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.LEFT, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 1 and places  is not equal  3`() {
        val chairNumber = 1
        val places = 2

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.TOP, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber = 2 and places  is not equal  3`() {
        val chairNumber = 2
        val places = 2

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.BOTTOM, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber is greater than 3 and even number`() {
        val chairNumber = 4
        val places = 4

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.RIGHT, table.getChairType(chairNumber))
    }

    @Test
    fun `chair type when chairNumber is greater than 3 and not even number`() {
        val chairNumber = 5
        val places = 5

        val table = getTableWithPlaces(places)

        assertEquals(ChairType.TOP, table.getChairType(chairNumber))
    }

    @Test
    fun `chair bounds when chair type LEFT and number = 2`() {
        val chairNumber = 2
        val chairType = ChairType.LEFT
        val table = getTableWithPlaces(4)

        val x = defaultPadding - chairSpecHeight
        val y = (tableHeight + x) / 2 - chairSpecWidth / 2
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecHeight, y + chairSpecWidth)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type LEFT and number = 5`() {
        val chairNumber = 5
        val chairType = ChairType.LEFT
        val table = getTableWithPlaces(5)

        val x = defaultPadding - chairSpecHeight
        val y = (tableHeight + x) / 2 - chairSpecWidth / 2
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecHeight, y + chairSpecWidth)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type TOP and number = 4`() {
        val chairNumber = 4
        val chairType = ChairType.TOP
        val table = getTableWithPlaces(4)

        val x = defaultPadding + extraWidth / 2
        val y = defaultPadding - chairSpecHeight
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type TOP and number = 5`() {
        val chairNumber = 5
        val chairType = ChairType.TOP
        val table = getTableWithPlaces(6)

        var x = defaultPadding + extraWidth / 2
        x += (chairNumber - 2) / 2 * (chairSpecWidth + chairMargin)
        val y = defaultPadding - chairSpecHeight
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type RIGHT and number = 2`() {
        val chairNumber = 2
        val chairType = ChairType.RIGHT
        val places = 4

        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = getAddedLength(chairFactor, places)
        val table = getTableWithPlaces(places)

        val x = defaultPadding + specWidth + addedLength
        val y = (tableHeight + defaultPadding - chairSpecHeight) / 2 - chairSpecWidth / 2
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecHeight, y + chairSpecWidth)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type RIGHT and number = 5`() {
        val chairNumber = 5
        val chairType = ChairType.RIGHT
        val places = 4

        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)
        val addedLength = getAddedLength(chairFactor, places)
        val table = getTableWithPlaces(places)

        val x = defaultPadding + specWidth + addedLength
        val y = (tableHeight + defaultPadding - chairSpecHeight) / 2 - chairSpecWidth / 2
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecHeight, y + chairSpecWidth)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    private fun getSpecWidth(chairFactor: Int) =
        (chairFactor + extraWidth.toFloat() / 2 + extraWidth.toFloat() / 2 - chairMargin).toInt()

    @Test
    fun `chair bounds when chair type BOTTOM and number = 5`() {
        val chairNumber = 5
        val chairType = ChairType.BOTTOM
        val table = getTableWithPlaces(4)

        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)

        val x = defaultPadding + extraWidth / 2
        val y = defaultPadding + specWidth
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }

    @Test
    fun `chair bounds when chair type BOTTOM and number = 6`() {
        val chairNumber = 6
        val chairType = ChairType.BOTTOM
        val table = getTableWithPlaces(6)

        val chairFactor = chairSpecWidth + chairMargin
        val specWidth = getSpecWidth(chairFactor)

        var x = defaultPadding + extraWidth / 2
        x += (chairNumber - 1 - 2) / 2 * (chairSpecWidth + chairMargin)
        val y = defaultPadding + specWidth
        val chairBounds = SchemeItemBounds(x, y, x + chairSpecWidth, y + chairSpecHeight)

        assertEquals(chairBounds, table.getChairBoundsByType(chairType, chairNumber, chairSpecHeight))
    }
}