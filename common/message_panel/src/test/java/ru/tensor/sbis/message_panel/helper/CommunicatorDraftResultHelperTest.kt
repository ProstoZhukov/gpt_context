package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import org.apache.commons.lang3.StringUtils.EMPTY
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.communicator.generated.DraftMessage
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.integration.CommunicatorDraftMessage
import ru.tensor.sbis.message_panel.integration.CommunicatorDraftResultHelper
import ru.tensor.sbis.profiles.generated.Person
import ru.tensor.sbis.profiles.generated.PersonName
import java.util.*

/**
 * Тестирование вспомогательного класса [CommunicatorDraftResultHelperTest] для работы
 * с результатом загрузки драфта диалога/чата из микросервиса сообщений
 *
 * @author vv.chekurda
 */
@RunWith(JUnitParamsRunner::class)
class CommunicatorDraftResultHelperTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val resultHelper: DraftResultHelper<CommunicatorDraftMessage> = CommunicatorDraftResultHelper()

    private val messageText = "Test message text"

    @Mock
    private lateinit var draftMessageResult: CommunicatorDraftMessage
    @Mock
    private lateinit var draftMessage: DraftMessage
    @Mock
    private lateinit var quotedMessage: Message
    @Mock
    private lateinit var messageUuid: UUID
    @Mock
    private lateinit var quoteUuid: UUID

    @Test
    fun `Helper returns message id from draft message on getId call`() {
        whenever(draftMessage.id).thenReturn(messageUuid)
        whenever(draftMessageResult.data).thenReturn(draftMessage)

        assertEquals(resultHelper.getId(draftMessageResult), messageUuid)
    }

    @Test
    fun `Helper returns message text from draft message on getText call`() {
        whenever(draftMessage.text).thenReturn(messageText)
        whenever(draftMessageResult.data).thenReturn(draftMessage)

        assertEquals(resultHelper.getText(draftMessageResult), messageText)
    }

    @Test
    fun `Draft message is not empty if it contains message text`() {
        whenever(draftMessage.text).thenReturn(messageText)
        whenever(draftMessageResult.data).thenReturn(draftMessage)

        assertFalse(resultHelper.isEmpty(draftMessageResult))
    }

    @Test
    fun `Draft message is not empty if it contains quoted message`() {
        whenever(draftMessage.text).thenReturn(EMPTY)
        whenever(draftMessageResult.data).thenReturn(draftMessage)
        whenever(draftMessageResult.quote).thenReturn(quotedMessage)

        assertFalse(resultHelper.isEmpty(draftMessageResult))
    }

    @Test
    fun `Draft message is empty if it doesn't contains message text and quoted message`() {
        whenever(draftMessage.text).thenReturn(EMPTY)
        whenever(draftMessageResult.data).thenReturn(draftMessage)
        whenever(draftMessageResult.quote).thenReturn(null)

        assertTrue(resultHelper.isEmpty(draftMessageResult))
    }

    @Test
    fun `Helper returns quoted message from draft as quote content`() {
        val lastName = "lastName"
        val firstName = "firstName"
        val patronymicName = "patronymicName"
        val personName = PersonName(lastName, firstName, patronymicName, EMPTY)
        val sender = mock<Person>()
        val simpleContentItem = MessageContentItem().apply { text = messageText }

        whenever(sender.name).thenReturn(personName)
        whenever(quotedMessage.uuid).thenReturn(quoteUuid)
        whenever(quotedMessage.sender).thenReturn(sender)
        whenever(quotedMessage.content).thenReturn(arrayListOf(simpleContentItem))
        whenever(quotedMessage.rootElements).thenReturn(arrayListOf(0))
        whenever(draftMessageResult.quote).thenReturn(quotedMessage)

        val resultQuoteContent = resultHelper.getQuoteContent(draftMessageResult)!!
        val expectedName = "$lastName $firstName"

        assertEquals(resultQuoteContent.uuid, quoteUuid)
        assertEquals(resultQuoteContent.sender, expectedName)
        assertEquals(resultQuoteContent.text, messageText)
    }
}