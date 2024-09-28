package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.message_panel.model.CoreConversationInfo

/**
 * Тестирование определения необходимости принудительного скрытия кнопки выбора получаетелей по умолчанию.
 *
 * @author vv.chekurda
 */
class ShouldForceHideRecipientsButtonByDefaultTest {

    @Test
    fun `Verify recipient button force hidden by default for chat but not group conversation`() {
        val mockCoreInfo = getMockCoreConversationInfo(
            isChat = true,
            isGroupConversation = false,
            isVideoConversationType = false
        )

        val shouldHideButton = mockCoreInfo.shouldForceHideChangeRecipientsButton()

        assertTrue(shouldHideButton)
        verify(mockCoreInfo).isChat
        verify(mockCoreInfo).isGroupConversation
        verifyNoMoreInteractions(mockCoreInfo)
    }

    @Test
    fun `Verify recipient button not force hidden by default for chat if it's group conversation`() {
        val mockCoreInfo = getMockCoreConversationInfo(
            isChat = true,
            isGroupConversation = true,
            isVideoConversationType = false
        )

        val shouldHideButton = mockCoreInfo.shouldForceHideChangeRecipientsButton()

        assertFalse(shouldHideButton)
        verify(mockCoreInfo).isChat
        verify(mockCoreInfo).isGroupConversation
        verify(mockCoreInfo).conversationType
        verifyNoMoreInteractions(mockCoreInfo)
    }

    @Test
    fun `Verify recipient button force hidden by default for video conversation`() {
        val mockCoreInfo = getMockCoreConversationInfo(
            isChat = false,
            isGroupConversation = false,
            isVideoConversationType = true
        )

        val shouldHideButton = mockCoreInfo.shouldForceHideChangeRecipientsButton()

        assertTrue(shouldHideButton)
        verify(mockCoreInfo).isChat
        verify(mockCoreInfo).conversationType
        verifyNoMoreInteractions(mockCoreInfo)
    }

    @Test
    fun `Verify recipient button force hidden by default for video conversation that is also a chat`() {
        val mockCoreInfo = getMockCoreConversationInfo(
            isChat = true,
            isGroupConversation = false,
            isVideoConversationType = true
        )

        val shouldHideButton = mockCoreInfo.shouldForceHideChangeRecipientsButton()

        assertTrue(shouldHideButton)
        verify(mockCoreInfo).isChat
        verify(mockCoreInfo).isGroupConversation
        verifyNoMoreInteractions(mockCoreInfo)
    }

    @Test
    fun `Verify recipient button force hidden by default for video conversation that is also a chat and group conversation`() {
        val mockCoreInfo = getMockCoreConversationInfo(
            isChat = true,
            isGroupConversation = true,
            isVideoConversationType = true
        )

        val shouldHideButton = mockCoreInfo.shouldForceHideChangeRecipientsButton()

        assertTrue(shouldHideButton)
        verify(mockCoreInfo).isChat
        verify(mockCoreInfo).isGroupConversation
        verify(mockCoreInfo).conversationType
        verifyNoMoreInteractions(mockCoreInfo)
    }

    private fun getMockCoreConversationInfo(
        isChat: Boolean,
        isGroupConversation: Boolean,
        isVideoConversationType: Boolean
    ) = mock<CoreConversationInfo> {
        on { this.isChat } doReturn isChat
        on { this.isGroupConversation } doReturn isGroupConversation
        on { conversationType } doReturn if (isVideoConversationType) {
            ConversationType.VIDEO_CONVERSATION
        } else {
            ConversationType.REGULAR
        }
    }
}