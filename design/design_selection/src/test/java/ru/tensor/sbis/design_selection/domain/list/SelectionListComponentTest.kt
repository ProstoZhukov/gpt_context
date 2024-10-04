package ru.tensor.sbis.design_selection.domain.list

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterMeta
import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Тесты компонента списка компонента выбора [SelectionListComponent].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionListComponentTest {

    private lateinit var listComponent: SelectionListComponent

    @Mock
    private lateinit var listVM: ListComponentViewViewModel<ItemWithSection<AnyItem>, Any, SelectionItem>

    @Mock
    private lateinit var filterFactory: SelectionFilterFactory<Any, SelectionItemId>

    @Before
    fun setUp() {
        listComponent = SelectionListComponent(listVM, filterFactory)
    }

    @Test
    fun `When call reset, then create filter and reset list view model`() {
        val filter = mock<Any>()
        val filterMeta = mock<SelectionFilterMeta<SelectionItemId>>()
        whenever(filterFactory.createFilter(any())).thenReturn(filter)

        listComponent.reset(filterMeta)

        verify(filterFactory).createFilter(filterMeta)
        verify(listVM).reset(filter)
        verifyNoMoreInteractions(filterFactory)
        verifyNoMoreInteractions(listVM)
    }
}