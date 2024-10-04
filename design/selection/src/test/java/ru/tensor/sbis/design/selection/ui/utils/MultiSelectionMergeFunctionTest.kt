package ru.tensor.sbis.design.selection.ui.utils

import org.mockito.kotlin.mock
import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
internal class MultiSelectionMergeFunctionTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var data1: TestData
    @Mock
    private lateinit var data2: TestData
    @Mock
    private lateinit var item1: SelectorItemModel
    @Mock
    private lateinit var item2: SelectorItemModel
    @Mock
    private lateinit var vm: MultiSelectionViewModel<SelectorItemModel>

    @InjectMocks
    private lateinit var combinerFunction: MultiSelectionMergeFunction

    private lateinit var itemList: List<SelectorItemModel>

    @Before
    fun setUp() {
        itemList = listOf(item1, item2)
    }

    @Test
    fun `When item selected, then data list should be emitted without the item`() {
        val selection = listOf(mock<SelectorItemModel>())

        assertEquals(listOf(item2), combinerFunction.apply(selection, itemList))
    }

    @Test
    fun `When no items selected, then all data should be returned without modification`() {
        val selection = emptyList<SelectorItemModel>()

        assertSame(itemList, combinerFunction.apply(selection, itemList))
    }
}