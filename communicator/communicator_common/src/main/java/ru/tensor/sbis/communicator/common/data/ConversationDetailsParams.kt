package ru.tensor.sbis.communicator.common.data

import android.net.Uri
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.design.profile_decl.person.PersonData
import java.util.*

/** @SelfDocumented */
data class ConversationDetailsParams(
    val dialogUuid: UUID? = null,
    val messageUuid: UUID? = null,
    val folderUuid: UUID? = null,
    val participantsUuids: ArrayList<UUID>? = null,
    val files: ArrayList<Uri>? = null,
    val textToShare: String? = null,
    val document: Document? = null,
    val type: ConversationType = ConversationType.REGULAR,
    val isChat: Boolean = false,
    val isGroupConversation: Boolean = false,
    val archivedDialog: Boolean = false,
    val viewData: List<PersonData>? = null,
    val dialogTitle: String? = null,
    val title: String? = null,
    val photoId: String? = null,
    val fromChatTab: Boolean = false,
    val needToShowKeyboard: Boolean = false,
    val threadCreationInfo: ThreadInfo? = null,
    val fromParentThread: Boolean = false,
    val highlightMessage: Boolean = false
)