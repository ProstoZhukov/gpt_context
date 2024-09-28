package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import java.util.*

/**
 * @author aa.gulevskiy
 */
class DecorTest {

    private val decor = Decor(UUID.randomUUID(), 0, "", null, 0, 185,
            213, 0, HallSchemeSpecHolder.DecorSpec(40, 70),0F)

    @Test
    fun `correct left coordinate of the rect after item created`() {
        assertEquals(185, decor.rect.left)
    }

    @Test
    fun `correct top coordinate of the rect after item created`() {
        assertEquals(213, decor.rect.top)
    }

    @Test
    fun `correct right coordinate of the rect after item created`() {
        assertEquals(225, decor.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created`() {
        assertEquals(283, decor.rect.bottom)
    }
}