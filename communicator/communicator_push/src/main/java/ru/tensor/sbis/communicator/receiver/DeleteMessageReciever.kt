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
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import timber.log.Timber
import java.util.*

/**
 * Ресивер для действия "Удалить" в пуше сообщения
 */
internal class DeleteMessageReceiver: EntryPointBroadcastReceiver() {

    private fun getMessageController(context: Context): DependencyProvider<MessageController> =
        CommunicatorCommonComponent.getInstance(context).messageController

    private fun getDialogController(context: Context): DependencyProvider<DialogController> =
        CommunicatorCommonComponent.getInstance(context).dialogController

    private fun getPushCenter(context: Context): PushCenter {
        return DaggerQuickReplyComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(context))
            .userActivityService(CommunicatorPushComponent.getInstance(context).dependency.userActivityService)
            .build()
            .pushCenter
    }

    @SuppressLint("CheckResult")
    override fun onReady(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        Completable.fromRunnable {
            val dialogUuid = intent.getSerializableExtra(DELETE_PUSH_DIALOG_UUID_KEY) as UUID?
            val messageUuid = intent.getSerializableExtra(DELETE_PUSH_MESSAGE_UUID_KEY) as UUID?
            val isComment = intent.getBooleanExtra(DELETE_PUSH_IS_COMMENT, false)
            val isArticleDiscussionMessage = intent.getBooleanExtra(DELETE_PUSH_ARTICLE_DISCUSSION, false)

            if (isComment || isArticleDiscussionMessage) {
                if (dialogUuid != null) {
                    getDialogController(context).get().deleteSocnetDialogOnPushNotification(dialogUuid)
                }
            } else if (dialogUuid != null && messageUuid != null) {
                getMessageController(context).get().run {
                    markMessagesAsRead(arrayListOf(messageUuid))
                    deleteMessagesForMeOnly(dialogUuid, arrayListOf(messageUuid))
                }
            }

            getPushCenter(context).cancel(PushType.NEW_MESSAGE, PushCancelContract.createDialogParams(dialogUuid))
            pendingResult.finish()
        }
            .subscribeOn(Schedulers.io())
            .subscribe(Functions.EMPTY_ACTION, { Timber.e(it) })
    }

    companion object {
        const val DELETE_PUSH_DIALOG_UUID_KEY: String = "dialog_uuid"
        const val DELETE_PUSH_MESSAGE_UUID_KEY: String = "message_uuid"
        const val DELETE_PUSH_IS_COMMENT: String = "is_comment"
        const val DELETE_PUSH_ARTICLE_DISCUSSION = "is_article_discussion"
    }
}
