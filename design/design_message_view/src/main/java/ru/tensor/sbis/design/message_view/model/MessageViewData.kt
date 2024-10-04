package ru.tensor.sbis.design.message_view.model

import android.text.Spannable
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.communicator.generated.CrmConsultationIconType
import ru.tensor.sbis.communicator.generated.ServiceMessage
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageViewData
import ru.tensor.sbis.design.cloud_view.thread.data.ThreadData
import ru.tensor.sbis.design.message_view.content.crm_views.chat_bot_buttons.ChatBotButtonsParams
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ServiceRateData
import ru.tensor.sbis.design.message_view.content.service_views.ChatServiceMessageClickableSpan
import ru.tensor.sbis.design.message_view.model.MessageType.*
import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.design.message_view.utils.equals
import ru.tensor.sbis.design.video_message_view.message.data.VideoMessageViewData
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Модели данных для каждой из ячеек, отображающихся в [MessageView].
 *
 * @author dv.baranov
 */

/**
 * Публичный интерфейс даты MessageView.
 */
sealed interface MessageViewData : CoreMessageData, ComparableItem<MessageViewData> {

    /** Тип ячейки. */
    val type: MessageType

    override fun areTheSame(otherItem: MessageViewData): Boolean = uuid == otherItem.uuid
}

/**
 * Перечисление всех видов ячеек.
 */
enum class MessageType {
    OUTCOME_MESSAGE,
    INCOME_MESSAGE,
    OUTCOME_VIDEO_MESSAGE,
    INCOME_VIDEO_MESSAGE,
    INCOME_RATE_MESSAGE,
    OUTCOME_RATE_MESSAGE,
    CHAT_BOT_BUTTONS,
    SERVICE_MESSAGE,
    SERVICE_MATERIALS_MESSAGE,
    THREAD_MESSAGE,
    THREAD_CREATION_MESSAGE,
    GREETINGS_BUTTONS
}

/**
 * Базовый интерфейс моделей ячеек, использующих CloudView.
 */
internal sealed interface CloudViewData : MessageViewData

/**
 * Модель обычных сообщений.
 */
internal data class MessageCloudViewData(
    private val messageData: CoreMessageData,
    val audioViewData: AudioMessageViewData? = null
) : CloudViewData, CoreMessageData by messageData {
    override val type: MessageType = if (outgoing) OUTCOME_MESSAGE else INCOME_MESSAGE
}

/**
 * Модель сообщений с рейтингом.
 */
internal data class RateCloudViewData(
    private val messageData: CoreMessageData,
    val serviceRateData: ServiceRateData? = null
) : CloudViewData, CoreMessageData by messageData {
    override val type: MessageType = if (outgoing) OUTCOME_RATE_MESSAGE else INCOME_RATE_MESSAGE
}

/**
 * Модель кнопок чат-бота.
 */
internal data class ChatBotViewData(
    private val messageData: CoreMessageData,
    val chatBotButtonsParams: ChatBotButtonsParams? = null
) : CloudViewData, CoreMessageData by messageData {
    override val type: MessageType = CHAT_BOT_BUTTONS
}

/**
 * Модель видеосообщений.
 */
internal data class VideoCloudViewData(
    private val messageData: CoreMessageData,
    val videoViewData: VideoMessageViewData
) : CloudViewData, CoreMessageData by messageData {
    override val type: MessageType = if (outgoing) OUTCOME_VIDEO_MESSAGE else INCOME_VIDEO_MESSAGE
}

/**
 * Модель сервисных сообщений с контентом.
 */
internal data class ServiceMaterialsViewData(
    private val messageData: CoreMessageData
) : CloudViewData, CoreMessageData by messageData {
    override val type: MessageType = SERVICE_MATERIALS_MESSAGE
}

/**
 * Модель сервисных сообщений.
 */
data class ServiceViewData internal constructor(
    private val messageData: CoreMessageData,
    val serviceMessage: ServiceMessage? = null,
    val serviceMessageGroup: ServiceMessageGroup? = null,
    var clickableSpan: ChatServiceMessageClickableSpan? = null,
    val text: Spannable,
    val icon: CrmConsultationIconType?,
    val rateServiceMessage: ServiceRateData? = null,
    val isOperator: Boolean
) : MessageViewData, CoreMessageData by messageData {
    override val type: MessageType = SERVICE_MESSAGE
    val isServiceGroup = serviceMessageGroup != null
    val isFoldedServiceMessageGroup: Boolean = serviceMessageGroup?.folded == true

    override fun equals(other: Any?): Boolean =
        other is ServiceViewData && text.contentEquals(other.text) &&
            serviceMessage equals other.serviceMessage &&
            serviceMessageGroup equals other.serviceMessageGroup &&
            EqualsBuilder()
                .append(clickableSpan, other.clickableSpan)
                .append(icon, other.icon)
                .append(rateServiceMessage, other.rateServiceMessage)
                .append(isOperator, other.isOperator)
                .append(messageData, other.messageData)
                .isEquals

    override fun hashCode(): Int =
        HashCodeBuilder()
            .append(serviceMessage)
            .append(serviceMessageGroup)
            .append(clickableSpan)
            .append(text)
            .append(icon)
            .append(rateServiceMessage)
            .append(isOperator)
            .append(type)
            .append(isServiceGroup)
            .append(isFoldedServiceMessageGroup)
            .toHashCode()
}

/**
 * Модель ячеек тредов.
 */
internal data class ThreadViewData(
    private val messageData: CoreMessageData,
    val threadData: ThreadData
) : MessageViewData, CoreMessageData by messageData {
    override val type: MessageType = THREAD_MESSAGE
}

/**
 * Модель сервисных сообщений о создании треда.
 */
internal data class ThreadCreationViewData(
    private val messageData: CoreMessageData,
    val text: CharSequence?
) : MessageViewData, CoreMessageData by messageData {
    override val type: MessageType = THREAD_CREATION_MESSAGE
}

/**
 * Модель кнопок приветствий.
 */
internal data class GreetingsViewData(
    val greetings: List<String>,
    private val messageData: CoreMessageData = CoreMessageDataImpl()
) : MessageViewData, CoreMessageData by messageData {
    override val type: MessageType = GREETINGS_BUTTONS

    override fun hasTheSameContent(otherItem: MessageViewData): Boolean =
        otherItem is GreetingsViewData && greetings.size == otherItem.greetings.size &&
            greetings.containsAll(otherItem.greetings)
}
