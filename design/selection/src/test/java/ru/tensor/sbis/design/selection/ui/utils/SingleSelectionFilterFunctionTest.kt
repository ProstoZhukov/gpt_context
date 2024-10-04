package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.*
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SingleSelectionFilterFunctionTest {

    private val searchQuery = "Test search query"

    private val title = "Test item title"

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var item: SelectorItemModel

    private val filterFunction = SingleSelectionFilterFunction()

    @Before
    fun setUp() {
        whenever(item.meta).thenReturn(meta)
        whenever(item.title).thenReturn(title)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When multiple items selected, then exception should be thrown`() {
        filterFunction.apply(listOf(mock(), mock()), searchQuery)
    }

    @Test
    fun `When search query cleared, then item's bold range should be cleared`() {
        filterFunction.apply(listOf(item), "")

        verify(meta, only()).queryRanges = emptyList()
    }

    @Test
    fun `When item title doesn't match search query, then empty list should be returned`() {
        val filteredList = filterFunction.apply(listOf(item), "Search query doesn't match anything")

        assertSame(emptyList<SelectorItemModel>(), filteredList)
    }

    @Test
    fun `When item title match the search query, then it should receive bold range`() {
        filterFunction.apply(listOf(item), "item")

        verify(meta, only()).queryRanges = listOf(5..9)
    }

    @Test
    fun `When item title match the query string, then it should be returned in the same list`() {
        val itemList = listOf(item)

        val filteredList = filterFunction.apply(itemList, "item")

        assertSame(itemList, filteredList)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=32939720-e506-4a79-a9eb-fd858d547fb9
     */
    @Test
    fun `When item is not selected yet, then same list should be returned`() {
        val selection: List<SelectorItemModel> = mock { on { isEmpty() } doReturn true }

        val filteredList = filterFunction.apply(selection, searchQuery)

        assertSame(selection, filteredList)
    }
}