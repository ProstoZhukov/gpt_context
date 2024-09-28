package ru.tensor.sbis.communicator.sbis_conversation.adapters.utils

import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.design.cloud_view.model.DefaultPersonModel
import ru.tensor.sbis.design.cloud_view.model.PersonModel

/**
 * Маппер модели отправителя сообщения.
 */
internal val ConversationMessage.cloudSenderPersonModel: PersonModel
    get() = DefaultPersonModel(
        this.message!!.senderViewData,
        cloudSenderName(isChannel)
    )

/**
 * Маппер строки имени отправителя формата
 * Епанчин И. или Епанчин Иван
 */
private fun ConversationMessage.cloudSenderName(isChannel: Boolean): String {
    val message = this.message!!
    val receiverIsEmpty = message.receiverName.isNullOrEmpty() && message.receiverLastName.isNullOrEmpty()
    val isLongName = isChannel && receiverIsEmpty
    val personName = message.senderName
    val senderNameBuilder = StringBuilder()
    senderNameBuilder.append(personName.last)
    val name = personName.first
    if (name.isNotEmpty()) {
        senderNameBuilder
            .append(' ')
            .doIf(isLongName) { append(name) }
            .doIf(!isLongName) {
                append(name.substring(0, 1))
                append(".")
            }
    }
    return senderNameBuilder.toString()
}