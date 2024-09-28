package ru.tensor.sbis.design_selection.domain.completion

import org.mockito.kotlin.mock
import io.reactivex.functions.BiFunction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.completion.button.DoneButtonVisibilityViewModel
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Тесты на механику работы вью-модели кнопки подтверждения выбора [DoneButtonVisibilityViewModel].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DoneButtonVisibilityViewModelTest {

    private lateinit var doneButtonVM: DoneButtonVisibilityViewModel
    private val functionResult = true

    @Before
    fun setUp() {
        val function = BiFunction<SelectedData<SelectionItem>, SelectedData<SelectionItem>, Boolean> { _, _ ->
            functionResult
        }
        doneButtonVM = DoneButtonVisibilityViewModel(function)
    }

    @Test
    fun `When set only initial data, then receive 1 value`() {
        val doneButtonVisible = doneButtonVM.doneButtonVisible.test()

        doneButtonVM.setInitialData(mock())

        doneButtonVisible.assertValueCount(1)
            .assertValueAt(0, functionResult)
    }

    @Test
    fun `When set initial data and set selected data, then receive 2 values`() {
        val doneButtonVisible = doneButtonVM.doneButtonVisible.test()

        doneButtonVM.setInitialData(mock())
        doneButtonVM.setSelectedData(mock())

        doneButtonVisible.assertValueCount(2)
            .assertValueAt(0, functionResult)
            .assertValueAt(1, functionResult)
    }

    @Test
    fun `When set initial data 2 times, then receive 2 values`() {
        val doneButtonVisible = doneButtonVM.doneButtonVisible.test()

        doneButtonVM.setInitialData(mock())
        doneButtonVM.setInitialData(mock())

        doneButtonVisible.assertValueCount(2)
            .assertValueAt(0, functionResult)
            .assertValueAt(1, functionResult)
    }

    @Test
    fun `When set selected data 2 times, then don't receive values`() {
        val doneButtonVisible = doneButtonVM.doneButtonVisible.test()

        doneButtonVM.setSelectedData(mock())
        doneButtonVM.setSelectedData(mock())

        doneButtonVisible.assertEmpty()
    }

    @Test
    fun `When set initial data and selected data 2 times, then receive 3 values`() {
        val doneButtonVisible = doneButtonVM.doneButtonVisible.test()

        doneButtonVM.setInitialData(mock())
        doneButtonVM.setSelectedData(mock())
        doneButtonVM.setSelectedData(mock())

        doneButtonVisible.assertValueCount(3)
            .assertValueAt(0, functionResult)
            .assertValueAt(1, functionResult)
            .assertValueAt(2, functionResult)
    }
}