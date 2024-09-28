package ru.tensor.sbis.design_selection.domain

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.main.di.SelectionController

/**
 * Тесты интерактора компонента выбора [SelectionInteractor].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionInteractorTest {

    @get:Rule
    val rule = TrampolineSchedulerRule()

    private lateinit var interactor: SelectionInteractor<SelectionItem>

    @Mock
    private lateinit var controller: SelectionController

    @Before
    fun setUp() {
        interactor = SelectionInteractor(controller)
    }

    @Test
    fun `When call select, then use select with notify adapter`() {
        val itemId = mock<SelectionItemId>()

        interactor.select(itemId).test().await()

        verify(controller).select(itemId, true)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call selectBySearch, then use select without notify adapter`() {
        val itemId = mock<SelectionItemId>()

        interactor.selectBySearch(itemId).test().await()

        verify(controller).select(itemId, false)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call replaceSelected, then use replaceSelected`() {
        val itemId = mock<SelectionItemId>()

        interactor.replaceSelected(itemId).test().await()

        verify(controller).replaceSelected(itemId)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call unselect, then use unselect`() {
        val itemId = mock<SelectionItemId>()

        interactor.unselect(itemId).test().await()

        verify(controller).unselect(itemId)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call getSelectedData, then use getSelectedItems and hasSelectedItems`() {
        whenever(controller.getSelectedItems()).thenReturn(mock())
        whenever(controller.hasSelectedItems()).thenReturn(true)

        interactor.getSelectedData(true).test().await()

        verify(controller).getSelectedItems()
        verify(controller).hasSelectedItems()
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call getAllSelectedItems, then use getAllSelectedItems`() {
        whenever(controller.getAllSelectedItems()).thenReturn(mock())

        interactor.getAllSelectedItems().test().await()

        verify(controller).getAllSelectedItems()
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call subscribeOnFilterChanges, then use setOnFilterChangedCallback`() {
        interactor.subscribeOnFilterChanges().test().assertEmpty()
        verify(controller).setOnFilterChangedCallback(any())
        verifyNoMoreInteractions(controller)
    }
}