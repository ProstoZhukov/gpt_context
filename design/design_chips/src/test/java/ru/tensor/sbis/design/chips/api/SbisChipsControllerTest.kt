package ru.tensor.sbis.design.chips.api

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.chips.SbisChipsView
import ru.tensor.sbis.design.chips.list.SbisChipsAdapter
import ru.tensor.sbis.design.chips.models.SbisChipsBackgroundStyle
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.models.SbisChipsSelectionMode
import ru.tensor.sbis.design.chips.models.SbisChipsUnaccentedStyle
import ru.tensor.sbis.design.chips.models.SbisChipsViewMode
import ru.tensor.sbis.design.theme.global_variables.InlineHeight

/**
 * Тесты для [SbisChipsController].
 *
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SbisChipsControllerTest {

    private lateinit var controller: SbisChipsController

    private val adapter: SbisChipsAdapter = mockk()
    private val sbisChipsView: SbisChipsView = mockk()

    @Before
    fun setUp() {
        controller = SbisChipsController()
        controller.attach(sbisChipsView, adapter)
    }

    @Test
    fun `When selectionMode changes from single to multiple then take only the last selected key`() {
        justRun { adapter.selectedKeys = any() }
        justRun { adapter.updateElements(any()) }

        val listKeys = listOf(1, 2, 3)
        controller.selectedKeys = listKeys
        controller.configuration = controller.configuration.copy(selectionMode = SbisChipsSelectionMode.Multiple)

        assertEquals(listKeys.takeLast(1), controller.selectedKeys)
    }

    @Test
    fun `When style background changes then call the update of all elements`() {
        val contrastStyleBackgroundSlot = slot<SbisChipsBackgroundStyle>()
        justRun { adapter.notifyDataSetChanged() }
        justRun { adapter.style = capture(contrastStyleBackgroundSlot) }

        controller.configuration = controller.configuration.copy(
            style = SbisChipsBackgroundStyle.Unaccented(SbisChipsUnaccentedStyle.CONTRAST)
        )

        verify(exactly = 1) { adapter.notifyDataSetChanged() }
        assertEquals(
            SbisChipsBackgroundStyle.Unaccented(SbisChipsUnaccentedStyle.CONTRAST),
            contrastStyleBackgroundSlot.captured
        )
    }

    @Test
    fun `When readOnly changes then call the update of all elements`() {
        val readOnlySlot = slot<Boolean>()
        justRun { adapter.notifyDataSetChanged() }
        justRun { adapter.isReadOnly = capture(readOnlySlot) }

        controller.configuration = controller.configuration.copy(readOnly = true)

        verify(exactly = 1) { adapter.notifyDataSetChanged() }
        assertEquals(true, readOnlySlot.captured)
    }

    @Test
    fun `When viewMode changes then call the update of all elements`() {
        val viewModeSlot = slot<SbisChipsViewMode>()
        justRun { adapter.notifyDataSetChanged() }
        justRun { adapter.viewMode = capture(viewModeSlot) }

        val newViewMode = SbisChipsViewMode.GHOST
        controller.configuration = controller.configuration.copy(viewMode = newViewMode)

        verify(exactly = 1) { adapter.notifyDataSetChanged() }
        assertEquals(newViewMode, viewModeSlot.captured)
    }

    @Test
    fun `When size changes then call the update of all elements`() {
        val sizeSlot = slot<InlineHeight>()
        justRun { adapter.notifyDataSetChanged() }
        justRun { adapter.size = capture(sizeSlot) }

        val newSize = InlineHeight.X3L
        controller.configuration = controller.configuration.copy(size = newSize)

        verify(exactly = 1) { adapter.notifyDataSetChanged() }
        assertEquals(newSize, sizeSlot.captured)
    }

    @Test
    fun `When multiline changes then call change multiline in view`() {
        justRun { adapter.notifyDataSetChanged() }
        justRun { adapter.multiline = any() }
        justRun { sbisChipsView.changeMultiline(any()) }

        controller.configuration = controller.configuration.copy(multiline = true)

        verify(exactly = 1) { sbisChipsView.changeMultiline(true) }
    }

    @Test
    fun `When set items then call submitList()`() {
        justRun { adapter.submitList(any()) }

        val newItems = listOf(SbisChipsItem(0), SbisChipsItem(1), SbisChipsItem(2))
        controller.items = newItems

        verify(exactly = 1) { adapter.submitList(newItems) }
    }

    @Test
    fun `When set selectedKeys then call a callback to change elements`() {
        val delegate: SbisChipsSelectionDelegate = mockk()
        justRun { adapter.updateElements(any()) }
        justRun { adapter.selectedKeys = any() }
        justRun { delegate.onChange(any()) }

        val newKeys = listOf(0, 1, 2, 3, 4)
        controller.configuration = controller.configuration.copy(selectionMode = SbisChipsSelectionMode.Multiple)
        controller.selectionDelegate = delegate
        controller.selectedKeys = newKeys
        verify(exactly = 1) { adapter.updateElements(newKeys.toSet()) }
        verify(exactly = 1) { delegate.onChange(newKeys) }
    }

    @Test
    fun `When set selectedKeys then update the changed elements`() {
        val delegate: SbisChipsSelectionDelegate = mockk()
        justRun { adapter.updateElements(any()) }
        justRun { adapter.selectedKeys = any() }
        justRun { delegate.onChange(any()) }

        val oldKeys = listOf(0, 1, 2, 3, 4)
        val newKeys = listOf(2, 3, 4, 5, 6)
        val updatedKeys = setOf(0, 1, 5, 6)
        controller.configuration = controller.configuration.copy(selectionMode = SbisChipsSelectionMode.Multiple)
        controller.selectionDelegate = delegate
        controller.selectedKeys = oldKeys
        controller.selectedKeys = newKeys

        verify(exactly = 1) { adapter.updateElements(oldKeys.toSet()) }
        verify(exactly = 1) { delegate.onChange(oldKeys) }
        verify(exactly = 1) { adapter.updateElements(updatedKeys) }
        verify(exactly = 1) { delegate.onChange(newKeys) }
    }

    @Test
    fun `When select an element then onSelect() is called`() {
        val delegate: SbisChipsSelectionDelegate = mockk()
        justRun { adapter.updateElements(any()) }
        justRun { adapter.selectedKeys = any() }
        justRun { delegate.onChange(any()) }
        justRun { delegate.onSelect(any()) }

        controller.selectionDelegate = delegate
        controller.handleSelectedChanged(0, true)

        verify(exactly = 1) { delegate.onSelect(0) }
        verify(exactly = 1) { delegate.onChange(listOf(0)) }
        verify(exactly = 1) { adapter.updateElements(setOf(0)) }
        assertEquals(listOf(0), controller.selectedKeys)
    }

    @Test
    fun `When deselect an element then onDeselect() is called`() {
        val delegate: SbisChipsSelectionDelegate = mockk()
        justRun { adapter.updateElements(any()) }
        justRun { adapter.selectedKeys = any() }
        justRun { delegate.onChange(any()) }
        justRun { delegate.onDeselect(any()) }

        controller.configuration = controller.configuration.copy(selectionMode = SbisChipsSelectionMode.Multiple)
        controller.selectedKeys = listOf(0, 1)
        controller.selectionDelegate = delegate
        controller.handleSelectedChanged(0, false)

        verify(exactly = 1) { delegate.onDeselect(0) }
        verify(exactly = 1) { delegate.onChange(listOf(1)) }
        verify(exactly = 1) { adapter.updateElements(setOf(0)) }
        assertEquals(listOf(1), controller.selectedKeys)
    }
}