package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import org.junit.Assert
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.text.TextItem
import java.util.*

/**
 * @author aa.gulevskiy
 */
class TextItemTest {
    private val textItem = TextItem(UUID.randomUUID(), 3, "", null, 0,
            185, 213, 0, null, null, 50, 50, 0F,0)

    @Test
    fun `set size to 1 when size is null`() {
        Assert.assertEquals(20, textItem.size)
    }

    @Test
    fun `set size to 1 when size == 0`() {
        val textItem = TextItem(UUID.randomUUID(), 3, "", null, 0, 185,
                213, 0, null, null, 50, 50, 0F,0)

        Assert.assertEquals(20, textItem.size)
    }

    @Test
    fun `set size to 1 when size is greater than 0`() {
        val textItem = TextItem(UUID.randomUUID(), 3, "", null, 0, 185,
                213, 0, 5, null, 50, 50, 0F,0)

        Assert.assertEquals(5, textItem.size)
    }

    @Test
    fun `correct left coordinate of the rect after item created`() {
        Assert.assertEquals(185, textItem.rect.left)
    }

    @Test
    fun `correct top coordinate of the rect after item created`() {
        Assert.assertEquals(213, textItem.rect.top)
    }

    @Test
    fun `correct right coordinate of the rect after item created`() {
        Assert.assertEquals(235, textItem.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created`() {
        Assert.assertEquals(233, textItem.rect.bottom)
    }
}