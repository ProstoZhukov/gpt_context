package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows

import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class RowTest {

    @Test
    fun `item coordinates when placeTo is greater than placeFrom`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 1, 10, 2, 5,
                false, false, 50, 5)

        val width = 10 * 50 + 9 * 5
        val height = 5 + 4 * 50 + 3 * 5 + 5

        assertEquals(SchemeItemBounds(20, 50, 20 + width, 50 + height),
                row.rect)
    }

    @Test
    fun `item coordinates when placeFrom is greater than placeTo`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 2, 5,
                false, false, 50, 5)

        val width = 10 * 50 + 9 * 5
        val height = 5 + 4 * 50 + 3 * 5 + 5

        assertEquals(SchemeItemBounds(20, 50, 20 + width, 50 + height),
                row.rect)
    }

    @Test
    fun `item coordinates when rowFrom is greater than rowTo`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 7, -4,
                false, false, 50, 5)

        val width = 10 * 50 + 9 * 5
        val height = 5 + 12 * 50 + 11 * 5 + 5

        assertEquals(SchemeItemBounds(20, 50, 20 + width, 50 + height),
                row.rect)
    }

    @Test
    fun `places count is correct when placesFrom is greater than placesTo`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 7, -4,
                true, false, 50, 5)

        assertEquals(10, row.items[0].size)
    }

    @Test
    fun `places count is correct when placesTo is greater than placesFrom`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 4, 20, 7, -4,
                true, true, 50, 5)

        assertEquals(17, row.items[0].size)
    }

    @Test
    fun `rows count is correct when rowsFrom is greater than rowsTo`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 7, -4,
                false, true, 50, 5)

        assertEquals(12, row.items.size)
    }

    @Test
    fun `rows count is correct when rowsTo is greater than rowsFrom`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 1, 1500,
                false, true, 50, 5)

        assertEquals(1500, row.items.size)
    }

    @Test
    fun `place element is correct`() {
        val row = Row(UUID.randomUUID(), null, 0, "row", null,
                10, 20, 50, 0, 6, -3, 10, 1500,
                true, false, 50, 5)

        assertEquals(RowPlace("21", "1"), row.items[11][5])
    }
}