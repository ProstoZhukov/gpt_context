package ru.tensor.sbis.communicator.crm.conversation.data.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationFeatureFacade.crmMessageMapperHelper
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMServiceMessage
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.DecoratedOfMessage
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.communicator.generated.Message as ControllerMessage
import ru.tensor.sbis.design.message_view.mapper.MessageViewDataMapper
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.model.getMediaMessageData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import javax.inject.Inject

/**
 * Маппер модели сообщения контроллера [Message] в нашу модель [CRMConversationMessage].
 *
 * @author da.zhukov
 */
internal class CRMMessageMapper @Inject constructor(
    context: Context,
    coreConversationInfo: CRMCoreConversationInfo
) : BaseModelMapper<ControllerMessage, CRMConversationMessage>(context),
    ItemMapper<DecoratedOfMessage, CRMConversationMessage, TupleOfUuidOptionalOfBool> {

    private val messageViewDataMapper: MessageViewDataMapper =
        singletonComponent.dependency
            .messageViewComponentsFactory
            .createMessageViewDataMapper(
                context = context,
                isChat = coreConversationInfo.isChat,
                isCrmMessageForOperator = coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator
            )

    /**
     * Маппер модели для отображения фотографии отправителя
     */
    private val ControllerMessage.senderData: PersonData
        get() = PersonData(
            uuid = sender.uuid,
            photoUrl = sender.photoUrl,
            initialsStubData = sender.photoDecoration?.let { InitialsStubData(it.initials, it.backgroundColorHex) }
        )

    override fun map(
        item: DecoratedOfMessage,
        actionDelegate: ItemActionDelegate<DecoratedOfMessage, TupleOfUuidOptionalOfBool>
    ): CRMConversationMessage =
        apply(requireNotNull(item.origin))

    override fun apply(controllerMessage: ControllerMessage): CRMConversationMessage {
        val viewData = messageViewDataMapper.map(controllerMessage, crmMessageMapperHelper.isGroupConsultation)
        val (message, serviceMessage) = getConversationMessageData(controllerMessage, viewData)
        return CRMConversationMessage(
            message = message,
            conversationServiceMessage = serviceMessage,
            viewData = viewData
        )
    }

    private fun getConversationMessageData(
        controllerMessage: ControllerMessage,
        viewData: MessageViewData
    ): Pair<Message?, CRMServiceMessage?> =
        if (viewData is ServiceViewData) {
            null to getServiceMessage(controllerMessage, viewData)
        } else {
            getMessage(controllerMessage, viewData) to null
        }

    private fun getMessage(
        controllerMessage: ControllerMessage,
        viewData: MessageViewData
    ): Message =
        Message(
            uuid = controllerMessage.uuid,
            timestamp = controllerMessage.timestamp,
            syncStatus = controllerMessage.syncStatus,
            timestampSent = controllerMessage.timestampSent,
            outgoing = controllerMessage.outgoing,
            forMe = controllerMessage.forMe,
            removableType = controllerMessage.removableType,
            editable = controllerMessage.editable,
            canCreateThread = controllerMessage.canCreateThread,
            isQuotable = controllerMessage.quotable,
            edited = controllerMessage.edited,
            read = controllerMessage.read,
            receiverCount = controllerMessage.receiverCount,
            senderViewData = controllerMessage.senderData,
            senderName = controllerMessage.sender.name,
            receiverName = controllerMessage.receiverName,
            receiverLastName = controllerMessage.receiverLastName,
            content = controllerMessage.content,
            rootElements = controllerMessage.rootElements,
            attachmentCount = controllerMessage.attachmentCount,
            isDisabledStyle = controllerMessage.isFinishedSignRequest,
            isDownscaledImages = false,
            pinnable = controllerMessage.pinnable,
            timestampRead = controllerMessage.readTimestamp,
            timestampReadByMe = controllerMessage.readTimestampMe,
            messageText = viewData.messageText,
            readByMe = controllerMessage.readByMe,
            readByReceiver = controllerMessage.readByReceiver,
            mediaMessageData = viewData.getMediaMessageData()
        )

    private fun getServiceMessage(
        controllerMessage: ControllerMessage,
        viewData: ServiceViewData
    ): CRMServiceMessage =
        CRMServiceMessage(
            uuid = controllerMessage.uuid,
            timestampSent = controllerMessage.timestampSent,
            forMe = controllerMessage.forMe,
            outgoing = controllerMessage.outgoing,
            read = controllerMessage.read,
            text = viewData.text,
            icon = viewData.icon,
            serviceMessageGroup = viewData.serviceMessageGroup,
            serviceType = viewData.serviceMessage?.type
        )

    /**
     * Маппинг списка моделей сообщений контроллера в список ui моделей
     */
    fun apply(inputMessageList: List<ControllerMessage>): List<CRMConversationMessage> =
        inputMessageList.map(::apply)
}

/**
 * Хелпер маппера модели сообщения контроллера [Message] в нашу модель [CRMConversationMessage].
 * Необходим для синхронизации состояния isGroupConsultation в CRMConversationDataMapper.
 *
 * @author da.zhukov
 */
internal class CRMMessageMapperHelper {
    var isGroupConsultation: Boolean = false
}