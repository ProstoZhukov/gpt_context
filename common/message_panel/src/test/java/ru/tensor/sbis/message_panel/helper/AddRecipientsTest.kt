package ru.tensor.sbis.message_panel.helper

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.message_panel.model.CoreConversationInfo

/**
 * Тестирование группы расширений [CoreConversationInfo] для проверки возможности добавления адресатов
 *
 * @author vv.chekurda
 * Создан 10/11/2019
 */
@RunWith(JUnitParamsRunner::class)
class AddRecipientsTest {

    @Test
    @Parameters(value = ["true, false, true", "false, false, false", "true, true, false", "false, true, false"])
    fun `Given a core conversation info, when it is a chat and not is a group conversation, then it will be a private chat`(
        isChat: Boolean,
        isGroupConversation: Boolean,
        result: Boolean
    ) {
        val coreConversationInfo = CoreConversationInfo(isChat = isChat, isGroupConversation = isGroupConversation)

        Assert.assertEquals(result, coreConversationInfo.isPrivateChat)
    }

    @Test
    @Parameters(value = ["false, true", "true, false"])
    fun `Given a core conversation info, when it is not a chat, then it will be a dialog`(
        isChat: Boolean,
        result: Boolean
    ) {
        val coreConversationInfo = CoreConversationInfo(isChat = isChat)

        Assert.assertEquals(result, coreConversationInfo.isDialog)
    }

    @Test
    @Parameters(value = ["true, true, true", "false, true, true", "false, false, true", "true, false, false"])
    fun `Given a core conversation info, when it is a dialog or not a private chat, then it will be able to add recipient`(
        isChat: Boolean,
        canAddParticipant: Boolean,
        result: Boolean
    ) {
        val chatPermissions = Permissions()
        chatPermissions.canAddParticipant = canAddParticipant

        val coreConversationInfo = CoreConversationInfo(isChat = isChat, chatPermissions = chatPermissions)

        Assert.assertEquals(result, coreConversationInfo.canAddRecipient)
    }

    @Test
    @Parameters(value = ["true, true, true", "false, true, true", "false, false, true", "true, false, false"])
    fun `Given a isChat and chat permissions, when it is a dialog or not a private chat, then it will be able to add recipient`(
        isChat: Boolean,
        canAddParticipant: Boolean,
        result: Boolean
    ) {
        val chatPermissions = Permissions()
        chatPermissions.canAddParticipant = canAddParticipant

        Assert.assertEquals(result, canAddRecipientForConversation(isChat, chatPermissions))
    }


}