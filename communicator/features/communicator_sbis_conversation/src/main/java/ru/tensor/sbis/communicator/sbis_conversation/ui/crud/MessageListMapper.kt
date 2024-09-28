package ru.tensor.sbis.communicator.sbis_conversation.ui.crud

import android.content.Context
import androidx.tracing.Trace
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage

/**
 * Маппер списка сообщений
 */
internal class MessageListMapper(
    context: Context,
    private val messageMapper: MessageMapper
) : BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<ConversationMessage>>(context) {

    /** @SelfDocumented */
    override fun apply(rawList: ListResultOfMessageMapOfStringString): PagedListResult<ConversationMessage> {
        Trace.beginAsyncSection("MessageMapper.prefetch map", 0)
        var prevTimestamp: Long? = null
        val result = rawList.result.reversed().map {
            val item = messageMapper.map(it, prevTimestamp)
            prevTimestamp = it.timestampSent
            item
        }.reversed()
        Trace.endAsyncSection("MessageMapper.prefetch map", 0)
        return PagedListResult(
            result,
            rawList.haveMore,
            rawList.metadata
        )
    }
}