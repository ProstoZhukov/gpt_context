package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel.HallSchemeItemTestModel
import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPoint
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.*

/**
 * @author aa.gulevskiy
 */
class HallSchemeItemTest {

    @Test
    fun `item rotation when disposition 0`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 0, "", null, 0, 0, 0, 0)
        assertEquals(0, testItem.itemRotation)
    }

    @Test
    fun `item rotation when disposition 1`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 1, "", null, 0, 0, 0, 0)
        assertEquals(90, testItem.itemRotation)
    }

    @Test
    fun `incorrect item rotation when disposition 1`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 1, "", null, 0, 0, 0, 0)
        assertNotEquals(0, testItem.itemRotation)
    }

    @Test
    fun `item rotation when disposition 3`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 3, "", null, 0, 0, 0, 0)
        assertEquals(270, testItem.itemRotation)
    }

    @Test
    fun `item rotation when disposition 5`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 5, "", null, 0, 0, 0, 0)
        assertEquals(90, testItem.itemRotation)
    }

    @Test
    fun `item rotation when disposition 26`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 26, "", null, 0, 0, 0, 0)
        assertEquals(180, testItem.itemRotation)
    }

    @Test
    fun `incorrect item rotation when disposition 26`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 26, "", null, 0, 0, 0, 0)
        assertNotEquals(270, testItem.itemRotation)
    }

    @Test
    fun `is horizontal when disposition 0`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 0, "", null, 0, 0, 0, 0)
        assert(testItem.isHorizontal)
    }

    @Test
    fun `is not horizontal when disposition 1`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 1, "", null, 0, 0, 0, 0)
        assert(!testItem.isHorizontal)
    }

    @Test
    fun `is horizontal when disposition greater than 2`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 52468, "", null, 0, 0, 0, 0)
        assert(testItem.isHorizontal)
    }

    @Test
    fun `is not horizontal when disposition greater than 2`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 25161, "", null, 0, 0, 0, 0)
        assert(!testItem.isHorizontal)
    }

    @Test
    fun `rotated rect right coordinate when disposition 0`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 0, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(50, testItem.rotatedRect.right)
    }

    @Test
    fun `rotated rect bottom coordinate  when disposition 2`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 2, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(180, testItem.rotatedRect.bottom)
    }

    @Test
    fun `rotated rect right coordinate when disposition 1`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 1, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(175, testItem.rotatedRect.right)
    }

    @Test
    fun `rotated rect bottom coordinate when disposition 3`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 3, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(55, testItem.rotatedRect.bottom)
    }

    @Test
    fun `rotated rect right bottom point when disposition 0`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 0, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(BoundsPoint(50, 180), testItem.getRightBottomPoint())
    }

    @Test
    fun `rotated rect right bottom point when disposition 5`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 5, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(BoundsPoint(175, 55), testItem.getRightBottomPoint())
    }

    @Test
    fun `rotated rect right bottom point when disposition 3`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 3, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertEquals(BoundsPoint(175, 55), testItem.getRightBottomPoint())
    }

    @Test
    fun `incorrect rotated rect right bottom point when disposition 3`() {
        val testItem = HallSchemeItemTestModel(UUID.randomUUID(), 123, 3, "", null, 0, 0, 0, 0,
                SchemeItemBounds(BoundsPoint(10, 15), BoundsPoint(50, 180)))
        assertNotEquals(BoundsPoint(50, 180), testItem.getRightBottomPoint())
    }
}