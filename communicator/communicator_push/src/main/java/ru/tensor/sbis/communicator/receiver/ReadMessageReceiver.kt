package ru.tensor.sbis.communicator.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import io.reactivex.Completable
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.di.CommunicatorPushComponent
import ru.tensor.sbis.communicator.di.quickreply.DaggerQuickReplyComponent
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import java.util.*

/**
 * Ресивер для действия "Прочитать" в пуше сообщения
 */
internal class ReadMessageReceiver: EntryPointBroadcastReceiver() {

    private fun getMessageController(context: Context): DependencyProvider<MessageController> {
        return CommunicatorCommonComponent.getInstance(context)
            .messageController
    }

    private fun getPushCenter(context: Context): PushCenter {
        return DaggerQuickReplyComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(context))
            .userActivityService(CommunicatorPushComponent.getInstance(context).dependency.userActivityService)
            .build()
            .pushCenter
    }

    /**@SelfDocumented */
    @SuppressLint("CheckResult")
    override fun onReady(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        Completable.fromRunnable {
            val dialogUuid = intent.getSerializableExtra(PUSH_DIALOG_UUID_KEY) as UUID?
            val messageUuid = intent.getSerializableExtra(READ_PUSH_MESSAGE_UUID_KEY) as UUID?

            if (dialogUuid != null && messageUuid != null) {
                val messagesController = getMessageController(context).get()
                messagesController.markReadOnPushNotification(dialogUuid, messageUuid)
            }

            getPushCenter(context).cancel(PushType.NEW_MESSAGE, PushCancelContract.createDialogParams(dialogUuid))
            pendingResult.finish()
        }
            .subscribeOn(Schedulers.io())
            .subscribe { Functions.EMPTY_ACTION }
    }

    companion object {
        const val PUSH_DIALOG_UUID_KEY: String = "dialog_uuid"
        const val READ_PUSH_MESSAGE_UUID_KEY: String = "message_uuid"
    }
}