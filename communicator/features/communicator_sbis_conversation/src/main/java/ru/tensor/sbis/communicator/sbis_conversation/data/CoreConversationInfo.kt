package ru.tensor.sbis.communicator.sbis_conversation.data

import ru.tensor.sbis.communication_decl.communicator.ui.DocInfo
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communicator.base.conversation.data.BaseCoreConversationInfo
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import java.util.UUID

/**
 * Дата-класс основной информации о диалоге/чате
 */
internal data class CoreConversationInfo @JvmOverloads constructor(
    override var conversationUuid: UUID?,
    override val messageUuid: UUID?,
    var recipientUuid: UUID? = null,
    val folderUuid: UUID?,
    val recipientsUuids: List<UUID>?,
    val docInfo: DocInfo?,
    val conversationType: ConversationType?,
    override var isChat: Boolean = false,
    val fromChatsRegistry: Boolean = false,
    var archivedConversation: Boolean = false,
    val tablet: Boolean = false,
    var toolbarInitialData: ToolbarData? = null,
    val isPrivateChatCreation: Boolean = false,
    override val isInitAsGroupDialog: Boolean = false,
    val creationThreadInfo: ThreadInfo? = null,
    val fromParentThread: Boolean = false,
    val highlightMessage: Boolean = false,
    override val isFullViewMode: Boolean = true
) : BaseCoreConversationInfo {
    /** @SelfDocumented */
    fun getDocumentUUID() = docInfo?.documentUuid
}