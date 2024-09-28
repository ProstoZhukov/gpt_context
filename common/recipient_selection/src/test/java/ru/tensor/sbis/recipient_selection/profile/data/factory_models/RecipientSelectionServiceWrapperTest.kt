package ru.tensor.sbis.recipient_selection.profile.data.factory_models

import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.communicator.generated.ProfilesFoldersResult
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RecipientSelectionServiceWrapperTest {

    private companion object {
        const val SEARCH_STRING = "Test search string"
        const val COUNT = 3
        val EMPTY_EXCLUDE_LIST = ArrayList<UUID>(0)
    }

    @Mock
    private lateinit var dialogUuid: UUID

    @Mock
    private lateinit var documentUuid: UUID

    @Mock
    private lateinit var filter: RecipientsSearchFilter

    @Mock
    private lateinit var result: ProfilesFoldersResult

    @Mock
    private lateinit var recipientController: RecipientsController

    private lateinit var serviceWrapper: RecipientSelectionServiceWrapper

    @Before
    fun setUp() {
        whenever(filter.excludeList).thenReturn(emptyList())
        whenever(filter.searchString).thenReturn(SEARCH_STRING)
        whenever(filter.count).thenReturn(COUNT)

        serviceWrapper = RecipientSelectionServiceWrapper(lazy { recipientController })
    }

    @Test
    fun `When recipients requested for new dialog, then getDialogRecipientsList should be called`() {
        whenever(filter.isNewConversation).thenReturn(true)
        whenever(filter.dialogUuid).thenReturn(null)
        whenever(recipientController.getDialogRecipientsList(SEARCH_STRING, null, null, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getDialogRecipientsList(SEARCH_STRING, null, null, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested for new document's dialog, then getDialogRecipientsList should be called`() {
        whenever(filter.isNewConversation).thenReturn(true)
        whenever(filter.documentUuid).thenReturn(documentUuid)
        whenever(recipientController.getDialogRecipientsList(SEARCH_STRING, null, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getDialogRecipientsList(SEARCH_STRING, null, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested for dialog with id, then getDialogRecipientsList should be called`() {
        whenever(filter.isNewConversation).thenReturn(false)
        whenever(filter.dialogUuid).thenReturn(dialogUuid)
        whenever(recipientController.getDialogRecipientsList(SEARCH_STRING, dialogUuid, null, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getDialogRecipientsList(SEARCH_STRING, dialogUuid, null, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested for document's dialog, then getDialogRecipientsList should be called`() {
        whenever(filter.isNewConversation).thenReturn(false)
        whenever(filter.dialogUuid).thenReturn(dialogUuid)
        whenever(filter.documentUuid).thenReturn(documentUuid)
        whenever(recipientController.getDialogRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getDialogRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested for new chat, then getRecipientsList should be called`() {
        whenever(filter.isNewConversation).thenReturn(true)
        whenever(filter.isChat).thenReturn(true)
        whenever(recipientController.getRecipientsList(SEARCH_STRING,EMPTY_EXCLUDE_LIST, COUNT, false, false)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getRecipientsList(SEARCH_STRING,EMPTY_EXCLUDE_LIST, COUNT, false, false)
    }

    @Test
    fun `When recipients requested for chat, then getChatRecipientsList should be called`() {
        whenever(filter.isChat).thenReturn(true)
        whenever(filter.dialogUuid).thenReturn(dialogUuid)
        whenever(recipientController.getChatRecipientsList(SEARCH_STRING, dialogUuid, null, false, false, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getChatRecipientsList(SEARCH_STRING, dialogUuid, null, false, false, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested for chat with document, then getChatRecipientsList should be called`() {
        whenever(filter.isChat).thenReturn(true)
        whenever(filter.dialogUuid).thenReturn(dialogUuid)
        whenever(filter.documentUuid).thenReturn(documentUuid)
        whenever(recipientController.getChatRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, false, false, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getChatRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, false, false, EMPTY_EXCLUDE_LIST, COUNT)
    }

    @Test
    fun `When recipients requested without preconditions, then getRecipientsList should be called`() {
        whenever(recipientController.getRecipientsList(SEARCH_STRING,EMPTY_EXCLUDE_LIST, COUNT, false, false)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getRecipientsList(SEARCH_STRING,EMPTY_EXCLUDE_LIST, COUNT, false, false)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=99b2aa8d-c0e7-4360-9d74-7a3c93043dac
     */
    @Test
    fun `When recipients requested for DOCUMENT_CONVERSATION, then getDialogRecipientsList should be called`() {
        whenever(filter.conversationType).thenReturn(ConversationType.DOCUMENT_CONVERSATION)
        whenever(filter.dialogUuid).thenReturn(dialogUuid)
        whenever(filter.documentUuid).thenReturn(documentUuid)
        whenever(recipientController.getDialogRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)).thenReturn(result)

        serviceWrapper.list(filter)

        verify(recipientController).getDialogRecipientsList(SEARCH_STRING, dialogUuid, documentUuid, EMPTY_EXCLUDE_LIST, COUNT)
    }
}