package ru.tensor.sbis.common.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IterableExtensionsTest {

    private data class DataTest(val s: String, val i: Int, val d: Double)

    @Test
    fun `allItemsAreEquals empty true`() {
        assertTrue(emptyList<String>().allItemsAreEquals(true))
        assertTrue(emptyList<List<Int>>().allItemsAreEquals(true))
        assertTrue(emptySet<DataTest>().allItemsAreEquals(true))
    }

    @Test
    fun `allItemsAreEquals empty false`() {
        assertFalse(emptyList<String>().allItemsAreEquals())
        assertFalse(emptyList<List<Int>>().allItemsAreEquals())
        assertFalse(emptySet<DataTest>().allItemsAreEquals())
    }

    @Test
    fun `allItemsAreEquals one true`() {
        assertTrue(listOf("Str").allItemsAreEquals())
        assertTrue(listOf("Str").allItemsAreEquals(false))
        assertTrue(listOf(222).allItemsAreEquals(false))
        assertTrue(
            listOf(
                DataTest(
                    "t",
                    1,
                    22.7
                )
            ).allItemsAreEquals(false)
        )
    }

    @Test
    fun `allItemsAreEquals same`() {
        val data = DataTest("hello", 222, 17.11)

        val items1 = listOf("str", "str", "str", "str")
        val items2 = listOf(42, 42, 42, 42, 42, 42, 42, 42)
        val items3 = listOf(42, 42, 42, 42, 42, 42, 42, 42)
        val items4 = listOf(data.copy(), data.copy(), data.copy())

        assertTrue(items1.allItemsAreEquals())
        assertTrue(items2.allItemsAreEquals())
        assertTrue(items3.allItemsAreEquals())
        assertTrue(items4.allItemsAreEquals())
    }

    @Test
    fun `allItemsAreEquals different`() {
        val data = DataTest("hello", 222, 17.11)

        val items1 = listOf("str", "str", "STR@@", "str")
        val items2 = listOf(42, 42, 42, 42, 99, 42, 42, 42)
        val items3 = listOf(-222, 42, 42, 42, 42, 42, 42, 42)
        val items4 = listOf(data.copy(s = "omg"), data.copy(), data.copy())

        assertFalse(items1.allItemsAreEquals())
        assertFalse(items2.allItemsAreEquals())
        assertFalse(items3.allItemsAreEquals())
        assertFalse(items4.allItemsAreEquals())
    }

    @Test
    fun `allItemsAreEqualsBy empty true`() {
        assertTrue(emptyList<String>().allItemsAreEqualsBy(true) { it })
        assertTrue(emptyList<List<Int>>().allItemsAreEqualsBy(true) { it })
        assertTrue(emptySet<DataTest>().allItemsAreEqualsBy(true) { it.d })
    }

    @Test
    fun `allItemsAreEqualsBy empty false`() {
        assertFalse(emptyList<String>().allItemsAreEqualsBy { it })
        assertFalse(emptyList<List<Int>>().allItemsAreEqualsBy { it })
        assertFalse(emptySet<DataTest>().allItemsAreEqualsBy { it.d })
    }

    @Test
    fun `allItemsAreEqualsBy one true`() {
        assertTrue(listOf("Str").allItemsAreEqualsBy { it })
        assertTrue(listOf("Str").allItemsAreEqualsBy { it })
        assertTrue(listOf(222).allItemsAreEqualsBy(false) { it })
        assertTrue(listOf(DataTest("t", 1, 22.7)).allItemsAreEqualsBy { it.s })
    }

    @Test
    fun `allItemsAreEqualsBy same`() {
        val items1 = listOf("str", "str", "str", "str")
        val items2 = listOf(42, 42, 42, 42, 42, 42, 42, 42)
        val items3 = listOf(42, 42, 42, 42, 42, 42, 42, 42)

        assertTrue(items1.allItemsAreEqualsBy { it })
        assertTrue(items2.allItemsAreEqualsBy { it })
        assertTrue(items3.allItemsAreEqualsBy { it })

        val data = DataTest("hello", 222, 17.11)
        val items4 = listOf(data.copy(), data.copy(), data.copy())

        assertTrue(items4.allItemsAreEqualsBy { it })
        assertTrue(items4.allItemsAreEqualsBy { it.s })
        assertTrue(items4.allItemsAreEqualsBy { it.i })
        assertTrue(items4.allItemsAreEqualsBy { it.d })
    }

    @Test
    fun `allItemsAreEqualsBy different`() {
        val items1 = listOf("str", "str", "STR@@", "str")
        val items2 = listOf(42, 42, 42, 42, 99, 42, 42, 42)
        val items3 = listOf(-222, 42, 42, 42, 42, 42, 42, 42)

        assertFalse(items1.allItemsAreEqualsBy { it })
        assertFalse(items2.allItemsAreEqualsBy { it })
        assertFalse(items3.allItemsAreEqualsBy { it })

        val data = DataTest("hello", 222, 17.11)
        val items4 = listOf(data.copy(s = "omg"), data.copy(), data.copy())

        assertFalse(items4.allItemsAreEqualsBy { it })
        assertFalse(items4.allItemsAreEqualsBy { it.s })
        assertTrue(items4.allItemsAreEqualsBy { it.i })
        assertTrue(items4.allItemsAreEqualsBy { it.d })
    }

    @Test
    fun `allItemsAreEqualsBy same string length`() {
        assertTrue(listOf("cat", "dog", "bat").allItemsAreEqualsBy { it.length })
        assertTrue(listOf("cats", "dogs", "bats").allItemsAreEqualsBy { it.length })
    }

    @Test
    fun `allItemsAreEqualsBy different string length`() {
        assertFalse(listOf("cat", "dog", "ox").allItemsAreEqualsBy { it.length })
        assertFalse(listOf("cats", "dog", "bats").allItemsAreEqualsBy { it.length })
    }
}
