package ru.tensor.sbis.design.selection.bl.vm.completion

import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Test

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class HiddenDoneButtonViewModelTest {

    @Mock
    private lateinit var initialSelection: List<SelectorItem>

    @Mock
    private lateinit var selection: List<SelectorItem>

    private val vm = HiddenDoneButtonViewModel()

    @Test
    fun `When observer subscribes to button visibility, then it always receive invisible state`() {
        vm.doneButtonVisible.test().assertValue(false)
    }

    @Test
    fun `When observer subscribes to button enable state, then it newer receive any state`() {
        vm.doneButtonEnabled.test().assertComplete()
    }

    @Test
    fun `When vm gets initial selection, then not visibility nor enable state should be changed`() {
        val visibility = vm.doneButtonVisible.skip(1).test()
        val enableState = vm.doneButtonEnabled.test()

        vm.setInitialData(initialSelection)

        verifyNoMoreInteractions(initialSelection)
        visibility.assertComplete()
        enableState.assertComplete()
    }

    @Test
    fun `When vm gets selection, then not visibility nor enable state should be changed`() {
        val visibility = vm.doneButtonVisible.skip(1).test()
        val enableState = vm.doneButtonEnabled.test()

        vm.setSelectedData(selection)

        verifyNoMoreInteractions(selection)
        visibility.assertComplete()
        enableState.assertComplete()
    }
}