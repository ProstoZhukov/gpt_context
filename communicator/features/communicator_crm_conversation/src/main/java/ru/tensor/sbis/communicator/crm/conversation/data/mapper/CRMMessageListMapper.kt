package ru.tensor.sbis.communicator.crm.conversation.data.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.mvp.data.model.PagedListResult
import javax.inject.Inject

/**
 * Маппер списка сообщений.
 *
 * @author da.zhukov
 */
internal class CRMMessageListMapper @Inject constructor(
    context: Context,
    private val messageMapper: CRMMessageMapper
) : BaseModelMapper<ListResultOfMessageMapOfStringString, PagedListResult<CRMConversationMessage>>(context) {

    /** @SelfDocumented */
    override fun apply(rawList: ListResultOfMessageMapOfStringString): PagedListResult<CRMConversationMessage> =
        PagedListResult(
            rawList.result.map { messageMapper.apply(it) },
            rawList.haveMore,
            rawList.metadata ?: hashMapOf()
        )
}