package ru.tensor.sbis.message_panel.helper

import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.ShareContent

/**
 * Метод получения содержимого, которым нужно поделиться
 */
fun createSharedContent(text: String?, attachments: List<String>?) = when {
    !text.isNullOrEmpty() && !attachments.isNullOrEmpty() -> ShareContent(text, attachments)
    !text.isNullOrEmpty() -> ShareContent(text, emptyList())
    !attachments.isNullOrEmpty() -> ShareContent("", attachments)
    else -> null
}

/**
 * Функция можно ли добавлять в данное обсуждение новых получателей
 */
fun canAddRecipientForConversation(isChat: Boolean, chatPermissions: Permissions?) =
    isDialogConversation(isChat) || chatPermissions?.canAddParticipant == true

internal fun <T> List<T>.asArrayList(): ArrayList<T> = this as? ArrayList ?: ArrayList(this)

internal fun CoreConversationInfo.shouldForceHideChangeRecipientsButton(): Boolean =
    (isChat && !isGroupConversation
            || conversationType == ConversationType.VIDEO_CONVERSATION)

/**
 * Проверка внешних ограничений на активацию кнопки отправки сообщения
 */
internal fun CoreConversationInfo.getSendButtonActivated(): Boolean {
    return isNewConversation || hasChatPermission
}

/**
 * Проверка правил активации отправки на основе информации о получателях
 */
internal fun CoreConversationInfo.sendButtonActiveByRecipients(hasRecipients: Boolean): Boolean {
    return (isChat && hasRecipients) || (!isChat && !isNewConversation)
}

/**
 * Метод получения содержимого, которым нужно поделиться из [CoreConversationInfo]
 */
internal fun CoreConversationInfo.getSharedContent() = createSharedContent(sharedText, sharedAttachments)

/**
 * Проверка, есть ли права на написание сообщения в чате
 */
internal val CoreConversationInfo.hasChatPermission: Boolean
    get() = chatPermissions == null || chatPermissions.canSendMessage || !isChat

/**
 * Можно ли добавлять в данное обсуждение новых получателей
 */
internal val CoreConversationInfo.canAddRecipient: Boolean
    get() = canAddRecipientForConversation(isChat, chatPermissions)

/**
 * Является ли данное обсуждение диалогом
 */
internal val CoreConversationInfo.isDialog: Boolean
    get() = isDialogConversation(isChat)

/**
 * Является ли данное обсуждение Личным чатом
 */
internal val CoreConversationInfo.isPrivateChat: Boolean
    get() = isChat && !isGroupConversation

/**
 * Функция является ли данное обсуждение диалогом
 */
private fun isDialogConversation(isChat: Boolean) = !isChat