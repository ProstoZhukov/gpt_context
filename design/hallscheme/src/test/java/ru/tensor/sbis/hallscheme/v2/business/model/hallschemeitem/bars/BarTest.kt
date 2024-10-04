package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars

import org.junit.Test

import org.junit.Assert.*
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel.BarTestModel
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class BarTest {
    private var barTestModel: BarTestModel
    private val chairSpecHeight: Int = 10
    private val chairSpecFullHeight: Int = 20
    private val defaultPadding = 15
    private val cornerRadius = 2
    private val rect: SchemeItemBounds

    private val tableSpec = HallSchemeSpecHolder.TableSpec(25, cornerRadius, padding = defaultPadding)
    private val chairSpec = HallSchemeSpecHolder.ChairSpec(chairSpecHeight, chairSpecFullHeight, 25)
    private val billSpec = HallSchemeSpecHolder.BillSpec(20, 15, 6)
    private val bookingSpec = HallSchemeSpecHolder.BookingSpec(50, 3)
    private val assigneeSpec = HallSchemeSpecHolder.AssigneeSpec(100)

    init {
        barTestModel = BarTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 0, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec
        )


        val x = 2
        val y = 7
        val itemSpecExtraWidth = 25

        rect = SchemeItemBounds(
            x, y,
            x + (25 + 25 / 13) * 4 + barTestModel.addedLength + itemSpecExtraWidth +
                    (itemSpecExtraWidth - barTestModel.chairMargin) + defaultPadding * 2,
            y + 50 + defaultPadding * 2
        )
    }

    @Test
    fun `added length when places = 0`() {
        assertEquals(0, barTestModel.addedLength)
    }

    @Test
    fun `added length when places is less than 4`() {
        barTestModel = BarTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 3, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec
        )

        assertEquals(0, barTestModel.addedLength)
    }

    @Test
    fun `added length when places = 4`() {
        barTestModel = BarTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 4, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec
        )

        assertEquals(0, barTestModel.addedLength)
    }

    @Test
    fun `added length when places is greater than 7`() {
        barTestModel = BarTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 7, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec
        )

        assertEquals(114, barTestModel.addedLength)
    }


    @Test
    fun `correct rect right coordinate`() {
        assertEquals(221, barTestModel.rect.right)
    }

    @Test
    fun `correct bottom right coordinate`() {
        assertEquals(rect.bottom, barTestModel.rect.bottom)
    }

    @Test
    fun `chair bounds when number 1`() {
        val left = chairSpecHeight + 25
        val top = barTestModel.height + defaultPadding
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        assertEquals(chairBounds, barTestModel.getChairBounds(1, false))
    }

    @Test
    fun `chair bounds when number 5`() {
        barTestModel = BarTestModel(
            UUID.randomUUID(), 123, "", 0, "",
            "", 6, 0, 2, 7, 1, 1, 0, emptyList(),
            0.0, 0, 0,
            tableSpec,
            chairSpec,
            billSpec,
            bookingSpec,
            assigneeSpec
        )

        val left = chairSpecHeight + 25 + 4 * (25 + barTestModel.chairMargin)
        val top = barTestModel.height + defaultPadding
        val chairBounds = SchemeItemBounds(left, top, left + 25, top + chairSpecHeight)
        assertEquals(chairBounds, barTestModel.getChairBounds(5, false))
    }
}