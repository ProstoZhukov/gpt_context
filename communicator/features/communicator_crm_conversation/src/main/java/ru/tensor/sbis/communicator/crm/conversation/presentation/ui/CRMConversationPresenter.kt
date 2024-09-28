package ru.tensor.sbis.communicator.crm.conversation.presentation.ui

import android.os.Bundle
import ru.tensor.sbis.communicator.crm.conversation.presentation.adapter.CRMMessageActionsListener
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.conversation_option.CRMConversationOption
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.BaseConversationPresenter
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationContract.*
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesPresenterContract
import java.util.UUID

/**
 * Реализация презентера чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationPresenter(
    private val messagePresenter: CRMConversationMessagesPresenterContract<CRMConversationViewContract>,
    private val panelPresenter: CRMConversationMessagePanelPresenterContract<CRMConversationViewContract>,
    private val toolbarPresenter: CRMConversationToolbarPresenterContract<CRMConversationViewContract>
) : BaseConversationPresenter<CRMConversationViewContract>(messagePresenter, panelPresenter, toolbarPresenter),
    CRMConversationPresenterContract,
    CRMMessageActionsListener by messagePresenter {

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        messagePresenter.onQuoteClicked(quotedMessageUuid)
    }

    override fun pasteTextInMessagePanel(text: String) {
        panelPresenter.pasteTextInMessagePanel(text)
    }

    override fun replaceTextInMessagePanel(text: String) {
        panelPresenter.replaceTextInMessagePanel(text)
    }

    override fun sendGreetingMessage(text: String) {
        panelPresenter.sendGreetingMessage(text)
    }

    override fun openCRMConversationMenu() {
        toolbarPresenter.openCRMConversationMenu()
    }

    override fun onConversationOptionSelected(option: CRMConversationOption) {
        toolbarPresenter.onConversationOptionSelected(option)
    }

    override fun onToolbarClick() {
        toolbarPresenter.onToolbarClick()
    }

    override fun onReassignCommentResult(result: Bundle) {
        toolbarPresenter.onReassignCommentResult(result)
    }

    override fun openNewConsultation() {
        messagePresenter.openNewConsultation()
    }

    override fun openHistoryView() {
        messagePresenter.openHistoryView()
    }

    override fun onHistoryViewClosed() {
        messagePresenter.onHistoryViewClosed()
    }

    override fun openNextConsultation(chatParams: CRMConsultationParams) {
        messagePresenter.openNextConsultation(chatParams)
    }

    override fun takeConsultation() {
        messagePresenter.takeConsultation()
    }

    override fun reopenConsultation() {
        messagePresenter.reopenConsultation()
    }

    override fun deleteMessageForAll() {
        messagePresenter.deleteMessageForAll()
    }

    override fun deleteMessageOnlyForMe() {
        messagePresenter.deleteMessageOnlyForMe()
    }

    override fun onDialogDeletingClicked() {
        panelPresenter.onDialogDeletingClicked()
    }

    override fun onDialogDeletingConfirmed() {
        panelPresenter.onDialogDeletingConfirmed()
    }

    override suspend fun getGreetings() {
        messagePresenter.getGreetings()
    }

    override val isConsultationCompleted: Boolean
        get() = messagePresenter.isConsultationCompleted

    override fun insertGreetingsButtonsInMessageList(withNotify: Boolean) {
        messagePresenter.insertGreetingsButtonsInMessageList(withNotify)
    }

    override fun showQuickReplyView() {
        messagePresenter.showQuickReplyView()
    }

    override fun hideQuickReplyView() {
        messagePresenter.hideQuickReplyView()
    }

    override fun onQuoteLongClicked(enclosingMessageUuid: UUID) {
        messagePresenter.onQuoteLongClicked(enclosingMessageUuid)
    }
}