package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.UUID

/**
 * @author aa.gulevskiy
 */
class TableCircleTest {
    private val chairSpecHeight: Int = 10
    private val chairSpecWidth: Int = 10
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val extraWidth = 25
    private val x = 2
    private val y = 7
    private val chairMargin = 4

    private val tableSpec = HallSchemeSpecHolder.TableSpec(extraWidth, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, chairSpecWidth)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    private val tableCircle = TableCircle(
        UUID.randomUUID(), 123, "", 0, "",
        "", 0, x, y, 1, 1,
        tableSpec,
        chairSpec,
        billSpec,
        bookingSpec,
        assigneeSpec,
        chairMargin,
        tableInfo = TableInfo(1, 0.0, 0, 0, emptyList(), null)
    )

    private val rect = SchemeItemBounds(
        x, y,
        x + getSpecWidth() + 2 * defaultPadding,
        y + getSpecWidth() + 2 * defaultPadding
    )

    private fun getSpecWidth() = tableSpec.extraWidth + chairSpec.width

    @Test
    fun `item left bound`() {
        assertEquals(rect.left, tableCircle.rect.left)
    }

    @Test
    fun `item top bound`() {
        assertEquals(rect.top, tableCircle.rect.top)
    }

    @Test
    fun `item right bound`() {
        assertEquals(rect.right, tableCircle.rect.right)
    }

    @Test
    fun `item bottom bound`() {
        assertEquals(rect.bottom, tableCircle.rect.bottom)
    }

    @Test
    fun `added length = 0`() {
        assertEquals(0, tableCircle.addedLength)
    }
}