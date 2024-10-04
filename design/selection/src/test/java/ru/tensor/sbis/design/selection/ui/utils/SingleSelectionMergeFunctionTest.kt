package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SingleSelectionMergeFunctionTest {

    private val itemBId = "Item B id"

    @Mock
    private lateinit var selectedItem: SelectorItemModel

    @Mock
    private lateinit var itemA: SelectorItemModel

    @Mock
    private lateinit var itemB: SelectorItemModel

    @Mock
    private lateinit var vm: SingleSelectionViewModel<SelectorItemModel>

    private lateinit var itemList: List<SelectorItemModel>

    @InjectMocks
    private lateinit var mergeFunction: SingleSelectionMergeFunction

    @Before
    fun setUp() {
        itemList = listOf(itemA, itemB)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When multiple items selected, then exception should be thrown`() {
        mergeFunction.apply(itemList, mock())
    }

    @Test
    fun `When item is not selected, then same list should be returned`() {
        assertSame(itemList, mergeFunction.apply(emptyList(), itemList))
    }

    @Test
    fun `When item selected, then it should be updated without list update`() {
        whenever(selectedItem.id).thenReturn(itemBId)
        whenever(itemB.id).thenReturn(itemBId)

        val mergeList = mergeFunction.apply(listOf(selectedItem), itemList)

        assertSame(itemList, mergeList)
    }

    /**
     * Тест проверяет отсутствие лишних копирований списка для случаев, когда нет вставки
     */
    @Test
    fun `When item list doesn't contain selected item, then same item list should be returned`() {
        whenever(selectedItem.id).thenReturn("Item id")

        assertSame(itemList, mergeFunction.apply(listOf(selectedItem), itemList))
    }

    @Test
    fun `When item list contains item with same id as selected one, then vm update should be requested`() {
        whenever(itemB.id).thenReturn(itemBId)
        whenever(selectedItem.id).thenReturn(itemBId)

        mergeFunction.apply(listOf(selectedItem), itemList)

        verify(vm, only()).updateSelection(itemB)
    }
}