package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * Общая механика инициализации для [CleanSendState] и [DraftLoadingState]
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal fun cleanAction(
    liveData: MessagePanelLiveData,
    viewModel: MessagePanelViewModel<*, *, *>,
    cleanText: Boolean,
    cleanRecipients: Boolean = cleanText
) {
    liveData.setRecipientsSelected(false)

    if (cleanText) liveData.setMessageText("")
    if (cleanRecipients) viewModel.clearRecipients()
    viewModel.resetConversationInfo()
}

/**
 * Расширение для установки контента, которым поделились во вьюмодель
 */
internal fun MessagePanelViewModel<*, *, *>.setSharedContent(content: ShareContent) {
    liveData.setMessageText(content.text)
    attachmentPresenter.addAttachments(content.fileUriList)
}