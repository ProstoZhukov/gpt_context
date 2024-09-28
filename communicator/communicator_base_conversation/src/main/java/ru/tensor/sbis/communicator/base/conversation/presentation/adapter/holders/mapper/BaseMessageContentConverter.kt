@file:Suppress("unused")

package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.mapper

import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.design.cloud_view.model.DefaultPersonModel
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.profile_decl.person.PersonData
import java.util.*

/**
 * Маппер модели отправителя сообщения
 */
val Message.cloudSenderPersonModel: PersonModel
    get() = DefaultPersonModel(
        senderViewData,
        cloudSenderName
    )

/**
 * Маппер модели данных о получателе сообщения
 */
val Message.cloudReceiverInfo: ReceiverInfo
    get() = ReceiverInfo(
        DefaultPersonModel(
            PersonData(),
            cloudReceiverName
        ),
        receiverCount
    )

/**
 * Дата или время в замисимости от того было отправлено сообщение сегодня или нет
 */
val Message.dateOrTimeString: String
    get() = when (DateFormatUtils.isTheSameDay(Date(timestampSent), Date())) {
        true -> formattedDateTime?.time ?: EMPTY
        else -> formattedDateTime?.date ?: EMPTY
    }

/**
 * Маппер строки имени отправителя формата
 * Епанчин И.
 */
private val Message.cloudSenderName: String
    get() {
        val personName = senderName
        val senderNameBuilder = StringBuilder()
        senderNameBuilder.append(personName.last)
        val name = personName.first
        if (name.isNotEmpty()) {
            senderNameBuilder
                .append(' ')
                .append(name.substring(0, 1))
                .append(".")
        }
        return senderNameBuilder.toString()
    }

/**
 * Маппер строки получателя сообщения формата
 * Епанчин И.
 */
private val Message.cloudReceiverName: String
    get() {
        val recipientsBuilder = StringBuilder()
        val surname = receiverLastName
        if (receiverCount > 0 && !surname.isNullOrEmpty()) {
            recipientsBuilder.append(surname)
            val name = receiverName
            if (!name.isNullOrEmpty()) {
                recipientsBuilder
                    .append(' ')
                    .append(name, 0, 1)
                    .append(".")
            }
        }
        return recipientsBuilder.toString()
    }
