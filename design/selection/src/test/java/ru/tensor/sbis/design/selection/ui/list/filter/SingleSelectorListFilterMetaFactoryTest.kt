package ru.tensor.sbis.design.selection.ui.list.filter

import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SingleSelectorListFilterMetaFactoryTest {

    @Mock
    private lateinit var itemA: SelectorItemModel

    @Mock
    private lateinit var itemB: SelectorItemModel

    @InjectMocks
    private lateinit var metaFactory: SingleSelectorListFilterMetaFactory<Any>

    private lateinit var allItems: List<SelectorItemModel>
    private lateinit var selectedItem: List<SelectorItemModel>

    @Before
    fun setUp() {
        allItems = listOf(itemA, itemB)
        selectedItem = listOf(allItems.random())
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=51b3781e-1b8c-42ac-a836-f45689ceb7c2
    @Test
    fun `When selected item assigned, then no exception should be thrown`() {
        metaFactory.selection = selectedItem

        assertSame(selectedItem, metaFactory.selection)
    }

    @Test
    fun `When available items assigned, then no exception should be thrown`() {
        metaFactory.items = allItems

        assertSame(allItems, metaFactory.items)
    }
    //endregion

    @Test(expected = IllegalArgumentException::class)
    fun `When multiple items are assigned, than exception should be thrown`() {
        metaFactory.selection = allItems
    }
}