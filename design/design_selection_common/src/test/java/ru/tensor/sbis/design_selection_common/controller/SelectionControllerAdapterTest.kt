package ru.tensor.sbis.design_selection_common.controller

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientController
import ru.tensor.sbis.recipients.generated.RecipientControllerProvider
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel

/**
 * Тесты адаптера контроллера компонента выбора [SelectionControllerAdapter].
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionControllerAdapterTest {

    private lateinit var adapter: SelectionControllerAdapter<SelectionItem>

    @Mock
    private lateinit var controllerProvider: RecipientControllerProvider

    @Mock
    private lateinit var controller: RecipientController

    @Mock
    private lateinit var mapper: SelectionItemMapper<RecipientViewModel, RecipientId, SelectionItem, SelectionItemId>

    @Mock
    private lateinit var sourcesImportHelper: SelectionSourcesImportHelper

    @Before
    fun setUp() {
        whenever(controllerProvider.createController()).thenReturn(controller)
        whenever(mapper.getId(any())).thenReturn(mock())
        adapter = SelectionControllerAdapter(
            lazy { controllerProvider },
            mapper,
            sourcesImportHelper
        )
    }

    @Test
    fun `When call getSelectedItems, then call getSelectedRecipients without confirming`() {
        val selectedRecipients = arrayListOf<RecipientViewModel>(mock(), mock())
        whenever(controller.getSelectedRecipients(any())).thenReturn(selectedRecipients)
        whenever(mapper.map(any())).thenReturn(mock())

        adapter.getSelectedItems()

        verify(controller).getSelectedRecipients(isConfirming = false)
        verify(mapper, times(selectedRecipients.size)).map(any())
        verifyNoMoreInteractions(controller)
        verifyNoMoreInteractions(mapper)
    }

    @Test
    fun `When call getSelectedItems 3 times, then create only one controller`() {
        whenever(controller.getSelectedRecipients(any())).thenReturn(arrayListOf())

        adapter.getSelectedItems()
        adapter.getSelectedItems()
        adapter.getSelectedItems()

        verify(controllerProvider).createController()
        verify(controller, times(3)).getSelectedRecipients(isConfirming = false)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call getAllSelectedItems, then call getSelectedRecipients with confirming`() {
        val selectedRecipients = arrayListOf<RecipientViewModel>(mock(), mock())
        whenever(controller.getSelectedRecipients(any())).thenReturn(selectedRecipients)
        whenever(mapper.map(any())).thenReturn(mock())

        adapter.getAllSelectedItems()

        verify(controller).getSelectedRecipients(isConfirming = true)
        verify(mapper, times(selectedRecipients.size)).map(any())
        verifyNoMoreInteractions(controller)
        verifyNoMoreInteractions(mapper)
    }

    @Test
    fun `When call getAllSelectedItems, then import result`() {
        val selectedRecipients = arrayListOf<RecipientViewModel>(mock(), mock())
        whenever(controller.getSelectedRecipients(any())).thenReturn(selectedRecipients)
        whenever(mapper.map(any())).thenReturn(mock())

        adapter.getAllSelectedItems()

        verify(sourcesImportHelper).importSelectedRecipients(selectedRecipients)
        verifyNoMoreInteractions(sourcesImportHelper)
    }

    @Test
    fun `When call select, then call select on controller`() {
        val resultStatus = mock<CommandStatus> {
            on { errorCode } doReturn ErrorCode.SUCCESS
        }
        whenever(controller.select(any())).thenReturn(resultStatus)
        val itemId = mock<SelectionItemId>()
        val expectedId = mapper.getId(itemId)

        adapter.select(itemId)

        verify(controller).select(expectedId)
        verifyNoMoreInteractions(controller)
    }

    @Test(expected = RuntimeException::class)
    fun `When select return error status, then throw runtime exception`() {
        val resultStatus = mock<CommandStatus> {
            on { errorCode } doReturn ErrorCode.OTHER_ERROR
        }
        whenever(controller.select(any())).thenReturn(resultStatus)
        val itemId = mock<SelectionItemId>()
        val expectedId = mapper.getId(itemId)

        adapter.select(itemId)

        verify(controller).select(expectedId)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call replaceSelected, then call replaceSelected on controller`() {
        val itemId = mock<SelectionItemId>()
        val expectedId = mapper.getId(itemId)

        adapter.replaceSelected(itemId)

        verify(controller).replaceSelected(expectedId)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call unselect, then call unselect on controller`() {
        val itemId = mock<SelectionItemId>()
        val expectedId = mapper.getId(itemId)

        adapter.unselect(itemId)

        verify(controller).unselect(expectedId)
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call hasSelectedItems, then call hasSelectedRecipients on controller`() {
        whenever(controller.hasSelectedRecipients()).doReturn(true)

        val result = adapter.hasSelectedItems()

        assertTrue(result)
        verify(controller).hasSelectedRecipients()
        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call setOnFilterChangedCallback, then are no interactions on controller`() {
        adapter.setOnFilterChangedCallback(mock())

        verifyNoMoreInteractions(controller)
    }

    @Test
    fun `When call createCollection, then call get on controller`() {
        whenever(controller.get(any(), any())).doReturn(mock())
        val filter = mock<RecipientFilter>()
        val anchor = mock<PaginationOfRecipientAnchor>()

        adapter.createCollection(filter, anchor)

        verify(controller).get(filter, anchor)
        verify(controllerProvider).createController()
        verifyNoMoreInteractions(controller)
        verifyNoMoreInteractions(controllerProvider)
    }

    @Test
    fun `When call createCollection 2 times, then create 2 controllers`() {
        whenever(controller.get(any(), any())).doReturn(mock())

        adapter.createCollection(mock(), mock())
        adapter.createCollection(mock(), mock())

        verify(controllerProvider, times(2)).createController()
        verifyNoMoreInteractions(controllerProvider)
    }

    @Test
    fun `When call select before create collection, then create 1 controller`() {
        whenever(controller.get(any(), any())).doReturn(mock())
        val selectStatus = mock<CommandStatus> { on { errorCode } doReturn ErrorCode.SUCCESS }
        whenever(controller.select(any())).doReturn(selectStatus)
        val itemId = mock<SelectionItemId>()

        adapter.select(itemId)
        adapter.createCollection(mock(), mock())

        verify(controllerProvider).createController()
        verifyNoMoreInteractions(controllerProvider)
    }

    @Test
    fun `When call create collection before select, then create 1 controller`() {
        whenever(controller.get(any(), any())).doReturn(mock())
        val selectStatus = mock<CommandStatus> { on { errorCode } doReturn ErrorCode.SUCCESS }
        whenever(controller.select(any())).doReturn(selectStatus)
        val itemId = mock<SelectionItemId>()

        adapter.createCollection(mock(), mock())
        adapter.select(itemId)

        verify(controllerProvider).createController()
        verifyNoMoreInteractions(controllerProvider)
    }

    @Test
    fun `When call select and create collection 2 times, then create 2 controllers`() {
        whenever(controller.get(any(), any())).doReturn(mock())
        val selectStatus = mock<CommandStatus> { on { errorCode } doReturn ErrorCode.SUCCESS }
        whenever(controller.select(any())).doReturn(selectStatus)
        val itemId = mock<SelectionItemId>()

        adapter.select(itemId)
        adapter.createCollection(mock(), mock())
        adapter.createCollection(mock(), mock())

        verify(controllerProvider, times(2)).createController()
        verifyNoMoreInteractions(controllerProvider)
    }
}