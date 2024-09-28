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
class VisibleDoneButtonViewModelTest {

    @Mock
    private lateinit var initialSelection: List<SelectorItem>

    @Mock
    private lateinit var selection: List<SelectorItem>

    private val vm = VisibleDoneButtonViewModel()

    @Test
    fun `When observer subscribes to button visibility, then it always receive visible state`() {
        vm.doneButtonVisible.test().assertValue(true)
    }

    @Test
    fun `When observer subscribes to button enable state, then it always receive enabled state`() {
        vm.doneButtonEnabled.test().assertValue(true)
    }

    @Test
    fun `When vm gets initial selection, then not visibility nor enable state should be changed`() {
        val visibility = vm.doneButtonVisible.skip(1).test()
        val enableState = vm.doneButtonEnabled.skip(1).test()

        vm.setInitialData(initialSelection)

        verifyNoMoreInteractions(initialSelection)
        visibility.assertComplete()
        enableState.assertComplete()
    }

    @Test
    fun `When vm gets selection, then not visibility nor enable state should be changed`() {
        val visibility = vm.doneButtonVisible.skip(1).test()
        val enableState = vm.doneButtonEnabled.skip(1).test()

        vm.setSelectedData(selection)

        verifyNoMoreInteractions(selection)
        visibility.assertComplete()
        enableState.assertComplete()
    }
}