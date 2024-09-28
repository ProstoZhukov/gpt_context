package ru.tensor.sbis.communicator.sbis_conversation.ui

import android.view.View
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.BaseConversationPresenter
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.DocumentAccessType
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract.ConversationPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationContract.ConversationView
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import java.util.UUID

/**
 * Презентер реестра сообщений,
 * агрегирует презентеры секций тулбара, списка и панели сообщений.
 *
 * @author vv.chekurda
 */
internal class ConversationPresenterImpl(
    private val messagePresenter: ConversationMessagesContract.Presenter<ConversationView>,
    private val panelPresenter: ConversationMessagePanelContract.Presenter<ConversationView>,
    private val toolbarPresenter: ConversationToolbarContract.Presenter<ConversationView>
) : BaseConversationPresenter<ConversationView>(messagePresenter, panelPresenter, toolbarPresenter),
    ConversationPresenter {

    override val conversationUuid: UUID?
        get() = messagePresenter.conversationUuid

    override fun setRouter(router: ConversationRouter?) {
        messagePresenter.setRouter(router)
        panelPresenter.setRouter(router)
        toolbarPresenter.setRouter(router)
    }

    override fun messageFileSigningSuccess() {
        messagePresenter.messageFileSigningSuccess()
        panelPresenter.messageFileSigningSuccess()
    }

    override fun messageFileSigningFailure() {
        messagePresenter.messageFileSigningFailure()
        panelPresenter.messageFileSigningFailure()
    }

    override fun unpinChatMessage() {
        messagePresenter.unpinChatMessage()
    }

    override fun onKeyboardAppears(keyboardHeight: Int) {
        super.onKeyboardAppears(keyboardHeight)
        toolbarPresenter.onKeyboardAppears(keyboardHeight)
    }

    override fun onKeyboardDisappears(keyboardHeight: Int) {
        super.onKeyboardDisappears(keyboardHeight)
        toolbarPresenter.onKeyboardDisappears(keyboardHeight)
    }

    override fun deleteMessageOnlyForMe() {
        messagePresenter.deleteMessageOnlyForMe()
    }

    override fun deleteMessageForAll() {
        messagePresenter.deleteMessageForAll()
    }

    override fun onParticipantsScreenClosed() {
        messagePresenter.onParticipantsScreenClosed()
    }

    override fun close() {
        messagePresenter.close()
    }

    override fun onChatCreatedFromDialog() {
        messagePresenter.onChatCreatedFromDialog()
    }

    override fun onPhoneVerificationRequired(message: CharSequence?) {
        messagePresenter.onPhoneVerificationRequired(message)
    }

    override fun updateList() {
        messagePresenter.updateList()
    }

    override fun showConversationMembers() {
        messagePresenter.showConversationMembers()
    }

    override fun showVerificationPhoneDialog() {
        messagePresenter.showVerificationPhoneDialog()
    }

    override fun onMessageSelected(conversationMessage: ConversationMessage) {
        messagePresenter.onMessageSelected(conversationMessage)
    }

    override fun onMessageErrorStatusClicked(conversationMessage: ConversationMessage) {
        messagePresenter.onMessageErrorStatusClicked(conversationMessage)
    }

    override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) {
        messagePresenter.onMessageAttachmentClicked(message, attachment)
    }

    override fun onMessageQuotedBySwipe(message: ConversationMessage) {
        messagePresenter.onMessageQuotedBySwipe(message)
    }

    override fun onAcceptSigningButtonClicked(data: ConversationMessage) {
        messagePresenter.onAcceptSigningButtonClicked(data)
    }

    override fun onRejectSigningButtonClicked(data: ConversationMessage) {
        messagePresenter.onRejectSigningButtonClicked(data)
    }

    override fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View) {
        messagePresenter.onGrantAccessButtonClicked(data, sender)
    }

    override fun onDenyAccessButtonClicked(data: ConversationMessage) {
        messagePresenter.onDenyAccessButtonClicked(data)
    }

    override fun acceptAccessRequest(message: Message, messagePosition: Int, accessType: DocumentAccessType) {
        messagePresenter.acceptAccessRequest(message, messagePosition, accessType)
    }

    override fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean): Boolean {
        return messagePresenter.onMediaMessageExpandClicked(data, expanded)
    }

    override fun onMediaPlaybackError(error: Throwable) {
        messagePresenter.onMediaPlaybackError(error)
    }

    override fun onLinkClicked() {
        messagePresenter.onLinkClicked()
    }

    override fun onPhotoClicked(senderUuid: UUID) {
        messagePresenter.onPhotoClicked(senderUuid)
    }

    override fun onSenderNameClicked(senderUuid: UUID) {
        messagePresenter.onSenderNameClicked(senderUuid)
    }

    override fun onServiceMessageClicked(position: Int) {
        messagePresenter.onServiceMessageClicked(position)
    }

    override fun onPhoneNumberClicked(phoneNumber: String) {
        messagePresenter.onPhoneNumberClicked(phoneNumber)
    }

    override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) {
        messagePresenter.onPhoneNumberLongClicked(phoneNumber, messageUUID)
    }

    override fun onPhoneNumberActionClick(actionOrder: Int) {
        messagePresenter.onPhoneNumberActionClick(actionOrder)
    }

    override fun onDialogParticipantChoosed(profileUuid: UUID) {
        panelPresenter.onDialogParticipantChoosed(profileUuid)
    }

    override fun onSignMenuItemClicked() {
        panelPresenter.onSignMenuItemClicked()
    }

    override fun onRequestSignatureMenuItemClicked() {
        panelPresenter.onRequestSignatureMenuItemClicked()
    }

    override fun onSignAndRequestMenuItemClicked() {
        panelPresenter.onSignAndRequestMenuItemClicked()
    }

    override fun onChangeRecipientsClick() {
        panelPresenter.onChangeRecipientsClick()
    }

    override fun onRecipientsChangingCanceled() {
        panelPresenter.onRecipientsChangingCanceled()
    }

    override fun onDeleteDialog() {
        panelPresenter.onDeleteDialog()
    }

    override fun onDialogDeletingConfirmed() {
        panelPresenter.onDialogDeletingConfirmed()
    }

    override fun onDialogDeletingClicked() {
        panelPresenter.onDialogDeletingClicked()
    }

    override fun isRecipientSelectionClosed(): Boolean =
        panelPresenter.isRecipientSelectionClosed()

    override fun onBackPressed(): Boolean =
        panelPresenter.onBackPressed() || toolbarPresenter.onBackPressed()

    override fun onRecordCompleted() {
        panelPresenter.onRecordCompleted()
    }

    override fun onAudioRecordStateChanged(state: AudioRecordViewState) {
        panelPresenter.onAudioRecordStateChanged(state)
    }

    override fun onVideoRecordStateChanged(state: VideoRecordViewState) {
        panelPresenter.onVideoRecordStateChanged(state)
    }

    override fun onCancelRecordingDialogResult(isConfirmed: Boolean) {
        toolbarPresenter.onCancelRecordingDialogResult(isConfirmed)
    }

    override fun onTitlePhotoClick() {
        toolbarPresenter.onTitlePhotoClick()
    }

    override fun setDialogTitle(text: String) {
        toolbarPresenter.setDialogTitle(text)
    }

    override fun onCompleteTitleEditClicked(newTitle: CharSequence) {
        toolbarPresenter.onCompleteTitleEditClicked(newTitle)
    }

    override fun onToolbarClick() {
        toolbarPresenter.onToolbarClick()
    }

    override fun openDocument() {
        toolbarPresenter.openDocument()
    }

    override fun onToolbarMenuIconClicked() {
        toolbarPresenter.onToolbarMenuIconClicked()
    }

    override fun onQuitAndHideChatConfirmed() {
        toolbarPresenter.onQuitAndHideChatConfirmed()
    }

    override fun onQuitChatConfirmed() {
        toolbarPresenter.onQuitChatConfirmed()
    }

    override fun onHideChatConfirmed() {
        toolbarPresenter.onHideChatConfirmed()
    }

    override fun onConversationOptionSelected(conversationOption: ConversationOption) {
        toolbarPresenter.onConversationOptionSelected(conversationOption)
    }

    override fun restorePopupMenuVisibility(wasVisible: Boolean) {
        toolbarPresenter.restorePopupMenuVisibility(wasVisible)
    }

    override fun beforeToolbarClick() {
        toolbarPresenter.beforeToolbarClick()
    }

    override fun onTitleTextClick() {
        toolbarPresenter.onTitleTextClick()
    }

    override fun onViewerSliderClosed() {
        messagePresenter.onViewerSliderClosed()
    }

    override fun onDeleteUploadClicked(message: ConversationMessage, attachmentModel: AttachmentModel) {
        messagePresenter.onDeleteUploadClicked(message, attachmentModel)
    }

    override fun onRetryUploadClicked(message: ConversationMessage, attachmentModel: AttachmentModel) {
        messagePresenter.onRetryUploadClicked(message, attachmentModel)
    }

    override fun onErrorUploadClicked(
        message: ConversationMessage,
        attachmentModel: AttachmentModel,
        errorMessage: String
    ) {
        messagePresenter.onErrorUploadClicked(message, attachmentModel, errorMessage)
    }

    override fun onThreadMessageClicked(data: ConversationMessage) {
        messagePresenter.onThreadMessageClicked(data)
    }

    override fun onThreadCreationServiceClicked(data: ConversationMessage) {
        messagePresenter.onThreadCreationServiceClicked(data)
    }
}