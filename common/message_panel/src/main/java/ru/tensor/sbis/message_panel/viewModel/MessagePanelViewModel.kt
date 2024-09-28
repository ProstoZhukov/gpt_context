package ru.tensor.sbis.message_panel.viewModel

import io.reactivex.Completable
import io.reactivex.Observable
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.model.ShareContent
import java.util.*

/**
 * @author Subbotenko Dmitry
 */
interface MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> :
    MessagePanelDependencies<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>, MessagePanelViewModelInfo {
    val messageSending: Observable<Unit>
    val messageSent: Observable<MESSAGE_SENT_RESULT>
    val lastSentMessageUuid: Observable<RxContainer<UUID>>
    val messageEdit: Observable<MESSAGE_RESULT>
    val messageEditCanceled: Observable<Unit>
    val messageAttachErrorClicked: Observable<MessageAttachError>
    val recorderDecorData: Observable<RecorderDecorData>

    val onKeyboardForcedHidden: Observable<Unit>

    fun setConversationInfo(info: CoreConversationInfo): Boolean
    fun saveDraft(draftUuid: UUID? = null): Completable

    /**
     * Восстанавливает черновик сообщения, если это не было сделано ранее
     *
     * @return true, если черновик не был восстановлен ранее
     */
    fun loadDraft(): Boolean
    fun resetConversationInfo()
    fun clearRecipients()
    fun resetRecipients()
    fun onSaveInstanceState()
    fun onCleared()
    fun enable()
    fun disable()
    fun sendMessage()
    fun sendMediaMessage(data: MediaRecordData)
    fun editMediaMessageEmotion(emotionCode: Int)
    fun editMessage(content: EditContent)
    fun quoteMessage(content: QuoteContent, showKeyboard: Boolean)
    fun shareMessage(content: ShareContent)
    fun signMessage(action: SignActions)
    fun cancelEdit(editingMessage: UUID? = null)
    fun replyComment(conversationUuid: UUID, messageUuid: UUID, documentUuid: UUID, showKeyboard: Boolean)
    fun shouldClearRecipients(): Boolean
    fun shouldHideKeyboardOnClear(): Boolean
    fun notifyUserTyping()
    fun restartUploadAttachment(id: Long)
    fun onForceHideKeyboard()
    fun onPickerLinkSelected(url: String)

    fun onMessageSending()
    fun onMessageSent(message: MESSAGE_SENT_RESULT)
    fun onMessageEdit(message: MESSAGE_RESULT)
    fun onMessageEditCanceled()
    fun onMessageAttachErrorClick(attachError: MessageAttachError)
    fun setRecipients(recipients: List<UUID>, isUserSelected: Boolean = false)
    fun addRecipients(recipients: List<UUID>, isUserSelected: Boolean = false)
    fun loadRecipients(recipients: List<UUID>, isUserSelected: Boolean = false, add: Boolean = false)
    fun clearPanel(resetDraftMessage: Boolean = false)
    fun onRecorderDecorDataChanged(recorderDecorData: RecorderDecorData)
}