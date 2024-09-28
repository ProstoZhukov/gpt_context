package ru.tensor.sbis.communicator.base.conversation.presentation.presenter

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListSizeSettings
import ru.tensor.sbis.communicator.generated.HierarchyPaginationOfMessageMessageAnchor
import ru.tensor.sbis.communicator.generated.MessageAnchor
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.PaginationUnitOfMessageMessageAnchor
import ru.tensor.sbis.service.generated.DirectionType
import java.util.UUID

/**
 * Фильтр для работы с коллекцией сообщений.
 *
 * @author vv.chekurda
 */
open class MessageCollectionFilter {

    private lateinit var themeUuid: UUID
    private var anchorMessage: UUID? = null

    var isGroupConversation: Boolean = false
        private set

    fun setThemeUuid(uuid: UUID): MessageCollectionFilter = apply {
        themeUuid = uuid
    }

    fun setAnchorMessage(uuid: UUID?): MessageCollectionFilter = apply {
        anchorMessage = uuid
    }

    fun setIsGroupConversation(isGroupConversation: Boolean): MessageCollectionFilter = apply {
        this.isGroupConversation = isGroupConversation
    }

    open fun getMessageFilter(
        fromUuid: UUID? = this.anchorMessage,
        pageSize: Int = ConversationListSizeSettings.listSize,
        requestId: String? = null
    ): MessageFilter =
        MessageFilter(themeUuid).also {
            it.fromUuid = fromUuid
            it.count = pageSize
            it.includeAnchor = true
            it.groupServiceMessages = true
            it.reversed = true
            it.direction = getDirection(fromUuid).toDirection()
            it.isGroupConversation = isGroupConversation
            it.requestId = requestId.orEmpty()
        }

    fun getPaginationAnchor(): HierarchyPaginationOfMessageMessageAnchor =
        HierarchyPaginationOfMessageMessageAnchor(
            arrayListOf(
                PaginationUnitOfMessageMessageAnchor().also {
                    with(it.pagination) {
                        anchor = anchorMessage?.let(::MessageAnchor)
                        direction = getDirection(anchorMessage)
                        pageSize = ConversationListSizeSettings.listSize.toLong()
                    }
                }
            )
        )

    private fun getDirection(anchorMessage: UUID?) =
        if (anchorMessage != null) {
            DirectionType.BOTHWAY
        } else {
            DirectionType.FORWARD
        }

    private fun DirectionType.toDirection(): QueryDirection =
        when (this) {
            DirectionType.FORWARD -> QueryDirection.TO_OLDER
            DirectionType.BACKWARD -> QueryDirection.TO_NEWER
            DirectionType.BOTHWAY -> QueryDirection.TO_BOTH
        }
}