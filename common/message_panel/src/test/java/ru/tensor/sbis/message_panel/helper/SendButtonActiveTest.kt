package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.custom.combined.CombinedParameters
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import java.util.*

/**
 * Тестирование активации кнопки отправки в зависимости от внешних условий [CoreConversationInfo]
 *
 * Fix [ошибки](https://online.sbis.ru/opendoc.html?guid=bafbb027-6ad6-4ca0-9133-6ff900f58038)
 *
 * @author vv.chekurda
 * Создан 9/11/2019
 */
@RunWith(JUnitParamsRunner::class)
class SendButtonActiveTest {

    @Test
    fun `Simple positive test`() {
        assertTrue(createPositiveMock().getSendButtonActivated())
    }

    @Test
    fun `Simple negative test`() {
        assertFalse(createNegativeMock().getSendButtonActivated())
    }

    @Test
    fun `Send button active for new conversation`() {
        val info = createNegativeMock()
        // изменение одного из ключевых параметров опрделяет поведение
        whenever(info.isNewConversation).thenReturn(true)

        assertTrue(info.getSendButtonActivated())
    }

    @Test
    @CombinedParameters("true,false", "true,false")
    fun `Button activation independent from conversation uuid`(
        positiveCase: Boolean,
        hasConversationUuid: Boolean
    ) {
        val info = if (positiveCase) createPositiveMock() else createNegativeMock()
        whenever(info.conversationUuid).thenReturn(if (hasConversationUuid) UUID.randomUUID() else null)

        assertTrue(info.getSendButtonActivated() == positiveCase)
    }

    private fun createPositiveMock() = mock<CoreConversationInfo>().apply {
        whenever(isChat).thenReturn(true)

        whenever(chatPermissions).thenReturn(Permissions().apply {
            canSendMessage = true
        })
    }

    private fun createNegativeMock() = mock<CoreConversationInfo>().apply {
        // достаточно "испортить" один из ключевых параметров
        whenever(isChat).thenReturn(true)

        whenever(chatPermissions).thenReturn(Permissions().apply {
            canSendMessage = false
        })
    }
}