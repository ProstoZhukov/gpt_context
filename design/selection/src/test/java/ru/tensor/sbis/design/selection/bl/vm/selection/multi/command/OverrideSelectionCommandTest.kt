package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.bl.vm.TestData

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class OverrideSelectionCommandTest {

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: TestData

    @InjectMocks
    private lateinit var command: OverrideSelectionCommand<TestData>

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)
    }

    @Test
    fun `When command invoked, then selection should contain only target item`() {
        val selection: List<TestData> = mock()

        assertEquals(listOf(data), command.invoke(selection))

        verifyNoMoreInteractions(selection)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=9abdd954-45fa-48a3-8ad8-70d3eed6bbf0
     */
    @Test
    fun `When command invoked, then target item should be selected`() {
        command.invoke(mock())

        verify(meta, only()).isSelected = true
    }
}