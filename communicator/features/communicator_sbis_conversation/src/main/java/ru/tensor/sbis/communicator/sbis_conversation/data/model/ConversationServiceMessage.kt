package ru.tensor.sbis.communicator.sbis_conversation.data.model

import ru.tensor.sbis.communicator.base.conversation.data.model.BaseServiceMessage
import ru.tensor.sbis.communicator.generated.ServiceMessage
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import java.util.*

/** Дата-класс модели сервисного сообщения */
internal data class ConversationServiceMessage(
    val uuid: UUID,
    val timestampSent: Long,
    val forMe: Boolean,
    val outgoing: Boolean,
    override var read: Boolean,
    override val serviceMessageGroup: ServiceMessageGroup? = null,
    val serviceMessage: ServiceMessage? = null,
    override val expandServiceGroupAction: () -> Unit = {}
) : BaseServiceMessage {

    var formattedDateTime: FormattedDateTime? = null
    var isFoldedServiceMessageGroup: Boolean = serviceMessageGroup?.folded  == true

    fun isServiceGroup() = serviceMessageGroup != null
}
