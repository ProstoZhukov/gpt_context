package ru.tensor.sbis.design.selection.ui.list.listener

import androidx.fragment.app.FragmentActivity
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import dagger.Lazy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
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
class SingleSelectionClickDelegateTest {

    @Mock
    private lateinit var data: TestData

    @Mock
    private lateinit var selectionVm: SingleSelectionViewModel<TestData>

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
        whenever(lazyOpenHierarchy.get()).thenReturn(openHierarchy)
        whenever(activityProvider.get()).thenReturn(activity)
        delegate = SingleSelectionClickDelegate(
            selectionVm,
            searchVm,
            lazyOpenHierarchy,
            selectorItemListeners,
            activityProvider,
            SelectionStatisticUseCase.UNKNOWN.value
        )
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

    @Test(expected = UnsupportedOperationException::class)
    fun `When right icon clicked, then throw exception`() {
        delegate.onAddButtonClicked(data)
    }

    /**
     * Важно доставлять событие о завершении первым, чтобы отключать механики при завершении работы
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=e4851b8d-aa32-4bfa-8681-4373f9b6bf0d
     */
    @Test
    fun `When item selected, then complete event should be first`() {
        val order = inOrder(selectionVm, searchVm, selectorItemListeners)

        delegate.onItemClicked(data)

        order.verify(selectionVm).complete(data)
        order.verify(searchVm).cancelSearch()
        order.verify(selectorItemListeners).itemClickListener
    }
}
