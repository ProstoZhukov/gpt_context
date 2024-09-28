package ru.tensor.sbis.communicator.send_message.reciever

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import io.reactivex.Completable
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import timber.log.Timber
import java.util.*

/**
 * Ресивер для действия "отменить" в пуше сообщения при его отправке
 *
 * @author dv.baranov
 */
internal class CancelSendMessageReceiver : EntryPointBroadcastReceiver() {

    private val messageController by lazy { MessageController.instance() }

    /**@SelfDocumented */
    @SuppressLint("CheckResult")
    override fun onReady(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val id = intent.getSerializableExtra(CANCEL_PUSH_WORK_ID) as UUID?
        id?.let {
            SendMessageWorker.cancelMessageSending(context, it)
        }

        Completable.fromRunnable {
            val dialogUuid = intent.getSerializableExtra(CANCEL_PUSH_CONVERSATION_UUID_KEY) as UUID?
            val messageUuid = intent.getSerializableExtra(CANCEL_PUSH_MESSAGE_UUID_KEY) as UUID?

            if (dialogUuid != null && messageUuid != null) {
                messageController.deleteMessagesForEveryone(dialogUuid, arrayListOf(messageUuid))
            }

            pendingResult.finish()
        }
            .subscribeOn(Schedulers.io())
            .subscribe(Functions.EMPTY_ACTION) { Timber.e(it) }
    }

    companion object {
        const val CANCEL_PUSH_CONVERSATION_UUID_KEY: String = "conversation_uuid"
        const val CANCEL_PUSH_MESSAGE_UUID_KEY: String = "message_uuid"
        const val CANCEL_PUSH_WORK_ID: String = "cancel_push_work_id"
    }
}
