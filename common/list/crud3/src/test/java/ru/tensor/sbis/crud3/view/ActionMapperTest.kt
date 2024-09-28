package ru.tensor.sbis.crud3.view

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.datachange.ItemChanged
import ru.tensor.sbis.crud3.view.datachange.ItemInserted
import ru.tensor.sbis.crud3.view.datachange.ItemMoved
import ru.tensor.sbis.crud3.view.datachange.ItemRemoved
import ru.tensor.sbis.crud3.view.datachange.SetItems
import java.util.Date

class ActionMapperTest {

    private val mappedData = listOf("")
    private val mappedCollectionItems = mock<CollectionItems<String>> {
        on { getAllItems() } doReturn mappedData
    }
    private val actionMapper = DataChangeMapper<Date, String>(mappedCollectionItems)
    private val data = listOf(Date())
    private val dataIndexes = listOf<Long>(1)
    private val dataIndexPair = listOf(1L to 2L)
    private val indexItemList = listOf(1L to Date())
    private val mapItem = mock<(Date) -> String>() {
        on { invoke(any()) } doReturn ""
    }
    private val mappedIndexItemList = listOf(1L to "")
    private val mapList = mock<(List<Date>) -> List<String>> {
        on { invoke(any()) } doReturn mappedData
    }
    private val mapIndexPairs = mock<(List<Pair<Long, Date>>) -> List<Pair<Long, String>>> {
        on { invoke(any()) } doReturn mappedIndexItemList
    }

    @Test
    fun mapSetItems() {
        val action = mock<SetItems<Date>> {
            on { allItems } doReturn data
        }
        val mappedAction = actionMapper.map(action, mapItem, mapList, mapIndexPairs)
        assertEquals(mapList(data), mappedAction.allItems)
        verify(mappedCollectionItems).reset(mapList(data))
    }

    @Test
    fun mapItemRemoved() {
        val action = mock<ItemRemoved<Date>> {
            on { indexes } doReturn dataIndexes
            on { allItems } doReturn data
        }
        val mappedAction = getMappedAction(action)
        assertEquals(mapList(data), mappedAction.allItems)
        verify(mappedCollectionItems).remove(dataIndexes)
    }

    @Test
    fun mapItemMoved() {
        val action = mock<ItemMoved<Date>> {
            on { indexPairs } doReturn dataIndexPair
            on { allItems } doReturn data
        }
        val mappedAction = getMappedAction(action)
        assertEquals(mapList(data), mappedAction.allItems)
        verify(mappedCollectionItems).move(dataIndexPair)
    }

    @Test
    fun mapItemInserted() {
        val action = mock<ItemInserted<Date>> {
            on { indexItemList } doReturn indexItemList
            on { allItems } doReturn data
        }
        val mappedAction = getMappedAction(action)
        assertEquals(mapList(data), mappedAction.allItems)
        verify(mappedCollectionItems).add(mappedIndexItemList)
    }

    @Test
    fun mapItemChanged() {
        val action = mock<ItemChanged<Date>> {
            on { indexItemList } doReturn indexItemList
            on { allItems } doReturn data
        }
        val mappedAction = getMappedAction(action)
        assertEquals(mapList(data), mappedAction.allItems)
        verify(mappedCollectionItems).replace(mappedIndexItemList)
    }

    private fun getMappedAction(action: DataChange<Date>) = actionMapper.map(action, mapItem, mapList, mapIndexPairs)
}