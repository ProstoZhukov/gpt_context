package ru.tensor.sbis.design_selection_common.controller

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communication_decl.selection.DefaultSelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import java.util.UUID

/**
 * Тесты для обертки контроллера компонента выбора [SelectionControllerWrapperImpl].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionControllerWrapperImplTest {

    private lateinit var controllerWrapper: SelectionControllerWrapperImpl<SelectionItem>

    @Mock
    private lateinit var adapter: SelectionControllerAdapter<SelectionItem>

    @Before
    fun setUp() {
        controllerWrapper = SelectionControllerWrapperImpl(
            adapter,
            mock(),
            mock(),
            mock(),
        )
    }

    @Test
    fun `When call select, then delegate select to adapter`() {
        val itemId = DefaultSelectionItemId(UUID.randomUUID())
        controllerWrapper.select(itemId)

        verify(adapter).select(itemId)
    }

    @Test
    fun `When call getSelectedItems, then delegate getSelectedItems to adapter`() {
        controllerWrapper.getSelectedItems()

        verify(adapter).getSelectedItems()
    }

    @Test
    fun `When call getAllSelectedItems, then delegate getAllSelectedItems to adapter`() {
        controllerWrapper.getAllSelectedItems()

        verify(adapter).getAllSelectedItems()
    }

    @Test
    fun `When call replaceSelected, then delegate replaceSelected to adapter`() {
        val itemId = DefaultSelectionItemId(UUID.randomUUID())
        controllerWrapper.replaceSelected(itemId)

        verify(adapter).replaceSelected(itemId)
    }

    @Test
    fun `When call unselect, then delegate unselect to adapter`() {
        val itemId = DefaultSelectionItemId(UUID.randomUUID())
        controllerWrapper.unselect(itemId)

        verify(adapter).unselect(itemId)
    }

    @Test
    fun `When call setOnFilterChangedCallback, then delegate setOnFilterChangedCallback to adapter`() {
        controllerWrapper.setOnFilterChangedCallback(mock())

        verify(adapter).setOnFilterChangedCallback(any())
    }

    @Test
    fun `When call hasSelectedItems, then delegate hasSelectedItems to adapter`() {
        controllerWrapper.hasSelectedItems()

        verify(adapter).hasSelectedItems()
    }
}