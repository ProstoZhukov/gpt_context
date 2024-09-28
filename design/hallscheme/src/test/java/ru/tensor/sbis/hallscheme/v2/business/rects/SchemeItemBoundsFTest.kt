package ru.tensor.sbis.hallscheme.v2.business.rects

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author aa.gulevskiy
 */
class SchemeItemBoundsFTest {
    private lateinit var schemeItemBoundsF: SchemeItemBoundsF

    @Test
    fun `width when X coordinates the same`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 0F), BoundsPointF(25F, 100F))
        assertEquals(0F, schemeItemBoundsF.width)
    }

    @Test
    fun `height when Y coordinates the same`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 150F), BoundsPointF(125F, 150F))
        assertEquals(0F, schemeItemBoundsF.height)
    }

    @Test
    fun `left equals left top X coordinate`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 0F), BoundsPointF(125F, 100F))
        assertEquals(25F, schemeItemBoundsF.left)
    }

    @Test
    fun `top equals left top Y coordinate`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 10F), BoundsPointF(125F, 100F))
        assertEquals(10F, schemeItemBoundsF.top)
    }

   @Test
    fun `right equals right bottom X coordinate`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 0F), BoundsPointF(125F, 100F))
        assertEquals(125F, schemeItemBoundsF.right)
    }

    @Test
    fun `bottom equals right bottom Y coordinate`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(25F, 10F), BoundsPointF(125F, 105F))
        assertEquals(105F, schemeItemBoundsF.bottom)
    }

    @Test
    fun `left after offset`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(0F, 0F), BoundsPointF(100F, 150F))
        schemeItemBoundsF.offset(11F,16F)
        assertEquals(11F, schemeItemBoundsF.left)
    }

    @Test
    fun `right after offset`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(0F, 0F), BoundsPointF(100F, 150F))
        schemeItemBoundsF.offset(11F,16F)
        assertEquals(111F, schemeItemBoundsF.right)
    }

    @Test
    fun `top after offset`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(0F, 0F), BoundsPointF(100F, 150F))
        schemeItemBoundsF.offset(11F,16F)
        assertEquals(16F, schemeItemBoundsF.top)
    }

    @Test
    fun `bottom after offset`() {
        schemeItemBoundsF = SchemeItemBoundsF(BoundsPointF(0F, 0F), BoundsPointF(100F, 150F))
        schemeItemBoundsF.offset(11F,16F)
        assertEquals(166F, schemeItemBoundsF.bottom)
    }
}