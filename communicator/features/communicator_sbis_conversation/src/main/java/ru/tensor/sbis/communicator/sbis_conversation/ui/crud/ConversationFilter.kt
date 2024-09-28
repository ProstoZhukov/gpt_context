package ru.tensor.sbis.communicator.sbis_conversation.ui.crud

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.base.conversation.data.model.crud.BaseConversationFilter
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import java.util.*
import kotlin.collections.ArrayList

/**
 * CRUD-фильтр для реестра сообщений
 */
internal class ConversationFilter : BaseConversationFilter() {

    var unfoldedGroupServiceMessage: ArrayList<ServiceMessageGroup>? = null

    override fun queryBuilder(): Builder<ConversationMessage, MessageFilter> =
        ConversationFilterBuilder(themeUuid, relevantMessageUuid, unfoldedGroupServiceMessage)

    class ConversationFilterBuilder internal constructor(
        themeUuid: UUID,
        relevantMessageUuid: UUID?,
        private var unfoldedGroupServiceMessage: ArrayList<ServiceMessageGroup>?,
        requestId: String? = null
    ) : BaseConversationFilterBuilder<ConversationMessage>(themeUuid, relevantMessageUuid, requestId) {

        override fun build(): MessageFilter =
            MessageFilter(
                themeId = themeUuid,
                direction = queryDirection ?: QueryDirection.TO_BOTH,
                fromUuid = fromUUID,
                count = mItemsCount,
                includeAnchor = inclusive,
                requestId = requestId ?: "",
                unfoldedGroups = unfoldedGroupServiceMessage ?: ArrayList(),
                groupServiceMessages = true,
                parentId = null,
                startCrmConsultationText = null,
                crmConsultationIconType = CrmConsultationIconType.UNKNOWN,
                reversed = true,
                isGroupConversation = false,
                isBothway = false
            )
    }
}