package ru.tensor.sbis.design.selection.bl.vm.completion

import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner::class)
class AtLeastOneChangeFunctionTest {

    @Mock
    private lateinit var selection: List<SelectorItem>

    @Mock
    private lateinit var initialSelection: List<SelectorItem>

    @InjectMocks
    private lateinit var function: AtLeastOneChangeFunction

    @Test
    fun `When nothing selected, then false should be returned`() {
        whenever(selection.isEmpty()).thenReturn(true)

        assertFalse(function.apply(selection, initialSelection))
        verifyNoMoreInteractions(initialSelection)
    }

    @Test
    fun `When selection is not empty, then true should be returned`() {
        whenever(selection.isEmpty()).thenReturn(false)

        assertTrue(function.apply(selection, initialSelection))
        verifyNoMoreInteractions(initialSelection)
    }
}