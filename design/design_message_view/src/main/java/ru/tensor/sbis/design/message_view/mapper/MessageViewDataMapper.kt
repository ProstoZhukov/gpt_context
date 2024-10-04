package ru.tensor.sbis.design.message_view.mapper

import android.content.Context
import android.text.Spannable
import androidx.core.util.ObjectsCompat.requireNonNull
import org.apache.commons.lang3.StringUtils.EMPTY
import org.json.JSONObject
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communication_decl.communicator.media.getServiceObject
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.design.cloud_view.model.DefaultPersonModel
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.thread.data.ThreadDataMapper
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.message_view.contact.MessageViewDataMapperFactory
import ru.tensor.sbis.design.message_view.content.crm_views.chat_bot_buttons.getChatBotButtonsParamsFromServiceObject
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.createServiceRateData
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.getRateData
import ru.tensor.sbis.design.message_view.content.service_views.findServiceMessageContentItem
import ru.tensor.sbis.design.message_view.content.service_views.getServiceMessageText
import ru.tensor.sbis.design.message_view.content.service_views.isMessageContainsServiceMessageWithContent
import ru.tensor.sbis.design.message_view.content.service_views.isService
import ru.tensor.sbis.design.message_view.content.threads.getThreadInfoFromServiceObject
import ru.tensor.sbis.design.message_view.model.ChatBotViewData
import ru.tensor.sbis.design.message_view.model.CoreMessageData
import ru.tensor.sbis.design.message_view.model.CoreMessageDataImpl
import ru.tensor.sbis.design.message_view.model.MessageCloudViewData
import ru.tensor.sbis.design.message_view.model.MessageContent
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.RateCloudViewData
import ru.tensor.sbis.design.message_view.model.ServiceMaterialsViewData
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.model.ThreadCreationViewData
import ru.tensor.sbis.design.message_view.model.ThreadViewData
import ru.tensor.sbis.design.message_view.model.VideoCloudViewData
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.MediaMessageDataMapper
import ru.tensor.sbis.design.message_view.utils.rich_text_converter.MessageRichTextConverterImpl
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.text_span.span.SbisSpannableString
import java.util.Date
import java.util.UUID

/**
 * Маппер преобразующий модель [Message] контроллера в модель [MessageViewData]
 * для отображения ячейки в [MessageView].
 *
 * @author dv.baranov
 */
