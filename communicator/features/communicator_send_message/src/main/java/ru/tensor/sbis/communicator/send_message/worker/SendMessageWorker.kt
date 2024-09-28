package ru.tensor.sbis.communicator.send_message.worker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.generated.SyncStatus.ERROR
import ru.tensor.sbis.common.generated.SyncStatus.IN_PROGRESS
import ru.tensor.sbis.common.generated.SyncStatus.SENDING
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.send_message.R
import ru.tensor.sbis.communicator.send_message.SendMessagePlugin.sendMessageComponent
import ru.tensor.sbis.communicator.send_message.SendMessagePlugin.themedAppContext
import ru.tensor.sbis.communicator.send_message.helpers.ATTACHMENTS
import ru.tensor.sbis.communicator.send_message.helpers.CONVERSATION_UUID
import ru.tensor.sbis.communicator.send_message.helpers.DATA_MESSAGE_SENT_KEY
import ru.tensor.sbis.communicator.send_message.helpers.DATA_MESSAGE_UUID_KEY
import ru.tensor.sbis.communicator.send_message.helpers.DOCUMENT_UUID
import ru.tensor.sbis.communicator.send_message.helpers.MESSAGE_TEXT
import ru.tensor.sbis.communicator.send_message.helpers.RECIPIENTS
import ru.tensor.sbis.communicator.send_message.helpers.SendMessageWorkStatusStore
import ru.tensor.sbis.communicator.send_message.helpers.SendMessagesStatusCheckHelper.getMessageSyncStatus
import ru.tensor.sbis.communicator.send_message.helpers.SendMessagesStatusCheckHelper.subscribeDataRefreshedEvent
import ru.tensor.sbis.communicator.send_message.helpers.buildInputData
import ru.tensor.sbis.communicator.send_message.helpers.buildMessageSentData
import ru.tensor.sbis.communicator.send_message.helpers.buildMessageUUIDData
import ru.tensor.sbis.communicator.send_message.interactor.use_case.SendMessageUseCaseImpl
import ru.tensor.sbis.communicator.send_message.model.SendMessageWorkStatus
import ru.tensor.sbis.communicator.send_message.reciever.CancelSendMessageReceiver
import ru.tensor.sbis.communicator.send_message.reciever.CancelSendMessageReceiver.Companion.CANCEL_PUSH_CONVERSATION_UUID_KEY
import ru.tensor.sbis.communicator.send_message.reciever.CancelSendMessageReceiver.Companion.CANCEL_PUSH_MESSAGE_UUID_KEY
import ru.tensor.sbis.communicator.send_message.reciever.CancelSendMessageReceiver.Companion.CANCEL_PUSH_WORK_ID
import ru.tensor.sbis.entrypoint_guard.work.EntryPointCoroutineWorker
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider.getColor
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider.getSmallIconRes
import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.design.R as RDesign

/**
 * Класс для запуска фоновой отправки сообщения с текстом и/или вложениями.
 *
 * @author dv.baranov
 */
