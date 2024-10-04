package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MultiSelectionFilterFunctionTest {

    private val testTitle = "Test data title"

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var item: SelectorItemModel

    private lateinit var selection: List<SelectorItemModel>
    private val filterFunction = MultiSelectionFilterFunction()

    @Before
    fun setUp() {
        whenever(item.meta).thenReturn(meta)
        selection = listOf(item)
    }

    @Test
    fun `When search query is empty, then it should be returned as is`() {
        assertSame(selection, filterFunction.apply(selection, ""))
    }

    @Test
    fun `When search query contains in data title, then meta should get query range`() {
        whenever(item.title).thenReturn(testTitle)

        filterFunction.apply(selection, testTitle.substring(5, 8))

        verify(meta).queryRanges = any()
    }

    @Test
    fun `When search query doesn't contains in data title, then meta should get empty query range list`() {
        whenever(item.title).thenReturn(testTitle)

        filterFunction.apply(selection, "Search query")

        verify(meta).queryRanges = emptyList()
    }

    @Test
    fun `When search query doesn't contains in data title, then data should be filtered`() {
        whenever(meta.queryRanges).thenReturn(emptyList())
        whenever(item.title).thenReturn(testTitle)

        assertEquals(emptyList<TestData>(), filterFunction.apply(selection, "Test query"))
    }
}