package ru.tensor.sbis.design_selection.domain.list

import org.mockito.kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.communication_decl.selection.SelectionMode

/**
 * Тесты на логику работы поставщика мода выбора [SelectionModeProvider].
 *
 * @author vv.chekurda
 */
class SelectionModeProviderTest {

    @Test
    fun `Return initial mode as default`() {
        val selectionMode = mock<SelectionMode>()
        val provider = SelectionModeProvider(initialMode = selectionMode)

        assertEquals(selectionMode, provider.selectionMode)
    }

    @Test
    fun `When selectionMode is SINGLE, then isMultiSelection is false`() {
        val provider = SelectionModeProvider(initialMode = SelectionMode.SINGLE)

        assertFalse(provider.isMultiSelection)
    }

    @Test
    fun `When selectionMode is ALWAYS_ADD, then isMultiSelection is true`() {
        val provider = SelectionModeProvider(initialMode = SelectionMode.ALWAYS_ADD)

        assertTrue(provider.isMultiSelection)
    }

    @Test
    fun `When selectionMode is REPLACE_ALL_IF_FIRST, then isMultiSelection is true`() {
        val provider = SelectionModeProvider(initialMode = SelectionMode.REPLACE_ALL_IF_FIRST)

        assertTrue(provider.isMultiSelection)
    }
}