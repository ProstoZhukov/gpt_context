package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.*
import org.junit.Test
import ru.tensor.sbis.message_panel.model.CoreConversationInfo

/**
 * Блок тестов для активации кнопки отправки по правилам чатов
 *
 * Fix [исправление для чатов](https://online.sbis.ru/opendoc.html?guid=bafbb027-6ad6-4ca0-9133-6ff900f58038)
 *
 * @author vv.chekurda
 * Создан 9/11/2019
 */
class SendButtonActivationByRecipientsTest {

    @Test
    fun `Send button inactive in chats without recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(true)

        assertFalse(info.sendButtonActiveByRecipients(false))
    }

    @Test
    fun `Send button active in chat with recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(true)

        assertTrue(info.sendButtonActiveByRecipients(true))
    }

    @Test
    fun `Send button active in existing group conversation with recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(false)
        whenever(info.isNewConversation).thenReturn(false)
        whenever(info.isGroupConversation).thenReturn(true)

        assertTrue(info.sendButtonActiveByRecipients(true))
    }

    @Test
    fun `Send button active in existing non group conversation without recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(false)
        whenever(info.isNewConversation).thenReturn(false)
        whenever(info.isGroupConversation).thenReturn(false)

        assertTrue(info.sendButtonActiveByRecipients(false))
    }

    @Test
    fun `Send button active in existing dialog without selected recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(false)
        whenever(info.isNewConversation).thenReturn(false)
        whenever(info.isGroupConversation).thenReturn(true)

        assertTrue(info.sendButtonActiveByRecipients(false))
    }

    @Test
    fun `Send button inactive in new dialog without selected recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(false)
        whenever(info.isNewConversation).thenReturn(true)
        whenever(info.isGroupConversation).thenReturn(true)

        assertFalse(info.sendButtonActiveByRecipients(false))
    }

    @Test
    fun `Send button inactive in new dialog with selected recipients`() {
        val info = mock<CoreConversationInfo>()
        whenever(info.isChat).thenReturn(false)
        whenever(info.isNewConversation).thenReturn(true)
        whenever(info.isGroupConversation).thenReturn(true)

        assertFalse(info.sendButtonActiveByRecipients(true))
    }
}