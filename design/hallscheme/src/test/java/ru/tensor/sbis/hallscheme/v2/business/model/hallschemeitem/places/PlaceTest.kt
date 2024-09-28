package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.places

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import java.util.*

/**
 * @author aa.gulevskiy
 */
class PlaceTest {
    private val place = Place(UUID.randomUUID(), null, 0,
            "", null, 0, 185, 213, 0,
            HallSchemeSpecHolder.PlaceSpec(40, 70), 8)

    @Test
    fun `correct left coordinate of the rect after item created`() {
        assertEquals(185, place.rect.left)
    }

    @Test
    fun `correct top coordinate of the rect after item created`() {
        assertEquals(213, place.rect.top)
    }

    @Test
    fun `correct right coordinate of the rect after item created`() {
        assertEquals(225 + 2 * 8, place.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created`() {
        assertEquals(283 + 2 * 8, place.rect.bottom)
    }
}