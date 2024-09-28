package ru.tensor.sbis.crud4.view.datachange

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.crud3.view.datachange.ItemInserted
import ru.tensor.sbis.crud3.view.datachange.ItemInsertedValidator

class ItemInsertedTest {
    private lateinit var validator: ItemInsertedValidator<String>
    private lateinit var itemInserted: ItemInserted<String>

    @Before
    fun setUp() {
        validator = mockk(relaxed = true)
        itemInserted = ItemInserted(
            listOf(1L to "item1", 2L to "item2"),
            listOf("item1", "item2", "item3"),
            validator
        )
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements correctly`() {
        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 2), groups)
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements with offset correctly`() {
        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(10)

        // Verify
        assertEquals(listOf(11 to 2), groups)
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group non-consecutive elements correctly`() {
        // Arrange
        itemInserted = ItemInserted(
            listOf(1L to "item1", 3L to "item3"),
            listOf("item1", "item2", "item3"),
            validator
        )

        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 1, 3 to 1), groups)
    }

    @Test
    fun `init should validate indexItemList and allItems with validator`() {
        // Arrange
        val indexItemList = listOf(1L to "item1", 2L to "item2")
        val allItems = listOf("item1", "item2", "item3")

        // Act
        ItemInserted(indexItemList, allItems, validator)

        // Verify
        verify { validator.validate(indexItemList, allItems) }
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements correctly when there are multiple groups`() {
        // Arrange
        itemInserted = ItemInserted(
            listOf(1L to "item1", 2L to "item2", 4L to "item4", 5L to "item5"),
            listOf("item1", "item2", "item3", "item4", "item5"),
            validator
        )

        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 2, 4 to 2), groups)
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements correctly when there is only one element`() {
        // Arrange
        itemInserted = ItemInserted(
            listOf(1L to "item1"),
            listOf("item1"),
            validator
        )

        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 1), groups)
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements correctly when there are no consecutive elements`() {
        // Arrange
        itemInserted = ItemInserted(
            listOf(1L to "item1", 3L to "item3", 5L to "item5"),
            listOf("item1", "item2", "item3", "item4", "item5"),
            validator
        )

        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 1, 3 to 1, 5 to 1), groups)
    }

    @Test
    fun `toGroupsOfConsecutiveElements should group consecutive elements correctly when there are duplicate elements`() {
        // Arrange
        itemInserted = ItemInserted(
            listOf(1L to "item1", 2L to "item1", 3L to "item1"),
            listOf("item1", "item1", "item1"),
            validator
        )

        // Act
        val groups = itemInserted.toGroupsOfConsecutiveElements(0)

        // Verify
        assertEquals(listOf(1 to 3), groups)
    }
}