package ru.tensor.sbis.design.selection.bl.vm.selection.multi.command

import org.mockito.kotlin.*
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
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
class AddSelectionCommandTest {

    private val limit = 1
    private val limitSubject = PublishSubject.create<Int>()
    private val dataId = "Test data id"

    @Mock
    private lateinit var meta: SelectorItemMeta
    @Mock
    private lateinit var data: TestData
    @Mock
    private lateinit var overhead: TestData

    private lateinit var command: AddSelectionCommand<TestData>

    @Before
    fun setUp() {
        whenever(data.id).thenReturn(dataId)
        whenever(data.meta).thenReturn(meta)
        command = AddSelectionCommand(data, limit, limitSubject)
    }

    @Test
    fun `When limit exceed, then selection should be returned as is`() {
        val selection = listOf(overhead)

        assertEquals(selection, command.invoke(selection))
    }

    @Test
    fun `When limit exceed, then notification should be posted`() {
        val limit = limitSubject.test()

        command.invoke(listOf(overhead))

        limit.assertValue(1)
    }

    @Test
    fun `When data added, then selection list should contain it`() {
        assertEquals(listOf(data), command.invoke(emptyList()))
    }

    @Test
    fun `When data added, then selection flag should be assigned`() {
        command.invoke(emptyList())

        verify(meta, only()).isSelected = true
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=2adbcaa0-7cb7-4763-9195-77849e037de6
     */
    @Test
    fun `Given selection with element, when element with same id added, than it should override item in selection`() {
        val selectedData: TestData = mock { on { id } doReturn dataId }

        val selection = command.invoke(listOf(selectedData))

        assertSame(data, selection.single())
    }
}