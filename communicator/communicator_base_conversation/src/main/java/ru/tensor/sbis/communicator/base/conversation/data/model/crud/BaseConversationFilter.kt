package ru.tensor.sbis.communicator.base.conversation.data.model.crud

import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import java.io.Serializable
import java.util.*

/**
 * Базовая реализация фильтра для запросов сообщений.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationFilter : Serializable, ListFilter() {

    lateinit var themeUuid: UUID
    var relevantMessageUuid: UUID? = null

    /**
     * Возвращает true, если свойство themeUuid проинициализировано.
     */
    fun isThemeUuidInitialized(): Boolean = ::themeUuid.isInitialized

    abstract class BaseConversationFilterBuilder<MESSAGE : BaseConversationMessage> constructor(
        protected var themeUuid: UUID,
        private var relevantMessageUuid: UUID?,
        protected var requestId: String? = null
    ) : Builder<MESSAGE, MessageFilter>() {

        fun relevantMessageUuid(uuid: UUID?): BaseConversationFilterBuilder<MESSAGE> = apply {
            relevantMessageUuid = uuid
        }

        fun requestId(id: String?): BaseConversationFilterBuilder<MESSAGE> = apply {
            requestId = id
        }

        protected val fromUUID: UUID? get() = relevantMessageUuid ?: mAnchorModel?.uuid
        protected val queryDirection: QueryDirection? get() = QueryDirection.TO_BOTH.takeIf { relevantMessageUuid != null } ?: mDirection
        protected val inclusive: Boolean get() = mInclusive.takeUnless { QueryDirection.TO_BOTH == queryDirection } ?: true
    }
}