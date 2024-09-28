package ru.tensor.sbis.communicator.crm.conversation.interactor.crud

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import ru.tensor.sbis.platform.generated.Subscription
import java.util.UUID
import javax.inject.Inject

/**
 * CRUD-репозиторий для работы со списком сообщений в CRM.
 */
internal class CRMConversationRepository @Inject constructor(
    private val controller: DependencyProvider<MessageController>,
    private val messageControllerBinaryMapper: MessageControllerBinaryMapper
) : CRUDRepository<Message>,
    BaseListRepository<ListResultOfMessageMapOfStringString, MessageFilter, DataRefreshedMessageControllerCallback> {

    private fun deserializeMessage(bytes: ByteArray) = messageControllerBinaryMapper.map(bytes)

    override fun create(): Message =
        deserializeMessage(controller.get().create())

    override fun read(uuid: UUID): Message =
        deserializeMessage(controller.get().read(uuid)!!)

    override fun readFromCache(uuid: UUID): Message =
        deserializeMessage(controller.get().read(uuid)!!)

    override fun update(entity: Message): Message = throw NotImplementedError()

    override fun delete(uuid: UUID): Boolean =
        controller.get().delete(uuid)

    override fun list(filter: MessageFilter): ListResultOfMessageMapOfStringString {
        val result = controller.get().list(filter)
        result.result.reverse()
        val messages = messageControllerBinaryMapper.map(result)
        return ListResultOfMessageMapOfStringString(messages, result.haveMore, result.metadata)
    }

    override fun refresh(filter: MessageFilter): ListResultOfMessageMapOfStringString {
        val result = controller.get().refresh(filter)
        result.result.reverse()
        val messages = messageControllerBinaryMapper.map(result)
        return ListResultOfMessageMapOfStringString(messages, result.haveMore, result.metadata)
    }

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedMessageControllerCallback): Subscription =
        controller.get().dataRefreshed().subscribe(callback)
}