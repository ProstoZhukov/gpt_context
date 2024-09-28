package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RemoveSelectionCommandTest {

    private val dataId = "Test data id"

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: TestData

    private lateinit var command: RemoveSelectionCommand<TestData>

    @Before
    fun setUp() {
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        command = RemoveSelectionCommand(data)
    }

    @Test
    fun `When item unselected, than selection list should not contain it`() {
        val selection: List<TestData> = listOf(mock { on { id } doReturn dataId })

        assertEquals(emptyList<TestData>(), command.invoke(selection))
    }
}