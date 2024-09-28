package ru.tensor.sbis.design.selection.ui.utils.vm

import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractor
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectorListScreenViewModelTest {

    @Mock
    private lateinit var entity: SelectionListScreenEntity<Any, Any, Any>

    @Mock
    private lateinit var filterCreator: SelectorFilterCreator<Any, Any>

    @Mock
    private lateinit var interactor: SelectionListInteractor<Any, Any, Any, SelectionListScreenEntity<Any, Any, Any>>

    @Mock
    private lateinit var viewModelDelegate: ListScreenVMImpl<SelectionListScreenEntity<Any, Any, Any>>

    @Mock
    private lateinit var fixedButtonVmDelegate: FixedButtonViewModel<Any>

    private lateinit var listVm: SelectorListScreenViewModel

    @Before
    fun setUp() {
        whenever(viewModelDelegate.stubViewVisibility).thenReturn(mock())

        listVm =
            SelectorListScreenViewModel(entity, filterCreator, interactor, viewModelDelegate, fixedButtonVmDelegate)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=e4851b8d-aa32-4bfa-8681-4373f9b6bf0d
    @Test
    fun `When complete event received and search query changed, then entity should not be refreshed on search query update`() {
        listVm.onSelectionCompleted()
        listVm.refresh("Test search query")

        verifyNoMoreInteractions(interactor)
    }

    @Test
    fun `When complete event received and reload requested, then it should be ignored`() {
        listVm.onSelectionCompleted()
        listVm.reload()

        verifyNoMoreInteractions(entity, filterCreator, interactor, viewModelDelegate, fixedButtonVmDelegate)
    }
    //endregion
}
