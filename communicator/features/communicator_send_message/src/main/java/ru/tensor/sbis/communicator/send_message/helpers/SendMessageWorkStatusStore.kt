package ru.tensor.sbis.communicator.send_message.helpers

import android.content.Context
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.send_message.model.SendMessageWorkStatus
import ru.tensor.sbis.communicator.send_message.model.SendMessageWorkStatus.CANCELED_BY_USER
import ru.tensor.sbis.communicator.send_message.model.SendMessageWorkStatus.NOT_SENT
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import timber.log.Timber
import java.util.UUID

/**
 * Хранилище данных о статусе выполнения работы [SendMessageWorker].
 * Нужен для корректной работы алгоритма по отправке сообщения этим воркером после перезапуска системой.
 *
 * @author dv.baranov
 */
internal class SendMessageWorkStatusStore(private val context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(SEND_MESSAGE_WORKER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    /**
     * Сохранить статус работы и ,если нужно, UUID отправляемого сообщения в sharedPreferences.
     */
    internal fun saveWorkStatusAndMessageUUID(workUUID: UUID, workStatus: SendMessageWorkStatus, messageUUID: UUID? = null) {
        Timber.v("$STORE_TAG - try to save $messageUUID and $workStatus for work with uuid $workUUID")
        sharedPreferences
            .edit()
            .putString(UUIDUtils.toString(workUUID), workStatus.toString())
            .putString(getMessageUUIDKey(workUUID), UUIDUtils.toString(messageUUID) ?: "")
            .apply()
    }

    /**
     * Удалить информацию о статусе работы.
     */
    internal fun removeWorkStatusAndMessageUUID(workUUID: UUID) {
        Timber.v("$STORE_TAG - try to remove status of work with uuid $workUUID")
        sharedPreferences
            .edit()
            .remove(UUIDUtils.toString(workUUID))
            .remove(getMessageUUIDKey(workUUID))
            .apply()
    }

    /**
     * Получить статус [SendMessageWorkStatus] работы и uuid отправляемого сообщения, если он известен.
     */
    internal fun getStatusAndAssociatedMessageUuid(workUUID: UUID): Pair<SendMessageWorkStatus, UUID?> {
        val status = getWorkStatus(workUUID)
        val messageUUID = getMessageUUID(workUUID)
        Timber.v("$STORE_TAG - получили статус работы $workUUID - $status и messsageUUID: $messageUUID")
        return Pair(status, messageUUID)
    }

    /**
     * Получить сохраненное значение uuid отправляемого сообщения, если оно сохранено.
     */
    internal fun getMessageUUID(workUUID: UUID): UUID? {
        val messageId = sharedPreferences.getString(getMessageUUIDKey(workUUID), "")
        return if (messageId.isNullOrEmpty()) null else UUIDUtils.fromString(messageId)
    }

    /**
     * Получить UUID работы по UUID отправляемого сообщения.
     * Используется в случае удаления сообщения из реестра, тогда нужно удалить пуш.
     */
    internal fun getWorkUUID(messageUUID: UUID): UUID? {
        val workId = sharedPreferences.all.keys.find {
            UUIDUtils.equals(
                sharedPreferences.getString(it, ""),
                messageUUID
            )
        }
        return UUIDUtils.fromString(workId?.removePrefix(WORK_MESSAGE_UUID_PREFIX))
    }

    /**
     * Получить информацию о том, что была ли отменена работа пользователем.
     */
    internal fun isCanceledByUser(workUUID: UUID): Boolean {
        val status = getWorkStatus(workUUID)
        return status == CANCELED_BY_USER
    }

    private fun getWorkStatus(workUUID: UUID): SendMessageWorkStatus {
        val defValue = NOT_SENT.toString()
        return SendMessageWorkStatus.valueOf(
            sharedPreferences.getString(UUIDUtils.toString(workUUID), defValue) ?: defValue
        )
    }

    private fun getMessageUUIDKey(workUUID: UUID) = "$WORK_MESSAGE_UUID_PREFIX${UUIDUtils.toString(workUUID)}"
}

private const val WORK_MESSAGE_UUID_PREFIX = "message_uuid_"
private const val SEND_MESSAGE_WORKER_SHARED_PREFERENCES = "SEND_MESSAGE_WORKER_SHARED_PREFERENCES"

private const val STORE_TAG = "SEND_MESSAGE_WORKER_STATUS_STORE"
