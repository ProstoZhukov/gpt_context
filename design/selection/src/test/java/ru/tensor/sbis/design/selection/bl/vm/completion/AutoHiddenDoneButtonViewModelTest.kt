package ru.tensor.sbis.design.selection.bl.vm.completion

import org.mockito.kotlin.whenever
import io.reactivex.functions.BiFunction
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class AutoHiddenDoneButtonViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var selection: List<SelectorItem>

    @Mock
    private lateinit var initialSelection: List<SelectorItem>

    @Mock
    private lateinit var visibilityFunction: BiFunction<List<SelectorItem>, List<SelectorItem>, Boolean>

    @InjectMocks
    private lateinit var vm: AutoHiddenDoneButtonViewModel

    @Test
    @Parameters("true", "false")
    fun `When selection changed, then visibility function should be called`(isSelectionChanged: Boolean) {
        val visibility = vm.doneButtonVisible.test()
        whenever(visibilityFunction.apply(selection, initialSelection)).thenReturn(isSelectionChanged)

        vm.setInitialData(initialSelection)
        vm.setSelectedData(selection)

        visibility.assertValue(isSelectionChanged)
    }

    @Test
    fun `When vm gets enable state observer, then it should not receive any data`() {
        vm.doneButtonEnabled.test().assertNoValues().assertComplete()
    }
}