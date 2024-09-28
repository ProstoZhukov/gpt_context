package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel.OrderableItemTestModel
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBoundsF
import java.util.UUID

/**
 * @author aa.gulevskiy
 */
class OrderableItemTest {

    private lateinit var testItem: OrderableItem
    private val chairSpecHeight: Int = 10
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2

    @Before
    fun setUp() {
        testItem = OrderableItemTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            HallSchemeSpecHolder.TableSpec(25, cornerRadius, padding = defaultPadding),
            HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, 25),
            HallSchemeSpecHolder.BillSpec(20, 15, 3),
            HallSchemeSpecHolder.BookingSpec(50, 3),
            HallSchemeSpecHolder.AssigneeSpec(100)
        )
    }

    @Test
    fun `bill view X coordinate`() {
        assertEquals(40F, testItem.getBillViewX())
    }

    @Test
    fun `bill view Y coordinate`() {
        assertEquals(12F, testItem.getBillViewY())
    }

    @Test
    fun `info view width`() {
        assertEquals(20, testItem.getInfoViewWidth())
    }

    @Test
    fun `info view height`() {
        assertEquals(50, testItem.getInfoViewHeight())
    }

    @Test
    fun `correct right coordinate of the rect after item created `() {
        assertEquals(52, testItem.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created `() {
        assertEquals(57, testItem.rect.bottom)
    }

    @Test
    fun `correct vertical line size between corners without arcs`() {
        assertEquals(16F, testItem.verticalLineSize)
    }

    @Test
    fun `correct horizontal line size between corners without arcs`() {
        assertEquals(16F, testItem.horizontalLineSize)
    }

    @Test
    fun `correct bounds of the left top corner circle`() {
        val padding = defaultPadding.toFloat()
        val bounds = SchemeItemBoundsF(
            padding,
            padding,
            padding + cornerRadius * 2,
            padding + cornerRadius * 2
        )

        assertEquals(bounds, testItem.cornerCircleBounds)
    }

    @Test
    fun `correct left coordinate of the tabletop`() {
        assertEquals(17, testItem.getTableRectLeft())
    }

    @Test
    fun `correct top coordinate of the tabletop`() {
        assertEquals(22, testItem.getTableRectTop())
    }

    @Test
    fun `correct width of the tabletop`() {
        assertEquals(20F, testItem.tableTopWidth)
    }

    @Test
    fun `correct height of the tabletop`() {
        assertEquals(20F, testItem.tableTopHeight)
    }
}