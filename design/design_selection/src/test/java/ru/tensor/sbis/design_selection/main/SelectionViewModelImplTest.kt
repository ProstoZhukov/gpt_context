package ru.tensor.sbis.design_selection.main

import androidx.fragment.app.FragmentActivity
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonStrategy
import ru.tensor.sbis.design_selection.domain.completion.ApplySelection
import ru.tensor.sbis.design_selection.domain.completion.CancelSelection
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.design_selection.ui.main.vm.SelectionViewModelImpl
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate

/**
 * Тесты вью-модели компонента выбора [SelectionViewModelImpl].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionViewModelImplTest {

    @get:Rule
    val rule = TrampolineSchedulerRule()

    private lateinit var doneButtonDelegate: DoneButtonDelegate<SelectionItem>

    private lateinit var viewModel: SelectionViewModelImpl<SelectionItem>

    @Test
    fun `When create ViewModel, then selectedDataObservable receive default empty SelectedData`() {
        prepareViewModel()
        val defaultEmptyData = SelectedData<SelectionItem>()

        viewModel.selectedDataObservable.test()
            .awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, defaultEmptyData)
    }

    @Test
    fun `When call setSelectedData, then selectedDataObservable receive data`() {
        prepareViewModel()
        val data1 = SelectedData(items = listOf(mock(), mock()))
        val data2 = SelectedData(items = listOf(mock(), mock(), mock()))
        val selectedDataObserver = viewModel.selectedDataObservable.test()

        viewModel.setSelectedData(data1)
        viewModel.setSelectedData(data2)

        selectedDataObserver.awaitCount(3)
            .assertValueCount(3)
            .assertValueAt(1, data1)
            .assertValueAt(2, data2)
    }

    @Test
    fun `When create ViewModel, then selectedData return default empty SelectedData`() {
        prepareViewModel()
        val expectedInitData = SelectedData<SelectionItem>()

        assertEquals(expectedInitData, viewModel.selectedData)
    }

    @Test
    fun `When call setSelectedData, then selectedData return last SelectedData`() {
        prepareViewModel()
        val data1 = SelectedData(items = listOf(mock(), mock()))
        val data2 = SelectedData(items = listOf(mock(), mock(), mock()))

        viewModel.setSelectedData(data1)
        viewModel.setSelectedData(data2)

        assertEquals(data2, viewModel.selectedData)
    }

    @Test
    fun `When create ViewModel, then doneButtonDelegate don't receive initial data`() {
        prepareViewModel()

        verify(doneButtonDelegate, never()).setInitialData(any())
    }

    @Test
    fun `When set first controller selected data, then doneButtonDelegate receive initial data`() {
        prepareViewModel()
        val selectedData = SelectedData<SelectionItem>(isUserSelection = false)

        viewModel.setSelectedData(selectedData)

        verify(doneButtonDelegate).setInitialData(selectedData)
    }

    @Test
    fun `When set controller selected data 2 times, then doneButtonDelegate receive only first initial data`() {
        prepareViewModel()
        val selectedData1 = SelectedData(listOf(mock()), isUserSelection = false)
        val selectedData2 = SelectedData<SelectionItem>(isUserSelection = false)

        viewModel.setSelectedData(selectedData1)
        viewModel.setSelectedData(selectedData2)

        verify(doneButtonDelegate).setInitialData(selectedData1)
        verify(doneButtonDelegate).setInitialData(any())
    }

    @Test
    fun `When call setSelectedData with user data, then doneButtonDelegate don't receive initial data`() {
        prepareViewModel()
        val selectedData = SelectedData<SelectionItem>(isUserSelection = true)

        viewModel.setSelectedData(selectedData)

        verify(doneButtonDelegate, never()).setInitialData(selectedData)
    }

    @Test
    fun `When call setSelectedData, then doneButtonDelegate receive all selected data`() {
        prepareViewModel()
        val initialData = SelectedData<SelectionItem>()
        val selectedData1 = SelectedData(listOf(mock()))
        val selectedData2 = SelectedData(listOf(mock(), mock()))

        viewModel.setSelectedData(selectedData1)
        viewModel.setSelectedData(selectedData2)

        verify(doneButtonDelegate).setSelectedData(initialData)
        verify(doneButtonDelegate).setSelectedData(selectedData1)
        verify(doneButtonDelegate).setSelectedData(selectedData2)
    }

    @Test
    fun `When call onDoneButtonClicked, then onDoneButtonClickedObservable receive event`() {
        prepareViewModel()
        val clickObserver = viewModel.onDoneButtonClickedObservable.test()

        viewModel.onDoneButtonClicked()

        clickObserver.awaitCount(1)
            .assertValueCount(1)
    }

    @Test
    fun `When call onError, then errorMessage receive message`() {
        prepareViewModel()
        val testError = "123"
        val errorMessageObserver = viewModel.errorMessage.test()

        viewModel.onError(testError)

        errorMessageObserver.awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, testError)
    }

    @Test
    fun `When call cancel, then result is CancelSelection`() {
        prepareViewModel()
        val resultObserver = viewModel.result.test()

        viewModel.cancel()

        resultObserver.awaitCount(1)
            .assertValue(CancelSelection)
            .assertNotComplete()
    }

    @Test
    fun `When call complete with empty list, then result is empty list`() {
        prepareViewModel()
        val resultObserver = viewModel.result.test()
        val emptyResult = emptyList<SelectionItem>()
        val selectionResult = SelectionComponentResult(emptyResult)

        viewModel.complete(selectionResult)

        resultObserver.awaitCount(1)
            .assertValueAt(0, ApplySelection(selectionResult))
            .assertNotComplete()
    }

    @Test
    fun `When call complete with items, then result is these items`() {
        prepareViewModel()
        val resultObserver = viewModel.result.test()
        val selectedItems = listOf<SelectionItem>(mock(), mock())
        val selectionResult = SelectionComponentResult(selectedItems)

        viewModel.complete(selectionResult)

        resultObserver.awaitCount(1)
            .assertValueAt(0, ApplySelection(selectionResult))
            .assertNotComplete()
    }

    @Test
    fun `When call onBackPressed without router, then return true`() {
        prepareViewModel()
        viewModel.setRouter(null)

        val isHandled = viewModel.onBackPressed()

        assertTrue(isHandled)
    }

    @Test
    fun `When call onBackPressed and router return true, then return true`() {
        prepareViewModel()
        val router = mock<SelectionRouter> {
            on { back() } doReturn true
        }
        viewModel.setRouter(router)

        val isHandled = viewModel.onBackPressed()

        assertTrue(isHandled)
    }

    @Test
    fun `When call onBackPressed and router return false, then return true and set result CancelSelection`() {
        prepareViewModel()
        val resultObserver = viewModel.result.test()
        val router = mock<SelectionRouter>()
        whenever(router.back()).thenReturn(false)
        viewModel.setRouter(router)

        val isHandled = viewModel.onBackPressed()

        assertTrue(isHandled)
        resultObserver.awaitCount(1)
            .assertValue(CancelSelection)
            .assertNotComplete()
    }

    @Test
    fun `When call onBackPressed after cancel, then return false`() {
        prepareViewModel()
        viewModel.setRouter(mock())

        viewModel.cancel()
        val isHandled = viewModel.onBackPressed()

        assertFalse(isHandled)
    }

    @Test
    fun `When call onBackPressed after complete, then return false`() {
        prepareViewModel()
        viewModel.setRouter(mock())

        viewModel.complete(SelectionComponentResult(emptyList()))
        val isHandled = viewModel.onBackPressed()

        assertFalse(isHandled)
    }

    @Test
    fun `When create ViewModel with null HeaderButtonContract, then isHeaderButtonVisible receive false`() {
        prepareViewModel(headerButtonContract = null)

        viewModel.isHeaderButtonVisible.test()
            .awaitCount(1)
            .assertValueAt(0, false)
    }

    @Test
    fun `When create ViewModel with not null HeaderButtonContract, then isHeaderButtonVisible receive true`() {
        prepareViewModel()

        viewModel.isHeaderButtonVisible.test()
            .awaitCount(1)
            .assertValueAt(0, true)
    }

    @Test
    fun `When HeaderButtonStrategy with hideButton == true, then isHeaderButtonVisible receive false`() {
        prepareViewModel()
        val isHeaderButtonVisibleObserver = viewModel.isHeaderButtonVisible.test()
        val strategy = HeaderButtonStrategy(hideButton = true)

        viewModel.onHeaderButtonClicked(strategy)

        isHeaderButtonVisibleObserver.awaitCount(2)
            // Initial
            .assertValueAt(0, true)
            // After click
            .assertValueAt(1, false)
    }

    @Test
    fun `When HeaderButtonStrategy with hideButton == false, then isHeaderButtonVisible receive true`() {
        prepareViewModel()
        val isHeaderButtonVisibleObserver = viewModel.isHeaderButtonVisible.test()
        val strategy = HeaderButtonStrategy(hideButton = false)

        viewModel.onHeaderButtonClicked(strategy)

        isHeaderButtonVisibleObserver.awaitCount(2)
            .assertValueCount(2)
            // Initial
            .assertValueAt(0, true)
            // After click
            .assertValueAt(1, true)
    }

    @Test
    fun `When HeaderButtonStrategy with new config, then call close all folders and update config`() {
        prepareViewModel()
        val newConfig = mock<SelectionConfig>()
        val strategy = HeaderButtonStrategy(newConfig = newConfig)
        val router = mock<SelectionRouter>()
        viewModel.setRouter(router)
        val updateConfigObserver = viewModel.updateConfig.test()

        viewModel.onHeaderButtonClicked(strategy)

        verify(router).closeAllFolders()
        updateConfigObserver.awaitCount(1)
            .assertValueCount(1)
            .assertValueAt(0, newConfig)
    }

    @Test
    fun `When HeaderButtonStrategy with null config, then don't call close all folders and don't update config`() {
        prepareViewModel()
        val strategy = HeaderButtonStrategy(newConfig = null)
        val router = mock<SelectionRouter>()
        viewModel.setRouter(router)
        val updateConfigObserver = viewModel.updateConfig.test()

        viewModel.onHeaderButtonClicked(strategy)

        verify(router, never()).closeAllFolders()
        updateConfigObserver.assertNoValues()
    }

    private fun prepareViewModel(
        headerButtonContract: HeaderButtonContract<SelectionItem, FragmentActivity>? = mock()
    ) {
        doneButtonDelegate = mock()

        viewModel = SelectionViewModelImpl(
            selectedItemsClickListener = mock(),
            rulesHelper = mock { on { isFinalComplete } doReturn true },
            doneButtonDelegate = doneButtonDelegate,
            headerButtonContract = headerButtonContract,
            uiSchedule = Schedulers.newThread()
        )
    }
}