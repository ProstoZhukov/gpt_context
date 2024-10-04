package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class MediaTest {

    private val media = Media(UUID.randomUUID(), 0, "", null, 0, 85,
        123, 0, 120, 80, 1f, "")

    @Test
    fun `correct left coordinate of the rect after item created`() {
        assertEquals(85, media.rect.left)
    }

    @Test
    fun `correct top coordinate of the rect after item created`() {
        assertEquals(123, media.rect.top)
    }

    @Test
    fun `correct right coordinate of the rect after item created`() {
        assertEquals(205, media.rect.right)
    }

    @Test
    fun `correct bottom coordinate of the rect after item created`() {
        assertEquals(203, media.rect.bottom)
    }
}