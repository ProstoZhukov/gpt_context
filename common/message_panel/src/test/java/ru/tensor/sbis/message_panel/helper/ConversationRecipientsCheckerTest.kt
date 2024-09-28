package ru.tensor.sbis.message_panel.helper

import io.mockk.mockk
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import java.util.*

private val conversationUuid = UUID.randomUUID()

/**
 * Тест функции, определяющей необходимость отображения конкретных участников диалога
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ConversationRecipientsCheckerTest {

    @Mock
    private lateinit var interactor: MessagePanelRecipientsInteractor

    @InjectMocks
    private lateinit var checker: ConversationRecipientsChecker

    @Test
    fun `When there is no conversation uuid, then returns original list`() {
        val recipients = listOf<RecipientItem>(mockk(), mockk())

        val result = checker.apply(null to recipients)

        assertSame(recipients, result)
    }

    @Test
    fun `When there is only one recipient, then returns original list`() {
        val recipients = listOf(mock<RecipientPersonItem>())

        val result = checker.apply(conversationUuid to recipients)

        assertSame(recipients, result)
    }

    @Test
    fun `When recipient count is different than one, and all recipients are selected, then returns empty list`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val recipient1 = mock<RecipientPersonItem> {
            on { uuid } doReturn uuid1
        }
        val recipient2 = mock<RecipientPersonItem> {
            on { uuid } doReturn uuid2
        }
        val recipients = listOf(recipient1, recipient2)
        val uuids = listOf(uuid1, uuid2).asArrayList()
        whenever(interactor.checkAllMembersSelected(conversationUuid, uuids)).doReturn(true)

        val result = checker.apply(conversationUuid to recipients)

        assertEquals(emptyList<IContactVM>(), result)
    }

    @Test
    fun `When recipient count is different than one, and not all recipients are selected, then returns original list`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val recipient1 = mock<RecipientPersonItem> {
            on { uuid } doReturn uuid1
        }
        val recipient2 = mock<RecipientPersonItem> {
            on { uuid } doReturn uuid2
        }
        val recipients = listOf(recipient1, recipient2)
        val uuids = listOf(uuid1, uuid2).asArrayList()
        whenever(interactor.checkAllMembersSelected(conversationUuid, uuids)).doReturn(false)

        val result = checker.apply(conversationUuid to recipients)

        assertSame(recipients, result)
    }
}