package ru.tensor.sbis.communicator.send_message.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import ru.tensor.sbis.platform.generated.Subscription
import java.util.UUID

/**
 * Объект для помощи в отслеживании статуса доставки сообщений при шаринге.
 * Используется в [SendMessageWorker].
 *
 * @author dv.baranov
 */
internal object SendMessagesStatusCheckHelper {
    private val messageController by lazy { MessageController.instance() }

    /**
     * Подписаться на события обновления списка сообщений.
     *
     * @param callback Реализация [DataRefreshedMessageControllerCallback] для обработки события обновления.
     */
    fun subscribeDataRefreshedEvent(callback: DataRefreshedMessageControllerCallback): Subscription =
        messageController.dataRefreshed().subscribe(callback)

    /**
     * Получить статус доставки сообщения по его UUID.
     */
    suspend fun getMessageSyncStatus(messageUUID: UUID): SyncStatus =
        withContext(Dispatchers.IO) {
            val binaryMapper = MessageControllerBinaryMapper()
            val resultOfMessageRead = messageController.read(messageUUID)
            val messageResult = resultOfMessageRead?.let { binaryMapper.map(it) }
            return@withContext messageResult?.syncStatus ?: SyncStatus.IN_PROGRESS
        }
}
