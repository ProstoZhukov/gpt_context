package ru.tensor.sbis.message_panel.helper

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import java.util.*

/**
 * Тестирование методов при работе с [CoreConversationInfo] созданным с параметрами по умолчанию.
 * Исключения:
 * - [CoreConversationInfo.conversationUuid] - предположительно не валидное состояние с `null`
 *
 * @author vv.chekurda
 * @since 6/27/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DefaultPreconditionsTest {

    private lateinit var defaultCoreInfo: CoreConversationInfo

    @Before
    fun setUp() {
        defaultCoreInfo = CoreConversationInfo(conversationUuid = UUID.randomUUID())
    }

    @Test
    fun `Don't force hide recipients button by default`() {
        Assert.assertFalse(defaultCoreInfo.shouldForceHideChangeRecipientsButton())
    }

    @Test
    fun `Send button activated by default`() {
        Assert.assertTrue(defaultCoreInfo.getSendButtonActivated())
    }
}