class MessageViewDataMapper private constructor(
    private val context: Context,
    private val isChat: Boolean = false,
    private val isCrmMessageForOperator: Boolean = false,
    optimizeConvert: Boolean = false
) {
    private val threadDataMapper = ThreadDataMapper
    private val dateFormatter = ListDateFormatter.DateTimeWithTodayShort(context)
    private val converter = MessageRichTextConverterImpl(context, optimizeConvert)
    private val mediaMessageDataMapper = MediaMessageDataMapper(context)

    companion object : MessageViewDataMapperFactory {

        const val AUTO_FORMATTED_DATE_TIME = -1L

        override fun createMessageViewDataMapper(
            context: Context,
            isChat: Boolean,
            isCrmMessageForOperator: Boolean,
            optimizeConvert: Boolean
        ): MessageViewDataMapper =
            MessageViewDataMapper(context, isChat, isCrmMessageForOperator, optimizeConvert)
    }

    /** @SelfDocumented */
    @JvmOverloads
    fun map(
        message: Message,
        isGroupConversation: Boolean = false,
        isOutcomeMessageWithAuthor: Boolean = false,
        previousItemTimestamp: Long? = AUTO_FORMATTED_DATE_TIME
    ): MessageViewData {
        message.inGroupConversation = message.inGroupConversation || isGroupConversation
        // Получение isServiceRateMessage должно быть раньше, чем вызов prepareRateMessageContent
        val isServiceRateMessage = message.content
            .any { it.serviceType == ServiceType.CONSULATION_RATE }
        val serviceObject = getServiceObject(message.serviceObject)
        val baseViewData = message.asBaseViewData(
            serviceObject,
            isOutcomeMessageWithAuthor,
            previousItemTimestamp
        )

        val videoViewData = mediaMessageDataMapper.getVideoMessageViewData(baseViewData, serviceObject)

        val chatBotMessage = message.content
            .any { it.serviceType == ServiceType.CHATBOT_MESSAGE }
        val chatBotButtonsParams = if (chatBotMessage) {
            getChatBotButtonsParamsFromServiceObject(serviceObject)
        } else {
            null
        }

        val threadData = threadDataMapper.getThreadData(
            serviceObject,
            message.timestamp,
            message.outgoing,
            message.inGroupConversation
        )
        val threadInfo = getThreadInfoFromServiceObject(serviceObject, isChat)

        val isServiceRateRequestMessage = message.content
            .any { it.serviceType == ServiceType.CONSULTATION_RATE_REQUEST }
        val serviceRateData = if (isServiceRateMessage || isServiceRateRequestMessage) {
            message.createServiceRateData(
                isRateRequest = isServiceRateRequestMessage,
                isCrmMessageForOperator = isCrmMessageForOperator
            )
        } else {
            null
        }

        fun isRateMessage() = isServiceRateMessage && serviceRateData != null

        val viewData = when {
            isMessageContainsServiceMessageWithContent(message, serviceObject) -> {
                ServiceMaterialsViewData(messageData = baseViewData)
            }

            videoViewData != null -> {
                VideoCloudViewData(messageData = baseViewData, videoViewData = videoViewData)
            }

            threadData != null -> {
                ThreadViewData(
                    messageData = baseViewData,
                    threadData = threadData
                )
            }

            threadInfo != null -> {
                ThreadCreationViewData(
                    messageData = baseViewData,
                    text = message.content.find { it.serviceType != null }
                        ?.serviceMessage?.text
                        ?: EMPTY
                )
            }

            chatBotMessage && chatBotButtonsParams?.titles?.isNotEmpty() == true -> {
                ChatBotViewData(
                    messageData = baseViewData,
                    chatBotButtonsParams = chatBotButtonsParams
                )
            }

            isRateMessage() -> {
                RateCloudViewData(
                    messageData = baseViewData,
                    serviceRateData = serviceRateData
                )
            }

            message.isService() -> {
                val serviceItem = requireNonNull(findServiceMessageContentItem(message, converter))
                val (text, clickableSpan) = getServiceMessageText(serviceItem, context)
                ServiceViewData(
                    messageData = baseViewData,
                    serviceMessage = serviceItem.serviceMessage,
                    serviceMessageGroup = serviceItem.serviceMessageGroup,
                    clickableSpan = clickableSpan,
                    text = text,
                    icon = serviceItem.serviceMessage?.crmConsultationIconType,
                    rateServiceMessage = serviceRateData,
                    isOperator = isCrmMessageForOperator
                )
            }

            else -> {
                MessageCloudViewData(
                    messageData = baseViewData,
                    audioViewData = mediaMessageDataMapper.getAudioMessageViewData(baseViewData, serviceObject)
                )
            }
        }

        return viewData
    }

    fun clearReferences() {
        converter.clearReferences()
    }

    private fun Message.prepareRateMessageContent(): Spannable? {
        !content.any { it.serviceType == ServiceType.CONSULATION_RATE } && return null
        apply {
            content = arrayListOf(MessageContentItem())
            var comment = getRateData()?.optString("comment") ?: EMPTY
            if (comment == "null") comment = EMPTY
            textModel = "[[\"p\", {\"version\": \"2\"}, \"$comment\"]]"
            forMe = isCrmMessageForOperator
            outgoing = !isCrmMessageForOperator
            return SbisSpannableString(comment)
        }
    }

    private val Message.cloudSenderPersonModel: PersonModel
        get() = DefaultPersonModel(
            cloudSenderViewData,
            cloudSenderName
        )

    /**
     * Маппер модели для отображения фотографии отправителя
     */
    private val Message.cloudSenderViewData: PersonData
        get() = PersonData(
            sender.uuid,
            sender.photoUrl,
            initialsStubData = sender.photoDecoration?.let { InitialsStubData(it.initials, it.backgroundColorHex) }
        )

    /**
     * Маппер строки имени отправителя формата
     * Епанчин И.
     */
    private val Message.cloudSenderName: String
        get() {
            val personName = sender.name
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
     * Маппер модели данных о получателе сообщения
     */
    private val Message.cloudReceiverInfo: ReceiverInfo
        get() = ReceiverInfo(
            DefaultPersonModel(
                PersonData(),
                cloudReceiverName
            ),
            receiverCount
        )

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

    private val Message.sendingState: SendingState
        get() = when {
            syncStatus == SyncStatus.ERROR -> SendingState.NEEDS_MANUAL_SEND
            syncStatus == SyncStatus.SENDING -> SendingState.SENDING
            readByReceiver -> SendingState.IS_READ
            else -> SendingState.SENT
        }

    private fun Message.asBaseViewData(
        serviceObject: JSONObject?,
        isOutcomeMessageWithAuthor: Boolean = false,
        previousItemTimestamp: Long? = AUTO_FORMATTED_DATE_TIME
    ): CoreMessageData =
        CoreMessageDataImpl(
            uuid = getServiceMessageGroupUuid() ?: uuid,
            groupConversation = inGroupConversation,
            isOutcomeMessageWithAuthor = isOutcomeMessageWithAuthor,
            outgoing = outgoing,
            edited = edited,
            sendingState = sendingState,
            readByReceiver = readByReceiver,
            isQuotable = quotable,
            senderPersonModel = cloudSenderPersonModel,
            receiverInfo = cloudReceiverInfo,
            textModel = textModel,
            messageText = prepareRateMessageContent() ?: converter.getRichTextMessage(this, serviceObject),
            serviceObject = this.serviceObject,
            messageContent = MessageContent(content, rootElements),
            formattedDateTime = getFormattedDateTime(previousItemTimestamp)
        )

    private fun Message.getServiceMessageGroupUuid(): UUID? =
        content.find {
            it.itemType == MessageContentItemType.SERVICE_MESSAGE_GROUP
        }?.serviceMessageGroup?.firstMessageUuid

    /**
     * Получить время дату и время для сообщения.
     *
     * @param previousItemTimestamp - timestamp предыдущего сообщения.
     * AUTO_FORMATTED_DATE_TIME - используем признаки модели сообщения.
     */
    private fun Message.getFormattedDateTime(
        previousItemTimestamp: Long? = AUTO_FORMATTED_DATE_TIME
    ): FormattedDateTime {
        val date = Date(timestampSent)
        return if (previousItemTimestamp == AUTO_FORMATTED_DATE_TIME) {
            FormattedDateTime(
                date = if (firstInDay) dateFormatter.formatDate(date) else EMPTY,
                time = dateFormatter.formatTime(date)
            )
        } else {
            dateFormatter.format(
                date = date,
                previousDate = previousItemTimestamp?.let(::Date)
            )
        }
    }
}