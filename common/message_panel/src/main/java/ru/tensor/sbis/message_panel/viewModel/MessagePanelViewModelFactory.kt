package ru.tensor.sbis.message_panel.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewMode
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.message_panel.contract.MessageServiceDependency
import ru.tensor.sbis.message_panel.di.MessagePanelComponentProvider
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractorImpl
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractorImpl
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor

/**
 * @author Subbotenko Dmitry
 */
internal class MessagePanelViewModelFactory<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    private val applicationContext: Context,
    private val messageServiceDependency: MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val recipientsInteractor: MessagePanelRecipientsInteractor?,
    private val attachmentsInteractor: MessagePanelAttachmentsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val diComponent = MessagePanelComponentProvider[applicationContext]

        val messageServiceWrapper = messageServiceDependency.serviceWrapper
        val messageResultHelper = messageServiceDependency.messageResultHelper
        val draftResultHelper = messageServiceDependency.draftResultHelper

        return MessagePanelViewModelImpl(
            recipientsInteractor = recipientsInteractor,
            attachmentsInteractor = attachmentsInteractor,
            messageInteractor = MessagePanelMessageInteractorImpl(messageServiceWrapper),
            messageResultHelper = messageResultHelper,
            draftInteractor = MessagePanelDraftInteractorImpl(messageServiceWrapper),
            draftResultHelper = draftResultHelper,
            fileUriUtil = FileUriUtil(applicationContext),
            resourceProvider = diComponent.getResourceProvider(),
            recipientsManager = diComponent.recipientSelectionProvider?.getRecipientSelectionResultManager(),
            modelMapper = diComponent.dependency.createAttachmentRegisterModelMapper(AttachmentsViewMode.MESSAGE),
            subscriptionManager = diComponent.getSubscriptionManager(),
            loginInterface = diComponent.dependency.loginInterface
        ) as T
    }
}
