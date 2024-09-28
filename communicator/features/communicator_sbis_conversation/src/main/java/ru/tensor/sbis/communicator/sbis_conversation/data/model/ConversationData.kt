package ru.tensor.sbis.communicator.sbis_conversation.data.model

import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.edo_decl.document.Document

/**
 * Модель информации о переписке сбис
 *
 * @property toolbarData             данные для отображения в тулбаре
 * @property conversationAccess      модель доуступности переписки
 * @property participants            список участников
 * @property recipients              список получателей
 * @property isGroupConversation     true, если переписка групповая
 * @property document                модель документа, к которому прикреплена переписка
 * @property unreadCount             количество непрочитанных сообщений
 * @property isNotifyPersonalOnly    true, если уведомлять только о личных сообщениях в чате
 * @property isIAmAuthor             true, если пользователь создатель переписки
 * @property isClosedChat            true, если чат закрыт
 * @property isInviteDialog          true, если релевантное сообщение является приглашение к диалогу
 * @property isLocked                true, если функционал переписки закрыт со стороны БЛ
 * @property pinnedChatMessage       модель закрепленного в чате сообщения
 * @property canUnpinChatMessage     true, если можно открепить закрепленное к чату сообщение
 * @property isInArchive             true, если переписка находится в архиве
 * @property isPrivateChat           true, если переписка - приватный чат
 *
 * @author vv.chekurda
 */
internal data class ConversationData(
    override var toolbarData: ToolbarData? = null,
    override var conversationAccess: ConversationAccess = ConversationAccess(),
    var participants: List<ContactVM>? = null,
    var recipients: List<ContactVM>? = null,
    var isGroupConversation: Boolean = false,
    var document: Document? = null,
    var unreadCount: Int = 0,
    var isNotifyPersonalOnly: Boolean = false,
    var isIAmAuthor: Boolean = false,
    var isClosedChat: Boolean = false,
    var isInviteDialog: Boolean? = null,
    var isLocked: Boolean? = null,
    var pinnedChatMessage: Message? = null,
    var canUnpinChatMessage: Boolean = false,
    var isInArchive: Boolean = false,
    var isPrivateChat: Boolean = false,
    var chatType: ChatType = ChatType.UNKNOWN
) : BaseConversationData