package ru.tensor.sbis.communicator.crm.conversation.presentation.adapter

import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageActionsListener
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import java.util.UUID

/**
 * Интерфейс действий по кликам на различные контентные области сообщений чата CRM.
 *
 * @author da.zhukov
 */
internal interface CRMMessageActionsListener : BaseMessageActionsListener<CRMConversationMessage> {

    /**@SelfDocumented */
    fun onRateRequestButtonClicked(
        messageUUID: UUID,
        consultationRateType: ConsultationRateType,
        disableComment: Boolean
    )

    /**
     * Обработать клик на кнопку приветствия.
     */
    fun onGreetingClicked(title: String)

    /**
     * Обработать нажатие на кнопку, присланную чат-ботом.
     *
     * @param serviceMessageUuid идентификатор сообщения, в котором были кнопки.
     * @param title заголовок кнопки.
     */
    fun onChatBotButtonClicked(serviceMessageUuid: UUID, title: String)

    /**
     * Проскролить список сообщений, чтобы увидеть последние актуальные кнопки от чат-бота.
     */
    fun scrollToBotButtons()
}