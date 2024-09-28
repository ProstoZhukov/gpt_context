package ru.tensor.sbis.crud4.view.datachange

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.crud3.view.datachange.ItemInsertedValidator

class ItemInsertedValidatorTest {
    private lateinit var validator: ItemInsertedValidator<String>

    @Before
    fun setUp() {
        validator = ItemInsertedValidator()
    }

    @Test
    fun `validate should throw exception when indexItemList is empty`() {
        // Arrange
        val indexItemList = emptyList<Pair<Long, String>>()
        val allItems = listOf("item1", "item2")

        // Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            validator.validate(indexItemList, allItems)
        }

        // Verify
        assertEquals("indexItemList не должен быть пустым", exception.message)
    }

    @Test
    fun `validate should throw exception when allItems is empty`() {
        // Arrange
        val indexItemList = listOf(1L to "item1", 2L to "item2")
        val allItems = emptyList<String>()

        // Act
        val exception = assertThrows(IllegalArgumentException::class.java) {
            validator.validate(indexItemList, allItems)
        }

        // Verify
        assertEquals("allItems не должен быть пустым", exception.message)
    }

    @Test
    fun `validate should not throw exception when indexItemList and allItems are not empty`() {
        // Arrange
        val indexItemList = listOf(1L to "item1", 2L to "item2")
        val allItems = listOf("item1", "item2", "item3")

        // Act
        validator.validate(indexItemList, allItems)

        // Verify
        // No exception should be thrown
    }
}