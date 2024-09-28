package ru.tensor.sbis.business.common.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionExtTest {

    @Test
    fun `containsAll vararg true`() {
        assertTrue(listOf(1, 2, 3).containsAll(3, 2, 1))
        assertTrue(listOf(1, 2, 3).containsAll(1, 2))
        assertTrue(listOf(1, 2, 3).containsAll(2, 3))
        assertTrue(listOf(1, 2, 3).containsAll(1))
    }

    @Test
    fun `containsAll vararg false`() {
        assertFalse(listOf(1, 2, 3).containsAll(1, 2, 5))
        assertFalse(listOf(1, 2, 3).containsAll(2, 5))
        assertFalse(listOf(1, 2, 3).containsAll(7))
        assertFalse(listOf(1, 2, 3).containsAll(7))
    }

    @Test
    fun `containsAny true`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertTrue(list.containsAny(listOf(1)))
        assertTrue(list.containsAny(listOf(3)))
        assertTrue(list.containsAny(listOf(2, 3, 5)))
        assertTrue(list.containsAny(listOf(7, 8, 99, 222, 333, 456, 4, 567)))
    }

    @Test
    fun `containsAny false`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertFalse(list.containsAny(emptySet()))
        assertFalse(list.containsAny(listOf(0)))
        assertFalse(list.containsAny(listOf(-1, 333, 556)))
        assertFalse(list.containsAny((6..100_000).toList()))
    }

    @Test
    fun `containsAny vararg true`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertTrue(list.containsAny(1))
        assertTrue(list.containsAny(3))
        assertTrue(list.containsAny(2, 3, 5))
        assertTrue(list.containsAny(7, 8, 99, 222, 333, 456, 4, 567))
    }

    @Test
    fun `containsAny vararg false`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertFalse(list.containsAny(emptyArray<Int>()))
        assertFalse(list.containsAny(0))
        assertFalse(list.containsAny(-1, 0, 556))
        assertFalse(list.containsAny((6..100_000).toList().toTypedArray()))
    }
}
