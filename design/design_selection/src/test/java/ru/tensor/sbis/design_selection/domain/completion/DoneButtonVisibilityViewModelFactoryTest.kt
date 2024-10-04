package ru.tensor.sbis.design_selection.domain.completion

import org.mockito.kotlin.mock
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.completion.button.DoneButtonViewModelFactory
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Тесты логики работы вью-моделей фабрики [DoneButtonViewModelFactory]
 * в зависимости от передаваемых модов [SelectionDoneButtonVisibilityMode].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DoneButtonVisibilityViewModelFactoryTest {

    private lateinit var viewModelFactory: DoneButtonViewModelFactory

    @Before
    fun setUp() {
        viewModelFactory = DoneButtonViewModelFactory()
    }

    @Test
    fun `When create with mode VISIBLE, then done button is always visible`() {
        val viewModel = viewModelFactory.create(SelectionDoneButtonVisibilityMode.VISIBLE)
        val doneButtonVisibleTest = viewModel.doneButtonVisible.test()
        val initialData = SelectedData<SelectionItem>()
        val copyInitialData = initialData.copy()
        val selectedData = SelectedData(items = listOf(mock(), mock()))

        viewModel.setInitialData(initialData)
        viewModel.setSelectedData(copyInitialData)
        viewModel.setSelectedData(selectedData)

        doneButtonVisibleTest.assertValueCount(3)
            .assertValueAt(0, true)
            .assertValueAt(1, true)
            .assertValueAt(2, true)
    }

    @Test
    fun `When create with mode SELECTED_CHANGED, then done button is visible on selected items changed`() {
        val viewModel = viewModelFactory.create(SelectionDoneButtonVisibilityMode.SELECTED_CHANGED)
        val doneButtonVisibleTest = viewModel.doneButtonVisible.test()
        val initialData = SelectedData<SelectionItem>()
        val copyInitialData = initialData.copy()
        val selectedItem1 = mock<SelectionItem>()
        val selectedItem2 = mock<SelectionItem>()
        val selectedData = SelectedData(items = listOf(selectedItem1, selectedItem2))

        viewModel.setInitialData(initialData)
        viewModel.setSelectedData(copyInitialData)
        viewModel.setSelectedData(selectedData)
        viewModel.setSelectedData(copyInitialData)

        doneButtonVisibleTest.assertValueCount(4)
            .assertValueAt(0, false)
            .assertValueAt(1, false)
            .assertValueAt(2, true)
            .assertValueAt(3, false)
    }

    @Test
    fun `When create with mode SELECTED_CHANGED, then done button is not visible on shuffle items`() {
        val viewModel = viewModelFactory.create(SelectionDoneButtonVisibilityMode.SELECTED_CHANGED)
        val doneButtonVisibleTest = viewModel.doneButtonVisible.test()
        val selectedItem1 = mock<SelectionItem>()
        val selectedItem2 = mock<SelectionItem>()
        val initialData = SelectedData(items = listOf(selectedItem1, selectedItem2))
        val selectedData = SelectedData(items = listOf(selectedItem2, selectedItem1))

        viewModel.setInitialData(initialData)
        viewModel.setSelectedData(selectedData)

        doneButtonVisibleTest.assertValueCount(2)
            .assertValueAt(0, false)
            .assertValueAt(1, false)
    }

    @Test
    fun `When create with mode AT_LEAST_ONE with empty initial, then done button is visible on any one selected`() {
        val viewModel = viewModelFactory.create(SelectionDoneButtonVisibilityMode.AT_LEAST_ONE)
        val doneButtonVisibleTest = viewModel.doneButtonVisible.test()

        val initialData = SelectedData<SelectionItem>()
        val emptySelectedData = SelectedData<SelectionItem>()
        val notEmptySelectedData = SelectedData(items = listOf(mock()))

        viewModel.setInitialData(initialData)
        viewModel.setSelectedData(emptySelectedData)
        viewModel.setSelectedData(notEmptySelectedData)
        viewModel.setSelectedData(emptySelectedData)

        assertTrue(initialData.items.isEmpty())
        doneButtonVisibleTest.assertValueCount(4)
            .assertValueAt(0, false)
            .assertValueAt(1, false)
            .assertValueAt(2, true)
            .assertValueAt(3, false)
    }

    @Test
    fun `When create with mode AT_LEAST_ONE with not empty initial, then done button is visible on any one selected`() {
        val viewModel = viewModelFactory.create(SelectionDoneButtonVisibilityMode.AT_LEAST_ONE)
        val doneButtonVisibleTest = viewModel.doneButtonVisible.test()

        val initialData = SelectedData(items = listOf(mock()))
        val emptySelectedData = SelectedData<SelectionItem>()
        val notEmptySelectedData = SelectedData(items = listOf(mock()))

        viewModel.setInitialData(initialData)
        viewModel.setSelectedData(emptySelectedData)
        viewModel.setSelectedData(notEmptySelectedData)
        viewModel.setSelectedData(emptySelectedData)

        assertTrue(initialData.items.isNotEmpty())
        doneButtonVisibleTest.assertValueCount(4)
            .assertValueAt(0, true)
            .assertValueAt(1, false)
            .assertValueAt(2, true)
            .assertValueAt(3, false)
    }
}