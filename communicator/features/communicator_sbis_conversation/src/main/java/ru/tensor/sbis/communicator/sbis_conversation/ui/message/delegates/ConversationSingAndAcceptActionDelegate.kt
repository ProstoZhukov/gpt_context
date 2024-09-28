package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import android.view.View
import io.reactivex.Single
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.mapNotNull
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.DocumentAccessType
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageAccessButtonListener
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationSingAndAcceptHandler
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.design.SbisMobileIcon
import timber.log.Timber

/**
 * Делегат реестра сообщений для работы с подписями и разрешениями.
 *
 * @author da.zhukov
 */
internal class ConversationSingAndAcceptActionDelegate(
    private val interactor: ConversationInteractor
) : ConversationMessagesBaseDelegate(),
    MessageAccessButtonListener,
    ConversationSingAndAcceptHandler {

    private var messageToSign: Message? = null

    //region signs

    override fun messageFileSigningSuccess() {
        messageToSign?.let {
            interactor.onMessageSigningSuccess(it)
                .doFinally { messageToSign = null }
                .subscribe(::handleSigningResult) {
                    error -> Timber.e(error, "Error trying onMessageSignedSuccess")
                }
                .storeIn(compositeDisposable)
        }
    }

    override fun messageFileSigningFailure() {
        messageToSign = null
    }

    fun signMessage(messageToSign: Message?) {
        this.messageToSign = messageToSign
        view?.run {
            val fileInfoViewModels = messageToSign!!.content.mapNotNull {
                it.attachment?.fileInfoViewModel
            }
            interactor.getAttachmentsUuidsToSign(fileInfoViewModels)
                .subscribe { attachmentsUuids ->
                    if (attachmentsUuids.isEmpty()) {
                        showToast(R.string.communicator_attachments_loading_to_sign)
                    } else {
                        showAttachmentsSigning(attachmentsUuids)
                    }
                }
                .storeIn(compositeDisposable)
        }
    }

    fun onRejectSigningButtonClicked(data: ConversationMessage) {
        handleRejectAction(data) { message ->
            interactor.rejectSignature(message)
        }
    }

    private fun handleSigningResult(status: CommandStatus) {
        when (status.errorCode) {
            ErrorCode.NETWORK_ERROR,
            ErrorCode.OTHER_ERROR -> view?.showError(status.errorMessage)
            else                  -> Unit
        }
    }

    //region access

    override fun acceptAccessRequest(message: Message, messagePosition: Int, accessType: DocumentAccessType) {
        view?.showProgressInAcceptButton(true, messagePosition)
        interactor.acceptAccessRequest(message, accessType)
            .subscribe({ handleGrantAccessResult(it, messagePosition) }, { handleGrantAccessError(it.message, messagePosition) })
            .storeIn(compositeDisposable)
    }

    override fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View) {
        val model = data.message ?: return
        val adapterPosition = view?.getAdapterPosition(data) ?: -1
        if (adapterPosition >= 0) {
            view?.showGrantAccessMenu(model, adapterPosition, sender)
        }
    }

    override fun onDenyAccessButtonClicked(data: ConversationMessage) {
        handleRejectAction(data) { message ->
            interactor.declineAccessRequest(message)
        }
    }

    private fun handleRejectAction(message: ConversationMessage, rejectAction: (Message) -> Single<CommandStatus>) {
        val adapterPosition = view?.getAdapterPosition(message) ?: - 1
        if (adapterPosition >= 0) {
            view?.showProgressInRejectButton(true, adapterPosition)
            rejectAction(message.message!!)
                .subscribe(
                    { handleRejectionResult(it, adapterPosition) },
                    { handleRejectionError(it.message, adapterPosition) }
                ).storeIn(compositeDisposable)
        }
    }

    private fun handleRejectionResult(status: CommandStatus, messagePosition: Int) {
        when (status.errorCode) {
            ErrorCode.NO_ATTACHED_PHONE -> onUnattachedPhoneError(messagePosition)
            ErrorCode.NETWORK_ERROR,
            ErrorCode.OTHER_ERROR -> handleRejectionError(status.errorMessage, messagePosition)
            else -> Unit
        }
    }

    private fun handleRejectionError(errorMessage: String?, messagePosition: Int) {
        view?.showProgressInRejectButton(false, messagePosition)
        errorMessage?.let { view?.showToast(it) }
    }

    private fun onUnattachedPhoneError(messagePosition: Int) {
        view?.run {
            showProgressInRejectButton(false, messagePosition)
            showUnattachedPhoneError()
        }
    }

    private fun handleGrantAccessResult(status: CommandStatus, messagePosition: Int) {
        if (status.errorCode == ErrorCode.SUCCESS) {
            // В случае успеха звать перезапрос данных не нужно, должен прийти рефреш колбэк
        } else {
            handleGrantAccessError(status.errorMessage, messagePosition)
        }
    }

    private fun handleGrantAccessError(errorMessage: String?, messagePosition: Int) {
        errorMessage ?: return
        val isNetworkError = errorMessage == view?.getStringRes(R.string.communicator_sync_error_message)
        view?.showErrorPopup(errorMessage, if (isNetworkError) SbisMobileIcon.Icon.smi_WiFiNone.character.toString() else null)
        view?.showProgressInAcceptButton(false, messagePosition)
        view?.showError(errorMessage)
    }
}