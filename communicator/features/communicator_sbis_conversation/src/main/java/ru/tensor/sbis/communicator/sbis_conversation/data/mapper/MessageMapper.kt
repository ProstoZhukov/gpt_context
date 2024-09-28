package ru.tensor.sbis.communicator.sbis_conversation.data.mapper

import android.content.Context
import androidx.tracing.Trace
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communication_decl.communicator.media.getServiceObject
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.generated.DecoratedOfMessage
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationServiceMessage
import ru.tensor.sbis.communicator.sbis_conversation.utils.TaskServiceContentHelper.getTaskServiceTextModel
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.design.message_view.mapper.MessageViewDataMapper
import ru.tensor.sbis.design.message_view.model.MessageViewData
import ru.tensor.sbis.design.message_view.model.ServiceViewData
import ru.tensor.sbis.design.message_view.model.getMediaMessageData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.communicator.generated.Message as ControllerMessage

/**
 * Маппер модели сообщения контроллера [ControllerMessage] в нашу модель [Message].
 *
 * @author dv.baranov
 */
internal class MessageMapper(
    context: Context,
    var isGroupDialog: Boolean = false,
    var isChannel: Boolean = false,
    optimizeConvert: Boolean = false
) : BaseModelMapper<ControllerMessage, ConversationMessage>(context),
    ItemMapper<DecoratedOfMessage, ConversationMessage, TupleOfUuidOptionalOfBool> {

    private val viewDataMapper: MessageViewDataMapper =
        singletonComponent.dependency
            .messageViewComponentsFactory
            .createMessageViewDataMapper(context, isChannel, false, optimizeConvert)

    private val ControllerMessage.senderData: PersonData
        get() = PersonData(
            uuid = sender.uuid,
            photoUrl = sender.photoUrl,
            initialsStubData = sender.photoDecoration?.let { InitialsStubData(it.initials, it.backgroundColorHex) }
        )

    private val ControllerMessage.isQuotable: Boolean
        get() = quotable && (syncStatus == SyncStatus.SUCCEEDED || syncStatus == SyncStatus.IN_PROGRESS)

    private val ControllerMessage.isAuthorBlocked: Boolean
        get() = singletonComponent.dependency.complainServiceProvider
            ?.getComplainService()?.isPersonBlocked(sender.uuid) ?: false

    override fun map(
        item: DecoratedOfMessage,
        actionDelegate: ItemActionDelegate<DecoratedOfMessage, TupleOfUuidOptionalOfBool>
    ): ConversationMessage {
        Trace.beginAsyncSection("MessageMapper.map", 0)
        val message = requireNotNull(item.origin)
        message.serviceMessageGroup?.folded = !item.isExpanded
        val result = prepareConversationMessage(
            item = item,
            message = map(message),
            actionDelegate = actionDelegate
        )
        Trace.endAsyncSection("MessageMapper.map", 0)
        return result
    }

    override fun apply(controllerMessage: ControllerMessage): ConversationMessage {
        return map(controllerMessage)
    }

    fun map(
        controllerMessage: ControllerMessage,
        previousItemTimestamp: Long? = MessageViewDataMapper.AUTO_FORMATTED_DATE_TIME
    ): ConversationMessage {
        getTaskServiceTextModel(controllerMessage)?.also(controllerMessage::textModel::set)
        val viewData = viewDataMapper.map(
            controllerMessage,
            isGroupDialog,
            previousItemTimestamp = previousItemTimestamp
        )
        val (message, serviceMessage) = getConversationMessageData(controllerMessage, viewData)
        return ConversationMessage(
            message = message,
            conversationServiceMessage = serviceMessage,
            viewData = viewData,
            isChannel = isChannel
        )
    }

    private fun prepareConversationMessage(
        item: DecoratedOfMessage,
        message: ConversationMessage,
        actionDelegate: ItemActionDelegate<DecoratedOfMessage, TupleOfUuidOptionalOfBool>
    ): ConversationMessage {
        val serviceMessage = message.conversationServiceMessage
        val serviceGroup = serviceMessage?.serviceMessageGroup
        return if (serviceGroup != null) {
            message.copy(
                conversationServiceMessage = serviceMessage.copy(
                    expandServiceGroupAction = { actionDelegate.expandFolderClick(item) }
                )
            )
        } else {
            message
        }
    }

    private fun getConversationMessageData(
        controllerMessage: ControllerMessage,
        viewData: MessageViewData
    ): Pair<Message?, ConversationServiceMessage?> =
        if (viewData is ServiceViewData) {
            null to getServiceMessage(controllerMessage, viewData)
        } else {
            getMessage(controllerMessage, viewData) to null
        }

    private fun getMessage(
        controllerMessage: ControllerMessage,
        viewData: MessageViewData
    ): Message {
        val serviceObject = getServiceObject(controllerMessage.serviceObject)
        return Message(
            uuid = controllerMessage.uuid,
            timestamp = controllerMessage.timestamp,
            syncStatus = controllerMessage.syncStatus,
            timestampSent = controllerMessage.timestampSent,
            outgoing = controllerMessage.outgoing,
            forMe = controllerMessage.forMe,
            removableType = controllerMessage.removableType,
            editable = controllerMessage.editable,
            canCreateThread = controllerMessage.canCreateThread,
            isQuotable = controllerMessage.isQuotable,
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
            mediaMessageData = viewData.getMediaMessageData(),
            threadInfo = ThreadInfo.fromServiceObject(serviceObject, isChannel),
            isAuthorBlocked = controllerMessage.isAuthorBlocked
        ).also {
            it.isMeetingInviteAnswer = serviceObject?.optString("serviceType")
                ?.equals("meeting-invite-answer") == true
        }
    }

    private fun getServiceMessage(
        controllerMessage: ControllerMessage,
        viewData: ServiceViewData
    ): ConversationServiceMessage =
        ConversationServiceMessage(
            uuid = viewData.uuid,
            timestampSent = controllerMessage.timestampSent,
            forMe = controllerMessage.forMe,
            outgoing = controllerMessage.outgoing,
            read = controllerMessage.read,
            serviceMessageGroup = viewData.serviceMessageGroup,
            serviceMessage = viewData.serviceMessage
        )

    /** @SelfDocumented */
    fun apply(inputMessageList: ArrayList<ControllerMessage>): List<ConversationMessage> =
        inputMessageList.map(::apply)

    /** @SelfDocumented */
    fun clearReferences() {
        viewDataMapper.clearReferences()
    }
}