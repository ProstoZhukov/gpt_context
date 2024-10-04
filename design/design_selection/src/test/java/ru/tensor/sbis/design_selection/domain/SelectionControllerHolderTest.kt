package ru.tensor.sbis.design_selection.domain

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.ui.main.di.SelectionController
import ru.tensor.sbis.design_selection.ui.main.di.SelectionControllerProvider

/**
 * Тесты на холдер-поставщик контроллера компонента выбора [SelectionControllerHolder].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionControllerHolderTest {

    private lateinit var controllerHolder: SelectionControllerHolder

    private lateinit var controllerProvider: SelectionControllerProvider

    @Before
    fun setUp() {
        controllerProvider = mock()
        controllerHolder = SelectionControllerHolder(lazy { controllerProvider })
    }

    @Test
    fun `When call createSelectionController, then use controllerProvider`() {
        val folderItem = mock<SelectionFolderItem>()
        val mockController = mock<SelectionController>()
        whenever(controllerProvider.createSelectionControllerWrapper(any())).thenReturn(mockController)

        val controller = controllerHolder.createSelectionController(folderItem)

        assertEquals(mockController, controller)
        verify(controllerProvider).createSelectionControllerWrapper(folderItem)
    }
}