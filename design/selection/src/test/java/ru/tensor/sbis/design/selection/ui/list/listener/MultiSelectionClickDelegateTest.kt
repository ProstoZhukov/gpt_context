package ru.tensor.sbis.design.selection.ui.list.listener

import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticUseCase
import javax.inject.Provider

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MultiSelectionClickDelegateTest {

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: TestData

    @Mock
    private lateinit var selectionVm: MultiSelectionViewModel<TestData>

    @Mock
    private lateinit var searchVm: SearchViewModel

    @Mock
    private lateinit var openHierarchy: OpenHierarchyListener<TestData>

    @Mock
    private lateinit var lazyOpenHierarchy: Lazy<OpenHierarchyListener<TestData>?>

    @Mock
    private lateinit var clickListener: ItemClickListener<SelectorItemModel, FragmentActivity>

    @Mock
    private lateinit var selectorItemListeners: SelectorItemListeners<SelectorItemModel, FragmentActivity>

    @Mock
    lateinit var activityProvider: Provider<FragmentActivity>

    @Mock
    lateinit var activity: FragmentActivity

    private lateinit var delegate: SelectionClickDelegate<TestData>

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)
        whenever(lazyOpenHierarchy.get()).thenReturn(openHierarchy)
        whenever(activityProvider.get()).thenReturn(activity)
        delegate = MultiSelectionClickDelegate(
            selectionVm,
            searchVm,
            lazyOpenHierarchy,
            selectorItemListeners,
            activityProvider,
            SelectionStatisticUseCase.UNKNOWN.value
        )
    }

    @Test
    fun `When data has nested items, then hierarchy should be opened on item click`() {
        whenever(data.hasNestedItems).thenReturn(true)

        delegate.onItemClicked(data)

        verify(openHierarchy, only()).invoke(data)
        verifyNoMoreInteractions(selectionVm)
    }

    @Test
    fun `When item doesn't have nested items, then item should be assigned as selected`() {
        whenever(data.hasNestedItems).thenReturn(false)

        delegate.onItemClicked(data)

        verify(selectionVm, only()).setSelected(data)
        verifyNoMoreInteractions(openHierarchy)
    }

    @Test
    fun `When add to selection clicked, then toggle listener should be invoked`() {
        delegate.onAddButtonClicked(data)

        verify(selectionVm, only()).toggleSelection(data)
        verifyNoMoreInteractions(openHierarchy)
    }

    @Test
    fun `When selected item clicked, then it should be unselected even if it has nested items`() {
        whenever(meta.selected).thenReturn(true)

        delegate.onItemClicked(data)

        verify(selectionVm, only()).toggleSelection(data)
        verifyNoMoreInteractions(openHierarchy)
    }

    @Test
    fun `Given itemClickListener is not null, item clicked, then call itemClickListener`() {
        whenever(selectorItemListeners.itemClickListener) doReturn clickListener

        delegate.onItemClicked(data)

        verify(clickListener).onClicked(activity, data)
    }

    @Test
    fun `Given itemLongClickListener is not null, item long button clicked, then call itemLongClickListener`() {
        whenever(selectorItemListeners.itemLongClickListener) doReturn clickListener

        delegate.onItemLongClicked(data)

        verify(clickListener).onClicked(activity, data)
    }

    @Test
    fun `Given rightActionListener is not null, item right icon clicked, then call rightActionListener`() {
        whenever(selectorItemListeners.rightActionListener) doReturn clickListener

        delegate.onAddButtonClicked(data)

        verify(clickListener).onClicked(activity, data)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=1c308727-3db4-4780-8c20-193dd036d4a4
    @Test
    fun `When add to selection clicked, then search should be cancelled`() {
        delegate.onAddButtonClicked(data)

        verify(searchVm, only()).clearSearch()
    }

    @Test
    fun `When selected item clicked, then search should be cancelled`() {
        whenever(meta.selected).thenReturn(true)

        delegate.onItemClicked(data)

        verify(searchVm, only()).clearSearch()
    }
    //endregion

    /**
     * Важно доставлять событие о завершении первым, чтобы отключать механики при завершении работы
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=e4851b8d-aa32-4bfa-8681-4373f9b6bf0d
     */
    @Test
    fun `When item selected, then complete event should be first`() {
        val order = inOrder(selectionVm, searchVm, selectorItemListeners)

        delegate.onItemClicked(data)

        order.verify(selectionVm).setSelected(data)
        order.verify(searchVm).clearSearch()
        order.verify(selectorItemListeners).itemClickListener
    }
}
