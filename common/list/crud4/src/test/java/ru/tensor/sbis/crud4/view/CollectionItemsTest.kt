package ru.tensor.sbis.crud4.view

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.crud3.view.CollectionItems

class CollectionItemsTest {

    val collectionItems = CollectionItems<Int>()

    @Before
    fun setUp() {
        collectionItems.add(listOf(Pair(0L, 1), Pair(1L, 2), Pair(2L, 3)))
    }

    @Test
    fun testSize() {
        assertEquals(3, collectionItems.size)
    }

    @Test
    fun testGetAllItems() {
        val result = collectionItems.getAllItems()

        //assert
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun testReset() {
        collectionItems.reset(listOf(4, 5, 6))

        //assert
        assertEquals(listOf(4, 5, 6), collectionItems.getAllItems())
    }

    @Test
    fun testRemove() {
        collectionItems.remove(listOf(0L, 1L))

        //assert
        assertEquals(listOf(2), collectionItems.getAllItems())
    }

    @Test
    fun testMove() {
        collectionItems.move(listOf(Pair(0L, 2L), Pair(1L, 2L)))

        //assert
        assertEquals(listOf(3, 1, 2), collectionItems.getAllItems())
    }

    @Test
    fun testAdd() {
        collectionItems.add(listOf(Pair(1L, 4), Pair(3L, 5)))

        //assert
        assertEquals(listOf(1, 4, 2, 5, 3), collectionItems.getAllItems())
    }

    @Test
    fun testReplace() {
        collectionItems.replace(listOf(Pair(0L, 4), Pair(2L, 5)))

        //assert
        assertEquals(listOf(4, 2, 5), collectionItems.getAllItems())
    }

    @Test
    fun testClear() {
        collectionItems.clear()

        //assert
        assertEquals(emptyList<Int>(), collectionItems.getAllItems())
    }
}