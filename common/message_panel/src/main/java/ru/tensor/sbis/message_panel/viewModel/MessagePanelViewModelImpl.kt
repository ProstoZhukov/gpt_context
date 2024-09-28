package ru.tensor.sbis.message_panel.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentPresenterImpl
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData
import ru.tensor.sbis.message_panel.helper.media.MessagePanelMediaRecordHelper
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.optionalRecipientInteractor
import ru.tensor.sbis.message_panel.model.ClearOption.CLEAR_RECIPIENTS
import ru.tensor.sbis.message_panel.model.ClearOption.HIDE_KEYBOARD
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveDataImpl
import ru.tensor.sbis.message_panel.viewModel.stateMachine.CleanStateEvent
import ru.tensor.sbis.message_panel.viewModel.stateMachine.DraftLoadingStateEvent
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventCancel
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventCancelEdit
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventDisable
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventEdit
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventEnable
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventEnableWithDraft
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventQuote
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventRecipients
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventReplay
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventSend
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventSendMediaMessage
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventShare
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventSign
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachine
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachineImpl
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber
import java.util.UUID

/**
 * @author Subbotenko Dmitry
 */
open class MessagePanelViewModelImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    override val recipientsInteractor: MessagePanelRecipientsInteractor?,
    override val attachmentsInteractor: MessagePanelAttachmentsInteractor,
    override val messageInteractor: MessagePanelMessageInteractor<MESSAGE_RESULT, MESSAGE_SENT_RESULT>,
    override val messageResultHelper: MessageResultHelper<MESSAGE_RESULT, MESSAGE_SENT_RESULT>,
    override val draftInteractor: MessagePanelDraftInteractor<DRAFT_RESULT>,
    override val draftResultHelper: DraftResultHelper<DRAFT_RESULT>,
    val fileUriUtil: FileUriUtil,
    override val resourceProvider: ResourceProvider,
    override val recipientsManager: RecipientSelectionResultManager?,
    modelMapper: AttachmentRegisterModelMapper,
    subscriptionManager: SubscriptionManager,
    loginInterface: LoginInterface,
    // TODO: 03.03.2021 Костыль для правильной инициализации ViewModel и LiveData удалить после распутывание эитх зависимостей https://online.sbis.ru/opendoc.html?guid=8b86afd3-85c4-4796-afb4-1a3b49ab8ce6
    startStateMachine: Boolean = true,
    private val uiScheduler: Scheduler = TensorSchedulers.androidUiScheduler
) : ViewModel(), MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {

    private var isDraftLoaded = false

    private val userInputDisposable = SerialDisposable()

    val disposer = CompositeDisposable().apply {
        add(userInputDisposable)
    }

    @Volatile var cachedInfo: CoreConversationInfo = CoreConversationInfo()

    override val stateMachine: MessagePanelStateMachine<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> =
        MessagePanelStateMachineImpl(this, uiScheduler)

    final override val attachmentPresenter by lazy {
        MessagePanelAttachmentPresenterImpl(
            viewModel = this,
            interactor = attachmentsInteractor,
            modelMapper = modelMapper,
            fileUriUtil = fileUriUtil,
            observeScheduler = uiScheduler,
            subscriptionManager = subscriptionManager,
            resourceProvider = resourceProvider,
            loginInterface = loginInterface
        )
    }

    override val liveData by lazy {  MessagePanelLiveDataImpl(this, disposer, uiScheduler)}

    init {
        if (startStateMachine) {
            stateMachine.start()
        }
    }

    override fun setConversationInfo(info: CoreConversationInfo): Boolean {
        val capturedInfo = cachedInfo
        if (info.isNewConversation || !liveData.isRecipientsSelected()
                || info.customRecipientSelection != capturedInfo.customRecipientSelection) {
            messageInteractor.setAnalyticsUsageName(info.analyticsUsageName)
            cachedInfo = info.copy()
            return true
        }
        return false
    }

    override fun onSaveInstanceState() =
        saveDraftOnDestroy()

    override fun saveDraft(draftUuid: UUID?): Completable {
        if (!cachedInfo.saveDraftMessage) return Completable.complete()
        val draftUuidSingle = draftUuid?.let { Single.just(it) } ?: liveData.draftUuidUpdater.firstOrError()
        return draftUuidSingle.flatMapCompletable {
            when {
                cachedInfo.isNewConversation && cachedInfo.conversationUuid == null -> {
                    saveDraftByRecipient(it)
                }
                cachedInfo.conversationUuid != null || cachedInfo.document != null -> {
                    saveDraftByUuid(it)
                }
                else -> Completable.complete()
            }
        }
    }

    override fun loadDraft(): Boolean {
        if (isDraftLoaded) return false
        isDraftLoaded = true
        stateMachine.fire(
            EventEnableWithDraft(
                cachedInfo.document,
                cachedInfo.conversationUuid,
                cachedInfo.isNewConversation,
                cachedInfo.recipients
            )
        )
        return true
    }

    override val conversationInfo: CoreConversationInfo get() = cachedInfo

    override fun resetConversationInfo() {
        liveData.setConversationUuid(cachedInfo.conversationUuid)

        liveData.setDocumentUuid(cachedInfo.document)
        liveData.setQuotedMessageUuid(cachedInfo.quotedMessageUuid)
        liveData.setAnsweredMessageUuid(cachedInfo.answeredMessageUuid)
        liveData.setFolderUuid(cachedInfo.folderUuid)
        liveData.setMessageUuid(cachedInfo.messageUuid)
        liveData.setMessageMetaData(cachedInfo.messageMetaData)

        liveData.newDialogModeEnabled(cachedInfo.isNewDialogModeEnabled)
        liveData.setMinLines(cachedInfo.minLines)
        liveData.setIsTextRequired(cachedInfo.isTextRequired)

        liveData.setRecipientsPanelVisibility(cachedInfo.showRecipientsPanel)
        liveData.setRecipientsRequired(cachedInfo.recipientsRequired)
        liveData.setRecipientsHintEnabled(cachedInfo.isRecipientsHintEnabled)
        liveData.requireCheckAllMembers(cachedInfo.requireCheckAllMembers)
        liveData.showAttachmentsButton(cachedInfo.showAttachmentsButton)
        liveData.setAttachmentsDeletable(cachedInfo.canRemoveAttachments)
        liveData.setAttachmentsRestartable(cachedInfo.canRestartUploadAttachments)
        liveData.setAttachmentsErrorVisible(cachedInfo.canShowUploadErrorAttachments)
        liveData.setSendControlEnabled(cachedInfo.sendButtonEnabled)
        liveData.setQuickReplyButtonVisible(cachedInfo.showQuickReplyButton)
        liveData.setSendCoreRestrictions(false)
        liveData.setQuoteVisibility(false)
        liveData.forceHideRecipientsPanel(false)
    }

    override fun shouldClearRecipients() = cachedInfo.clearOnSendOptions.contains(CLEAR_RECIPIENTS)

    override fun shouldHideKeyboardOnClear() = cachedInfo.clearOnSendOptions.contains(HIDE_KEYBOARD)

    override fun notifyUserTyping() {
        cachedInfo.conversationUuid?.let {
            messageInteractor.notifyUserTyping(it)
                .subscribe()
                .storeIn(userInputDisposable)
        }
    }

    override fun restartUploadAttachment(id: Long) {
        attachmentPresenter.restartUploadAttachment(id)
    }

    override fun clearRecipients() {
        loadRecipients(emptyList())
    }

    override fun resetRecipients() {
        loadRecipients(liveData.recipientsUuidList)
    }

    override fun setRecipients(recipients: List<UUID>, isUserSelected: Boolean) =
        stateMachine.fire(EventRecipients(recipients, isUserSelected))

    override fun addRecipients(recipients: List<UUID>, isUserSelected: Boolean) {
        val currentRecipients = liveData.recipientsUuidList
        val newRecipients = recipients.filter { !currentRecipients.contains(it) }
        if (newRecipients.isNotEmpty()) {
            stateMachine.fire(EventRecipients(newRecipients, isUserSelected, add = true))
        }
    }

    override fun loadRecipients(recipients: List<UUID>, isUserSelected: Boolean, add: Boolean) {
        optionalRecipientInteractor { recipientsInteractor ->
            disposer += recipientsInteractor.loadRecipientModels(recipients)
                .subscribe { recipients ->
                    val recipientList = if (add) {
                        liveData.recipients.value!! + recipients
                    } else {
                        recipients
                    }
                    liveData.setRecipients(recipientList, isUserSelected)
                }
        }
    }

    override fun clearPanel(resetDraftMessage: Boolean) {
        attachmentPresenter.clearAttachments()
        if (resetDraftMessage) {
            conversationInfo.run {
                stateMachine.fire(
                    DraftLoadingStateEvent(
                        document,
                        conversationUuid,
                        isNewConversation,
                        recipients,
                        needToClean = false,
                        clearDraft = true
                    )
                )
            }
        } else {
            stateMachine.fire(CleanStateEvent())
        }
    }

    override fun onRecorderDecorDataChanged(recorderDecorData: RecorderDecorData) {
        this.recorderDecorData.onNext(recorderDecorData)
    }

    override val messageSending = PublishSubject.create<Unit>()
    override fun onMessageSending() {
        messageSending.onNext(Unit)
    }

    override val messageSent = PublishSubject.create<MESSAGE_SENT_RESULT>()
    override val lastSentMessageUuid = BehaviorSubject.create<RxContainer<UUID>>()

    override fun onMessageSent(message: MESSAGE_SENT_RESULT) {
        messageSent.onNext(message)
        lastSentMessageUuid.onNext(
            RxContainer(messageResultHelper.getSentMessageUuid(message))
        )
    }

    override val messageEdit = PublishSubject.create<MESSAGE_RESULT>()
    override fun onMessageEdit(message: MESSAGE_RESULT) {
        messageEdit.onNext(message)
    }

    override val messageEditCanceled = PublishSubject.create<Unit>()
    override fun onMessageEditCanceled() {
        messageEditCanceled.onNext(Unit)
    }

    override val messageAttachErrorClicked = PublishSubject.create<MessageAttachError>()
    override val recorderDecorData = PublishSubject.create<RecorderDecorData>()

    override fun onMessageAttachErrorClick(attachError: MessageAttachError) {
        messageAttachErrorClicked.onNext(attachError)
    }

    override val onKeyboardForcedHidden = PublishSubject.create<Unit>()
    override fun onForceHideKeyboard() {
        onKeyboardForcedHidden.onNext(Unit)
    }

    // endregion callbacks

    private fun saveDraftOnDestroy() {
        /* При редактировании введенный текст - не сохраняем.
           В качестве драфта сохраняется изначальный текст,
           который был введен до перехода в состояние редактирования */
        if (liveData.isEditing.blockingFirst()) return
        liveData.getDraftUuid()?.let {
            saveDraft(it).subscribe(Functions.EMPTY_ACTION) {
                Timber.w(it, "Error saving draft")
            }
        }
    }

    private fun canSaveDraftByRecipient(): Boolean {
        return (liveData.messageText.dataValue?.isNotBlank() == true ||
                attachmentPresenter.attachments.isNotEmpty()) &&
                liveData.recipientsUuidList.size == 1
    }

    private fun saveDraftByRecipient(draftUuid: UUID): Completable {
        return if (canSaveDraftByRecipient()) {
            draftInteractor.saveDraftByRecipient(
                draftUuid,
                liveData.recipientsUuidList.first(),
                liveData.messageText.dataValue,
                attachmentPresenter.attachments.mapNotNull(FileInfo::attachId),
                liveData.getModifiedMessageMetaData(liveData.getMentionsObject())
            )
        } else {
            Completable.complete()
        }
    }

    private fun saveDraftByUuid(draftUuid: UUID): Completable {
        return draftInteractor.saveDraft(
            draftUuid,
            cachedInfo.conversationUuid,
            cachedInfo.document,
            liveData.recipientsUuidList,
            liveData.messageText.dataValue,
            attachmentPresenter.attachments.mapNotNull(FileInfo::attachId),
            liveData.quotedMessageUuid.dataValue,
            liveData.answeredMessageUuid.dataValue,
            liveData.getModifiedMessageMetaData(liveData.getMentionsObject())
        )
    }

    override fun onCleared() {
        saveDraftOnDestroy()
        super.onCleared()
        attachmentPresenter.dispose()
        disposer.dispose()
        messageSending.onComplete()
        messageSent.onComplete()
        messageEdit.onComplete()
        stateMachine.stop()
    }

    override fun enable() = stateMachine.fire(EventEnable())
    override fun disable() {
        isDraftLoaded = false
        stateMachine.fire(EventDisable())
    }
    override fun sendMessage() = stateMachine.fire(EventSend())

    override fun sendMediaMessage(data: MediaRecordData) {
        viewModelScope.launch {
            val metaData = MessagePanelMediaRecordHelper.createMetaData(data)
            val attachment = MessagePanelMediaRecordHelper.createMediaMessageAttachment(data)
            withContext(Dispatchers.Main) {
                stateMachine.fire(EventSendMediaMessage(attachment, metaData))
            }
        }
    }

    override fun editMediaMessageEmotion(emotionCode: Int) {
        if (emotionCode == 0) return
        lastSentMessageUuid.firstOrError().subscribe { uuidContainer ->
            val messageUuid = uuidContainer.value
            if (messageUuid != null) {
                messageInteractor.editMediaMessageEmotion(messageUuid, emotionCode)
                    .subscribe()
                    .storeIn(disposer)
            }
        }.storeIn(disposer)
    }

    override fun editMessage(content: EditContent) {
        stateMachine.fire(
            EventEdit(
                resourceProvider.getString(R.string.message_panel_editing_message_title),
                content
            )
        )
    }

    override fun quoteMessage(content: QuoteContent, showKeyboard: Boolean) {
        stateMachine.fire(EventQuote(content, showKeyboard))
    }

    override fun shareMessage(content: ShareContent) {
        stateMachine.fire(EventShare(content))
    }

    override fun signMessage(action: SignActions) {
        stateMachine.fire(EventSign(action))
    }

    override fun onPickerLinkSelected(url: String) {
        val conversationUuid = cachedInfo.conversationUuid
        if (conversationUuid != null && !cachedInfo.isNewConversation) {
            messageInteractor.sendLink(conversationUuid, url)
                .subscribe({}, {
                    liveData.setMessageText(url)
                    sendMessage()
                })
                .storeIn(disposer)
        } else {
            liveData.setMessageText(url)
            sendMessage()
        }
    }

    override fun cancelEdit(editingMessage: UUID?) {
        // если не указан editingMessage, применяем безусловную отмену
        stateMachine.fire(editingMessage?.run(::EventCancelEdit) ?: EventCancel())
    }
    override fun replyComment(conversationUuid: UUID, messageUuid: UUID, documentUuid: UUID, showKeyboard: Boolean) {
        stateMachine.fire(EventReplay(conversationUuid, messageUuid, documentUuid, showKeyboard))
    }
}
