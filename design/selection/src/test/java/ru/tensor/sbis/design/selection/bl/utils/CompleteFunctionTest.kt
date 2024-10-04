package ru.tensor.sbis.design.selection.bl.utils

import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.vm.TestData
import ru.tensor.sbis.design.selection.bl.vm.selection.ApplySelection
import ru.tensor.sbis.design.selection.bl.vm.selection.CancelSelection
import ru.tensor.sbis.design.selection.bl.vm.selection.SetSelection

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CompleteFunctionTest {

    @Mock
    private lateinit var selection: List<TestData>

    @Mock
    private lateinit var data: TestData

    private val function = CompleteFunction<TestData>()

    @Test
    fun `When CancelSelection received, then empty list should be returned`() {
        function.apply(CancelSelection, selection).test().assertNoValues()

        verifyNoMoreInteractions(selection)
    }

    @Test
    fun `When ApplySelection received with list of data, then it should be returned as is`() {
        function.apply(ApplySelection, selection).test().assertValue { it === selection }
    }

    @Test
    fun `When SetSelection event received, then selected data should be ignored`() {
        function.apply(SetSelection(data), selection).test().assertValue(listOf(data))

        verifyNoMoreInteractions(selection)
    }
}