package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shape

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape
import java.util.*

/**
 * @author aa.gulevskiy
 */
class ShapeTest {
    private val shape = Shape(
        UUID.randomUUID(), "", null, 0, 185,
        213, 0, null, null, null, 200, 350, 0F
    )

    @Test
    fun `correct left coordinate of the rect after item created`() {
        Assert.assertEquals(185, shape.rect.left)
    }

    @Test
    fun `correct top coordinate of the rect after item created`() {
        Assert.assertEquals(213, shape.rect.top)
    }

    @Test
    fun `correct right coordinate of the rect after item created`() {
        Assert.assertEquals(385, shape.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created`() {
        Assert.assertEquals(563, shape.rect.bottom)
    }

    @Test
    fun `set size to 1 when size is null`() {
        Assert.assertEquals(1, shape.size)
    }

    @Test
    fun `set size to 1 when size == 0`() {
        val shape = Shape(
            UUID.randomUUID(), "", null, 0, 185,
            213, 0, 0, "white", "red", 200, 350, 0F
        )
        Assert.assertEquals(1, shape.size)
    }

    @Test
    fun `set size to 1 when size is greater than 0`() {
        val shape = Shape(
            UUID.randomUUID(), "", null, 0, 185,
            213, 0, 5, "white", "red", 200, 350, 0F
        )
        Assert.assertEquals(5, shape.size)
    }

    @Test
    fun `set width to 0 when width is null`() {
        val shape = Shape(
            UUID.randomUUID(), "", null, 0, 185,
            213, 0, null, null, null, null, 350, 0F
        )

        Assert.assertEquals(0, shape.width)
    }

    @Test
    fun `set height to 0 when height is null`() {
        val shape = Shape(
            UUID.randomUUID(), "", null, 0, 185,
            213, 0, null, null, null, 200, null, 0F
        )

        Assert.assertEquals(0, shape.height)
    }

    @Test
    fun `set width to 0 when width is greater than 0`() {
        Assert.assertEquals(200, shape.width)
    }

    @Test
    fun `set height to 0 when height is greater than 0`() {
        Assert.assertEquals(350, shape.height)
    }

    @Test
    fun `set disposition to 0 when it's not equal 0`() {
        Assert.assertEquals(0, shape.disposition)
    }
}