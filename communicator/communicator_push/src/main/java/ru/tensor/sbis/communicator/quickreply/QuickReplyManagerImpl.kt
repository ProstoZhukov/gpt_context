package ru.tensor.sbis.communicator.quickreply

import android.content.Context
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.quickreply.QuickReplyManager.SendCallback
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.user_activity_track.service.UserActivityService
import java.util.Objects

/**
 * Created by aa.mironychev on 14.08.17.
 */
class QuickReplyManagerImpl(
    context: Context,
    controller: DependencyProvider<MessageController>,
    center: PushCenter,
    userActivityService: UserActivityService?
) : QuickReplyManager {
    private val mContext: Context
    private val mController: DependencyProvider<MessageController>
    private val mPushCenter: PushCenter
    private val mUserActivityService: UserActivityService?
    private val disposable = CompositeDisposable()

    override fun sendMessage(quickReplyModel: QuickReplyModel, callback: SendCallback?) {
        // Sending message observable
        val routine = Single.fromCallable {
            val controller = mController.get()
            val status = controller.quickReply(
                Objects.requireNonNull(quickReplyModel.dialogUuid)!!,
                Objects.requireNonNull(quickReplyModel.targetMessage)!!,
                Objects.requireNonNull(quickReplyModel.recipient)!!.uuid,
                Objects.requireNonNull(quickReplyModel.messageUuid)!!
            )
            if (isSuccess(status)) {
                controller.markMessagesAsRead(ArrayList(listOf(quickReplyModel.messageUuid!!)))
                mPushCenter.cancel(
                    PushType.NEW_MESSAGE,
                    PushCancelContract.createDialogParams(quickReplyModel.dialogUuid)
                )
                mUserActivityService?.registerOneTimeActivity("Quick reply to message")
            }
            status
        }
            .doFinally {
                callback?.call()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        // Do on sending finished
        disposable.add(routine.subscribe({ result: CommandStatus -> onSendingResult(result) }) { throwable: Throwable ->
            onSendingException(
                throwable
            )
        })
    }

    // region Callbacks
    private fun onSendingResult(result: CommandStatus) {
        if (!isSuccess(result)) {
            showSendingError(result.errorMessage)
        }
    }

    private fun onSendingException(throwable: Throwable) {
        showSendingError(throwable.message)
    }

    private fun showSendingError(errorMessage: String?) {
        if (!CommonUtils.isEmpty(errorMessage)) {
            SbisPopupNotification.pushToast(mContext, errorMessage!!)
        } else {
            SbisPopupNotification.pushToast(mContext, R.string.communicator_failed_to_send_quick_message)
        }
    }

    // endregion
    override fun close() {
        disposable.dispose()
    }

    companion object {
        // endregion
        // region Utility methods
        private fun isSuccess(status: CommandStatus?): Boolean {
            return status != null && status.errorCode == ErrorCode.SUCCESS
        }
    }

    init {
        mContext = context.applicationContext
        mController = controller
        mPushCenter = center
        mUserActivityService = userActivityService
    }
}