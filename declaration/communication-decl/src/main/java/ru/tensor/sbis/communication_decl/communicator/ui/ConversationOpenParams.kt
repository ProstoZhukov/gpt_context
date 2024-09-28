package ru.tensor.sbis.communication_decl.communicator.ui

import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.edo_decl.document.DocumentType
import java.io.Serializable
import java.util.UUID

/**
 * Общий интерфейс параметров для открытия/создания переписки.
 * @property type тип переписки.
 * @property docInfo информация по документу, для создания или открытия переписки.
 *
 * @author da.zhukov
 */
sealed interface ConversationParams : Serializable {
    val type: ConversationType
    val docInfo: DocInfo?
}

/**
 * Общий интерфейс параметров для создания переписки.
 * @property folderUuid идентификатор папки, в которой будет создан диалог, null - если нужно создать в корневой.
 *
 * @author da.zhukov
 */
sealed interface BaseConversationCreationParams : ConversationParams {
    val folderUuid: UUID?
}

/**
 * Общий интерфейс параметров для создания переписки.
 * @property conversationUuid идентификатор переписки которую нееобходимо открыть.
 * @property messageUuid индентификатор релевантного сообщения в переписке.
 * @property isChat является ли переписка чатом.
 * @property highlightMessage необходимость подсветить сообщение [messageUuid].
 *
 * @author da.zhukov
 */
sealed interface BaseConversationOpenParams : ConversationParams {
    val conversationUuid: UUID
    val messageUuid: UUID?
    val isChat: Boolean
    val highlightMessage: Boolean
}

/**
 * Cоздать новую переписку.
 * Участников диалога пользователь выбирает сам.
 */
data class DialogCreationParams(
    override val docInfo: DocInfo? = null,
    override val type: ConversationType = ConversationType.REGULAR,
    override val folderUuid: UUID? = null
) : BaseConversationCreationParams

/**
 * Cоздать новую переписку.
 * Участников диалога пользователь не выбирает.
 * @property participantsUuids список получателей, определенный прикладником.
 */
data class DialogCreationWithParticipantsParams(
    val participantsUuids: List<UUID>,
    override val docInfo: DocInfo? = null,
    override val type: ConversationType = ConversationType.REGULAR,
    override val folderUuid: UUID? = null
) : BaseConversationCreationParams

/**
 * Открыть переписку.
 */
data class ConversationOpenParams(
    override val conversationUuid: UUID,
    override val messageUuid: UUID? = null,
    override val type: ConversationType = ConversationType.REGULAR,
    override val isChat: Boolean = false,
    override val highlightMessage: Boolean = false,
    override val docInfo: DocInfo? = null
) : BaseConversationOpenParams

/**
 * Открыть переписку из реестра диалогов/каналов.
 * Используется только разработчиками переписки.
 *
 * @property archivedConversation true если переписка из архива.
 * @property isInitAsGroupDialog true если переписка по групповому диалогу.
 */
data class ConversationFromRegistryParams(
    override val conversationUuid: UUID,
    override val messageUuid: UUID? = null,
    override val type: ConversationType = ConversationType.REGULAR,
    override val isChat: Boolean = false,
    override val highlightMessage: Boolean = false,
    override val docInfo: DocInfo? = null,
    val archivedConversation: Boolean = false,
    val isInitAsGroupDialog: Boolean = false,
    val conversationViewMode: ConversationViewMode = ConversationViewMode.FULL
) : BaseConversationOpenParams

/**
 * Необходимая информация по документу, для создания или открытия переписки.
 *
 * @property documentUuid идентификатор документа, по которому необходимо создать/отркрыть переписку.
 * @property documentType тип документа по которому необходимо создать/отркрыть переписку.
 * @property documentTitle заголовок документа для отображения в плашке, когда от контроллера ещё нет данных.
 */
data class DocInfo(
    val documentUuid: UUID,
    val documentType: DocumentType?,
    val documentTitle: String
) : Serializable