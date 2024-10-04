package ru.tensor.sbis.design.selection.bl.vm.completion

import org.mockito.kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class AutoDisableDoneButtonViewModelTest {

    private val vm = AutoDisableDoneButtonViewModel()

    @Test
    fun `When selection changed, then button should become enabled`() {
        val item1 = mock<SelectorItem>()
        val item2 = mock<SelectorItem>()
        val initialSelection = listOf(item1)
        val selection = listOf(item1, item2)
        val isEnabled = vm.doneButtonEnabled.test()

        vm.setInitialData(initialSelection)
        vm.setSelectedData(selection)

        isEnabled.assertValue(true)
    }

    @Test
    fun `When selection doesn't differ from initial, then button should become disabled`() {
        val item1 = mock<SelectorItem>()
        val initialSelection = listOf(item1)
        val selection = listOf(item1)
        val isEnabled = vm.doneButtonEnabled.test()

        vm.setInitialData(initialSelection)
        vm.setSelectedData(selection)

        isEnabled.assertValue(false)
    }

    @Test
    fun `When observer subscribes to button visibility, then it receives visible state`() {
        vm.doneButtonVisible.test().assertValue(true)
    }
}