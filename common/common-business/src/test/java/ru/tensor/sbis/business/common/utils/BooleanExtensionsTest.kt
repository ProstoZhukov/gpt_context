package ru.tensor.sbis.business.common.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BooleanExtTest {

    @Test
    fun `orTrue not null`() {
        assertTrue(true.orTrue())
        assertFalse(false.orTrue())
    }

    @Test
    fun `orTrue null`() {
        assertTrue((null as Boolean?).orTrue())
    }

    @Test
    fun `orFalse not null`() {
        assertTrue(true.orFalse())
        assertFalse(false.orFalse())
    }

    @Test
    fun `orFalse null`() {
        assertFalse((null as Boolean?).orFalse())
    }
}
