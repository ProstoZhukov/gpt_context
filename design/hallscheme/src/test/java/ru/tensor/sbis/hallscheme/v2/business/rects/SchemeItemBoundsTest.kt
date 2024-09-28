package ru.tensor.sbis.hallscheme.v2.business.rects

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * @author aa.gulevskiy
 */
class SchemeItemBoundsTest {

    private lateinit var schemeItemBounds: SchemeItemBounds

    @Before
    fun setUp() {
        schemeItemBounds = SchemeItemBounds()
    }

    @Test
    fun `width when X coordinates the same`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 0), BoundsPoint(25, 100))
        assertEquals(0, schemeItemBounds.width)
    }

    @Test
    fun `height when Y coordinates the same`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 150), BoundsPoint(125, 150))
        assertEquals(0, schemeItemBounds.height)
    }

    @Test
    fun `X coordinate when created with empty constructor`() {
        schemeItemBounds = SchemeItemBounds()
        assertEquals(0, schemeItemBounds.leftTop.x)
    }

    @Test
    fun `Y coordinate when created with empty constructor`() {
        schemeItemBounds = SchemeItemBounds()
        assertEquals(0, schemeItemBounds.leftTop.y)
    }

    @Test
    fun `left equals left top X coordinate`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 0), BoundsPoint(125, 100))
        assertEquals(25, schemeItemBounds.left)
    }

    @Test
    fun `top equals left top Y coordinate`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 10), BoundsPoint(125, 100))
        assertEquals(10, schemeItemBounds.top)
    }

    @Test
    fun `right equals right bottom X coordinate`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 0), BoundsPoint(125, 100))
        assertEquals(125, schemeItemBounds.right)
    }

    @Test
    fun `bottom equals right bottom Y coordinate`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(25, 10), BoundsPoint(125, 105))
        assertEquals(105, schemeItemBounds.bottom)
    }

    @Test
    fun `right and bottom coordinates after rotation`() {
        schemeItemBounds = SchemeItemBounds(BoundsPoint(0, 50), BoundsPoint(120, 300))
        val itemAfterRotation = schemeItemBounds.rotateTo90()
        assertEquals(250,itemAfterRotation.right)
        assertEquals(170,itemAfterRotation.bottom)
    }
}