class SendMessageWorker(
    context: Context,
    workerParams: WorkerParameters,
) : EntryPointCoroutineWorker(context, workerParams) {

    companion object {

        /**
         * Запустить отправку сообщения в фоне.
         *
         * @param conversationUUID идентификатор переписки.
         * @param documentUUID идентификатор документа, к которму прикреплен диалог.
         * @param recipients список идентификаторов получателей сообщения.
         * @param messageText текст сообщения.
         * @param attachments список вложений.
         * @return true, когда работа по отправке сообщения выполнена
         */
        fun sendMessage(
            context: Context,
            conversationUUID: UUID? = null,
            documentUUID: UUID? = null,
            recipients: List<UUID> = emptyList(),
            messageText: String = EMPTY,
            attachments: List<Uri> = emptyList(),
        ) {
            val data = buildInputData(conversationUUID, documentUUID, recipients, messageText, attachments)
            val work = OneTimeWorkRequest.Builder(SendMessageWorker::class.java)
                .setInputData(data)
                .addTag(SEND_MESSAGE_WORK_TAG)
                .build()
            WorkManager.getInstance(context.applicationContext).enqueue(work)
        }

        /**
         * Отменить все процессы отправки сообщений в фоне.
         */
        fun cancelAllMessagesSending(context: Context) {
            WorkManager.getInstance(context.applicationContext)
                .cancelAllWorkByTag(SEND_MESSAGE_WORK_TAG)
        }

        /**
         * Отменить процесс отправки сообщения в фоне.
         *
         * @param id - идентификатор работы.
         */
        fun cancelMessageSending(context: Context, id: UUID) {
            SendMessageWorkStatusStore(context).saveWorkStatusAndMessageUUID(
                id,
                SendMessageWorkStatus.CANCELED_BY_USER,
            )
            WorkManager.getInstance(context.applicationContext)
                .cancelWorkById(id)
        }

        /**
         * Отменить процесс отправки сообщения в фоне по его uuid.
         */
        fun cancelMessageSendingByUUID(context: Context, messageUuid: UUID) {
            val sendMessageWorkStatusStore = SendMessageWorkStatusStore(context.applicationContext)
            val workUUID = sendMessageWorkStatusStore.getWorkUUID(messageUuid)
            if (workUUID != null) {
                cancelMessageSending(context, workUUID)
            } else {
                Timber.w("$WORKER_TAG - Can't cancel sending of message with uuid: $messageUuid")
            }
        }
    }

    private val sendMessageUseCase = SendMessageUseCaseImpl()
    private val sendMessageWorkStatusStore = SendMessageWorkStatusStore(applicationContext)
    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val notificationOldChannelId = NotificationChannelUtils.buildChannelId(SEND_MESSAGE_CHANNEL_ID)
    private val notificationNewChannelId = NotificationChannelUtils.buildChannelId(SEND_MESSAGE_NEW_CHANNEL_ID)
    private val sendMessageTitle = applicationContext.getString(R.string.send_message_notification_title)

    override suspend fun onReady(): Result {
        val subscription = subscribeMessageDataRefresh()
        return try {
            setForeground(getForegroundInfo())
            val result: Result = startSendingMessageAndWaitForResult()
            subscription.disable()
            endCurrentWork(true)
            result
        } catch (ex: Exception) {
            val isCanceled = sendMessageWorkStatusStore.isCanceledByUser(id)
            if (isCanceled) {
                Timber.i("$WORKER_TAG - work was canceled by user")
            } else {
                Timber.e(ex, WORKER_TAG)
            }
            subscription.disable()
            endCurrentWork(isCanceled || !isStopped)
            Result.failure()
        }
    }

    private fun subscribeMessageDataRefresh(): Subscription {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        return subscribeDataRefreshedEvent(object : DataRefreshedMessageControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                val messageId = param[SEND_MESSAGE_ID_KEY]
                val messageStatus = param[SEND_MESSAGE_STATUS_EVENT_KEY]
                if (messageStatus?.isNotEmpty() == true) {
                    messageId?.let {
                        val messageUuid = sendMessageWorkStatusStore.getMessageUUID(id)
                        if (UUIDUtils.equals(messageUuid, messageId)) {
                            scope.launch {
                                setProgress(buildMessageSentData(messageUuid!!))
                                scope.cancel()
                            }
                        }
                    }
                }
            }
        })
    }

    private suspend fun startSendingMessageAndWaitForResult(): Result = withContext(Dispatchers.IO) {
        val workStatusFromStore = sendMessageWorkStatusStore.getStatusAndAssociatedMessageUuid(id)
        logIfWorkRestartedBySystem(workStatusFromStore)
        return@withContext when (workStatusFromStore.first) {
            SendMessageWorkStatus.NOT_SENT -> sendMessageAndWaitResult()
            SendMessageWorkStatus.NEED_WAIT_FOR_RESULT -> onlyWaitResult(workStatusFromStore.second)
            SendMessageWorkStatus.RESULT_SUCCESS -> Result.success()
            SendMessageWorkStatus.RESULT_FAILURE -> Result.failure()
            SendMessageWorkStatus.CANCELED_BY_USER -> {
                Timber.e("$WORKER_TAG - workStatus is 'canceled by user', this shouldn't happen")
                Result.failure()
            }
        }
    }

    private fun logIfWorkRestartedBySystem(status: Pair<SendMessageWorkStatus, UUID?>) {
        if (runAttemptCount > 1) {
            Timber.i("$WORKER_TAG - system restarted the job with status ${status.first} and messageUUID:${status.second}")
        }
    }

    private suspend fun sendMessageAndWaitResult(): Result {
        val dialogAndMessageUuids = sendMessage(inputData)
        val messageUuid = dialogAndMessageUuids.second
        if (messageUuid == null) {
            Timber.e("$WORKER_TAG - messageUUID is null")
            return Result.failure()
        }
        showMessageSendNotification(
            dialogAndMessageUuids.first,
            messageUuid,
        )
        return waitForResultOfSendingMessage()
    }

    private suspend fun onlyWaitResult(messageUuid: UUID?): Result {
        val conversationUuid =
            inputData.getString(CONVERSATION_UUID)?.let(UUIDUtils::fromString)
        if (messageUuid == null) {
            Timber.e("$WORKER_TAG - messageUUID is null")
            return Result.failure()
        }
        showMessageSendNotification(conversationUuid, messageUuid)
        setProgress(buildMessageSentData(messageUuid))
        return waitForResultOfSendingMessage()
    }

    private suspend fun sendMessage(inputData: Data): Pair<UUID?, UUID?> {
        val conversationUuid = inputData.getString(CONVERSATION_UUID)?.let(UUIDUtils::fromString)
        val documentUuid = inputData.getString(DOCUMENT_UUID)?.let(UUIDUtils::fromString)
        val messageText = inputData.getString(MESSAGE_TEXT) ?: EMPTY
        val recipients = inputData.getStringArray(RECIPIENTS)!!.toList().map(UUIDUtils::fromString)
        val attachments = inputData.getStringArray(ATTACHMENTS)!!.toList().map { it.toUri() }
        val resultOfSending = if (conversationUuid == null && documentUuid == null) {
            sendMessageUseCase.sendNewDialogMessage(
                recipients,
                messageText,
                attachments,
            )
        } else {
            sendMessageUseCase.sendConversationMessage(
                conversationUuid,
                documentUuid,
                messageText,
                attachments,
                recipients,
            )
        }
        resultOfSending.second?.let {
            saveWorkStatusAndMessageUUID(
                SendMessageWorkStatus.NEED_WAIT_FOR_RESULT,
                it,
            )
        }
        return resultOfSending
    }

    private fun showMessageSendNotification(dialogUUID: UUID?, messageUuid: UUID?) {
        Timber.v("$WORKER_TAG - вызов notificationManager.notify() для показа пуша")
        val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(
                getNotificationIdOfCurrentWork(),
                createSendMessageNotification(dialogUUID, messageUuid),
            )
        }
    }

    private fun endCurrentWork(needClearStore: Boolean = false) {
        Timber.v("$WORKER_TAG - вызвали endCurrentWork() должен исчезнуть пуш")
        notificationManager.cancel(getNotificationIdOfCurrentWork())
        if (needClearStore) {
            sendMessageWorkStatusStore.removeWorkStatusAndMessageUUID(id)
        }
    }

    //region Worker Notifications
    override suspend fun getForegroundInfo(): ForegroundInfo {
        Timber.v("$WORKER_TAG - вызвали getForegroundInfo() должен появиться пуш")
        submitNotificationChannel()
        return ForegroundInfo(getNotificationIdOfCurrentWork(), createSendMessageNotification())
    }

    private fun createSendMessageNotification(conversationUuid: UUID? = null, messageUuid: UUID? = null): Notification {
        val context = themedAppContext ?: applicationContext
        return NotificationCompat.Builder(applicationContext, notificationNewChannelId).run {
            color = getColor(context)
            setSmallIcon(getSmallIconRes(context))
            priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager.IMPORTANCE_HIGH
            } else {
                Notification.PRIORITY_HIGH
            }
            setSound(null)
            setCategory(Notification.CATEGORY_SERVICE)
            setContentTitle(sendMessageTitle)
            setProgress(1, 0, true)
            setContentText("")
            addAction(buildCancelSendMessageAction(conversationUuid, messageUuid))
            build()
        }
    }

    private fun submitNotificationChannel() {
        if (NotificationChannelUtils.isSupportChannels()) {
            /*
            Из-за того, что решили показывать пуш в шторке (https://online.sbis.ru/opendoc.html?guid=0dac34c3-ac37-48a7-9a77-58fa2c2f4de3&client=3)
            пришлось повысить importance канала. Без замены старого канала на новый не обойтись:
            Register the channel with the system. You can't change the importance
            or other notification behaviors after this. (https://developer.android.com/develop/ui/views/notifications/channels)
            */
            val oldChannel = notificationManager.getNotificationChannel(notificationOldChannelId)
            if (oldChannel != null) {
                notificationManager.deleteNotificationChannel(notificationOldChannelId)
            }
            NotificationChannelUtils.submitChannel(
                applicationContext,
                NotificationChannel(
                    notificationNewChannelId,
                    sendMessageTitle,
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    enableLights(false)
                    enableVibration(false)
                    setShowBadge(false)
                },
            )
        }
    }

    private fun buildCancelSendMessageAction(conversationUuid: UUID?, messageUuid: UUID?): NotificationCompat.Action {
        val pendingIntent = buildCancelSendMessageIntent(conversationUuid, messageUuid)
        val actionLabel = applicationContext.getString(RDesign.string.design_cancel_item_label)
        val builder = NotificationCompat.Action.Builder(
            null,
            actionLabel,
            pendingIntent,
        )
        return builder.build()
    }

    private fun buildCancelSendMessageIntent(conversationUuid: UUID?, messageUuid: UUID?): PendingIntent {
        val commonBundle = Bundle().apply {
            putSerializable(CANCEL_PUSH_CONVERSATION_UUID_KEY, conversationUuid)
            putSerializable(CANCEL_PUSH_MESSAGE_UUID_KEY, messageUuid)
            putSerializable(CANCEL_PUSH_WORK_ID, id)
        }
        val intent = Intent(applicationContext, CancelSendMessageReceiver::class.java)
        intent.putExtras(commonBundle)
        return sendMessageComponent
            .pushIntentHelper
            .getUpdateCurrentBroadcastMutable(getUpdateBroadcastRequestCodeOfCurrentWork(), intent)
    }
    // endregion

    private suspend fun waitForResultOfSendingMessage(): Result = withContext(Dispatchers.IO) {
        var result: Result? = null
        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(id).asFlow()
            .takeWhile {
                !isStopped && result == null
            }
            .collect { info ->
                if (info.progress.getBoolean(DATA_MESSAGE_SENT_KEY, false)) {
                    val messageUuid = UUIDUtils.fromString(info.progress.getString(DATA_MESSAGE_UUID_KEY))
                    do {
                        val syncStatusOfCurrentMessage = messageUuid?.let { getMessageSyncStatus(it) }
                        if (syncStatusOfCurrentMessage?.isSendCompleted() == false) {
                            delay(CHECK_RESULTS_OF_MESSAGES_DELAY)
                            continue
                        } else {
                            result = if (syncStatusOfCurrentMessage == ERROR) {
                                Result.failure()
                            } else {
                                Result.success()
                            }
                            saveWorkStatusAndMessageUUID(result!!.toSendMessageWorkStatus())
                        }
                    } while (!isStopped && result == null)
                    if (result == null) {
                        result = Result.failure()
                    }
                    // Тут нужно пропихивать данные, отличные от предыдущих, чтобы liveData обновилась
                    // и можно было выйти из flow.
                    setProgress(buildMessageUUIDData(messageUuid))
                }
            }
        return@withContext result ?: Result.failure()
    }

    private fun saveWorkStatusAndMessageUUID(workStatus: SendMessageWorkStatus, messageUuid: UUID? = null) {
        sendMessageWorkStatusStore.saveWorkStatusAndMessageUUID(
            id,
            workStatus,
            messageUuid,
        )
    }

    private fun SyncStatus.isSendCompleted(): Boolean = this != IN_PROGRESS && this != SENDING

    private fun Result.toSendMessageWorkStatus(): SendMessageWorkStatus = when (this) {
        Result.success() -> SendMessageWorkStatus.RESULT_SUCCESS
        Result.failure() -> SendMessageWorkStatus.RESULT_FAILURE
        else -> {
            Timber.e("$WORKER_TAG - result isn't success or failure. Result is $this")
            SendMessageWorkStatus.NOT_SENT
        }
    }

    private fun getNotificationIdOfCurrentWork(): Int = id.hashCode()

    private fun getUpdateBroadcastRequestCodeOfCurrentWork(): Int = -id.hashCode()
}

private val SEND_MESSAGE_WORK_TAG = SendMessageWorker::class.java.name
private const val CHECK_RESULTS_OF_MESSAGES_DELAY = 2000L
private const val SEND_MESSAGE_CHANNEL_ID = "send_message_token"
private const val SEND_MESSAGE_NEW_CHANNEL_ID = "send_message_new_token"
private const val SEND_MESSAGE_ID_KEY = "message_id"
private const val SEND_MESSAGE_STATUS_EVENT_KEY = "message_status"

private const val WORKER_TAG = "SEND_MESSAGE_WORKER_MAIN"
