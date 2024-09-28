package ru.tensor.sbis.recipient_selection.profile.data

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilter
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionFilterKeys
import ru.tensor.sbis.recipient_selection.profile.ui.RECIPIENT_SELECTION_LIST_SIZE
import java.io.Serializable
import java.util.*

/**
 * Рабочий поисковый фильтр реализации компонента выбора получателей
 *
 * @author vv.chekurda
 */
internal data class RecipientsSearchFilter(
    var searchString: String = "",
    val dialogUuid: UUID?,
    val documentUuid: UUID?,
    val isNewConversation: Boolean,
    val isChat: Boolean,
    val containsWorkingGroups: Boolean,
    val onlyParticipants: Boolean,
    val excludeParticipants: Boolean,
    val canResultBeEmpty: Boolean,
    var excludeList: List<String>,
    var count: Int,
    val conversationType: ConversationType?,
    val requestCode: Int
) : Serializable {

    constructor(params: RecipientSelectionFilter) : this(
        searchString = "",
        dialogUuid = params.dialogUuid,
        documentUuid = UUIDUtils.fromString(params.documentUuid),
        isNewConversation = params.isNewConversation,
        isChat = params.bundle.getString(RecipientSelectionFilterKeys.CALL_FROM.key()) == RecipientSelectionFilterKeys.CHAT.key(),
        containsWorkingGroups = params.containsWorkingGroups(),
        onlyParticipants = params.isOnlyParticipants,
        excludeParticipants = params.isExcludeParticipants,
        canResultBeEmpty = params.canResultBeEmpty(),
        excludeList = arrayListOf<String>(),
        count = RECIPIENT_SELECTION_LIST_SIZE,
        conversationType = params.conversationType,
        requestCode = params.requestCode
    )
}