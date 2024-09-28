package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.contract.MessageServiceDependency
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper

/**
 * Реализация поставщика зависимостей для работы с микросервисом сообщений
 *
 * @author vv.chekurda
 */
class CommunicatorMessageServiceDependency(
    controllerProvider: DependencyProvider<MessageController> = DependencyProvider.create(MessageController::instance),
    dialogControllerProvider: DependencyProvider<DialogController> = DependencyProvider.create(DialogController::instance)
) : MessageServiceDependency<MessageResult, SendMessageResult, CommunicatorDraftMessage> {

    override val serviceWrapper: MessageServiceWrapper<MessageResult, SendMessageResult, CommunicatorDraftMessage> =
        CommunicatorMessageServiceWrapper(controllerProvider, dialogControllerProvider)

    override val messageResultHelper: MessageResultHelper<MessageResult, SendMessageResult> =
        CommunicatorMessageResultHelper()

    override val draftResultHelper: DraftResultHelper<CommunicatorDraftMessage> =
        CommunicatorDraftResultHelper()
}