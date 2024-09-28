package ru.tensor.sbis.communicator.sbis_conversation.ui.crud

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.conversation.data.ListResultOfMessageMapOfStringString
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*

/**
 * CRUD-репозиторй для работы со списком сообщений.
 *
 */
internal class ConversationRepository(
    private val controller: DependencyProvider<MessageController>,
    private val messageControllerBinaryMapper: MessageControllerBinaryMapper,
    private val activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
) :
    CRUDRepository<Message>,
    BaseListRepository<ListResultOfMessageMapOfStringString, MessageFilter, DataRefreshedMessageControllerCallback> {

    fun deserializeMessage(bytes: ByteArray) = messageControllerBinaryMapper.map(bytes)

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
        Timber.d("Messages CRUD list. $filter")
        val result = controller.get().list(filter)
        result.result.reverse()
        val messages = messageControllerBinaryMapper.map(result)
        activityStatusSubscriptionInitializer.initialize(messages.map { it.sender.uuid })
        result.metadata?.put(RESULT_META_FILTER_COUNT_KEY, filter.count.toString())
        return ListResultOfMessageMapOfStringString(messages, result.haveMore, result.metadata)
    }

    override fun refresh(filter: MessageFilter): ListResultOfMessageMapOfStringString {
        Timber.d("Messages CRUD refresh. $filter")
        val result = controller.get().refresh(filter)
        result.result.reverse()
        val messages = messageControllerBinaryMapper.map(result)
        activityStatusSubscriptionInitializer.initialize(messages.map { it.sender.uuid })
        result.metadata?.put(RESULT_META_FILTER_COUNT_KEY, filter.count.toString())
        return ListResultOfMessageMapOfStringString(messages, result.haveMore, result.metadata)
    }

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedMessageControllerCallback): Subscription =
        MessageController.instance().dataRefreshed().subscribe(callback)
}

internal const val RESULT_META_FILTER_COUNT_KEY = "RESULT_META_FILTER_COUNT_KEY"
