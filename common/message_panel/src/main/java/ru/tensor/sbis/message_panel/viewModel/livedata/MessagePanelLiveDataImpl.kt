package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.communication_decl.communicator.media.getServiceObject
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.message_panel.helper.getSendButtonActivated
import ru.tensor.sbis.message_panel.helper.sendButtonActiveByRecipients
import ru.tensor.sbis.message_panel.interactor.recipients.createMessagePanelRecipientsView
import ru.tensor.sbis.message_panel.interactor.recipients.requireRecipientInteractor
import ru.tensor.sbis.message_panel.view.mentions.MentionData
import ru.tensor.sbis.message_panel.view.mentions.MentionParser
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.LinesCountStatus.*
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsControls
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsControlsImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsData
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsDataImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHint
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByRequest
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediator
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediatorImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.CustomRecipientSelectionMediator
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.CustomRecipientSelectionMediatorImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsView
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Пороговое значение количества строк, при котором должен измениться способ отображения
 * кнопки получателей
 */
internal const val LINES_COUNT_THRESHOLD = 3
private const val RECIPIENTS_EVENT_WINDOW_DURATION_MS = 1000L
private const val USER_TYPING_THROTTLE_DURATION_MS = 5000L

/**
 * @author Subbotenko Dmitry
 */
open class MessagePanelLiveDataImpl(
    private val viewModel: MessagePanelViewModel<*, *, *>,
    private val disposer: DisposableContainer,
    uiScheduler: Scheduler = AndroidSchedulers.mainThread(),
    notificationDelegate: MessagePanelNotifications = MessagePanelNotificationsImpl(),
    keyboardEventDelegate: KeyboardEventMediator = KeyboardEventMediatorImpl()
) : MessagePanelLiveData,
    MessagePanelQuoteData by MessagePanelQuoteDataImpl(),
    MessagePanelEditData by MessagePanelEditDataImpl(viewModel, disposer),
    MessagePanelRecipientsView by viewModel.createMessagePanelRecipientsView(uiScheduler),
    MessagePanelAvailableSpaceForContent by MessagePanelAvailableSpaceForContentImpl(),
    MessagePanelNotifications by notificationDelegate,
    MessagePanelHint by MessagePanelHintImpl(),
    MessagePanelAttachmentsControls by MessagePanelAttachmentsControlsImpl(viewModel),
    MessagePanelAttachmentsData by MessagePanelAttachmentsDataImpl(
        viewModel.attachmentPresenter, keyboardEventDelegate, viewModel.resourceProvider, notificationDelegate
    ),
    KeyboardEventMediator by keyboardEventDelegate,
    CustomRecipientSelectionMediator by CustomRecipientSelectionMediatorImpl()
{

    private val areRecipientsRequired = BehaviorSubject.createDefault(false)
    private val isTextRequired = BehaviorSubject.createDefault(false)

    override val linesCountStatus: Subject<LinesCountStatus> = BehaviorSubject.createDefault(THRESHOLD_NOT_EXCEEDED)

    private val recipientSelectionEvent = PublishSubject.create<Unit>()

    private val sendControlCoreRestrictions: Subject<Boolean> = BehaviorSubject.createDefault(false)

    override val sendControlEnabled = BehaviorSubject.createDefault(true)

    final override val messagePanelEnabled: Observable<Boolean> = viewModel.stateMachine.isEnabled.toObservable()

    override val conversationUuid = BehaviorSubject.create<RxContainer<UUID?>>()
    override val document = BehaviorSubject.create<RxContainer<UUID?>>()
    override val answeredMessageUuid = BehaviorSubject.create<RxContainer<UUID>>()
    override val quotedMessageUuid = BehaviorSubject.create<RxContainer<UUID>>()
    override val folderUuid = BehaviorSubject.create<RxContainer<UUID>>()
    override val messageUuid = BehaviorSubject.create<RxContainer<UUID>>()
    override val messageMetaData = BehaviorSubject.create<RxContainer<String?>>()
    override val showQuickReplyButton = BehaviorSubject.createDefault(false)
    override val mentionsData = BehaviorSubject.createDefault(emptyList<MentionData>())
    override val draftMentions = BehaviorSubject.createDefault(MessageTextWithMentions())

    override val newDialogModeEnabled = BehaviorSubject.createDefault(false)
    override val minLines = BehaviorSubject.createDefault(1)
    override val isLandscape = BehaviorSubject.create<Boolean>()

    override val recipientsFeatureEnabled = BehaviorSubject.createDefault(false)
    override val recipients = BehaviorSubject.createDefault(emptyList<RecipientItem>())
    override val isRecipientsHintEnabled = BehaviorSubject.createDefault(true)
    override val requireCheckAllMembers = BehaviorSubject.createDefault(false)
    private val hasRecipients = recipients.map(Collection<*>::isNotEmpty)
    override val recipientsUuidList: List<UUID>
        get() {
            val recipients = recipients.value!!
            val persons = recipients.filterIsInstance<RecipientPersonItem>().map { it.uuid }
            val personsFromDepartments = recipients.filterIsInstance<RecipientDepartmentItem>()
                .map { department ->
                    department.personModels.map { it.uuid }
                }.flatten()
            return personsFromDepartments + persons
        }
    override val recipientsSelected = BehaviorSubject.createDefault(false)
    override val requireRecipients get() = areRecipientsRequired.value!! && recipients.value.isNullOrEmpty()

    override val messageText = BehaviorSubject.createDefault(RxContainer(""))

    private val typingText = PublishSubject.create<RxContainer<String>>().apply {
        disposer += messageText.subscribe(::onNext)
    }

    override val panelMaxHeight = PublishSubject.create<Int>()

    override val recorderDecorData by lazy {
        Observable.combineLatest(
            recipientsViewData,
            recipientsVisibility,
            quoteData,
            quotePanelVisible
        ) { recipientsData, isRecipientsVisible, quoteData, isQuoteVisible ->
            RecorderDecorData(
                recipientsData = recipientsData.takeIf { isRecipientsVisible },
                quoteData = quoteData.takeIf { isQuoteVisible }
            )
        }
    }

    init {
        disposer += recipientSelectionEvent
            .throttleFirst(RECIPIENTS_EVENT_WINDOW_DURATION_MS, TimeUnit.MILLISECONDS)
            .observeOn(uiScheduler)
            .subscribe { showRecipientSelection() }

        subscribeUserTyping()
    }

    override fun ordinalCancelClickListener() {
        viewModel.cancelEdit()
    }

    private data class SendControlActivatedContainer(
        val hasCoreRestrictions: Boolean = false,
        val isEnabled: Boolean = false,
        val isSending: Boolean? = null,
        val isQuoting: Boolean? = null,

        val messageText: String? = null,
        val isTextRequired: Boolean = false,
        val hasAttachments: Boolean = false,
        val permitByRecipients: Boolean = false,
        val inviteSupported: Boolean = false
    ) : RxContainer<Boolean>(!hasCoreRestrictions && isEnabled && !(isSending ?: false) &&
            !(isTextRequired && messageText.isNullOrBlank()) && (
                !messageText.isNullOrBlank()
                        || messageText.isNullOrBlank() && isTextRequired
                        || hasAttachments
                        || (permitByRecipients && inviteSupported)
                        || (isQuoting ?: false)
                )
    )

    override val sendControlActivated: Observable<out RxContainer<Boolean>> =
            BehaviorSubject.createDefault(SendControlActivatedContainer()).apply {
                disposer += sendControlCoreRestrictions.subscribe { onNext(value!!.copy(hasCoreRestrictions = it)) }
                disposer += Observable.combineLatest(
                    messagePanelEnabled,
                    sendControlEnabled
                ) { stateEnabled, buttonEnabled -> stateEnabled && buttonEnabled }
                    .subscribe { onNext(value!!.copy(isEnabled = it)) }
                disposer += viewModel.stateMachine.isSending.subscribe { onNext(value!!.copy(isSending = it)) }
                disposer += viewModel.stateMachine.isQuoting.subscribe { onNext(value!!.copy(isQuoting = it)) }
                disposer += messageText.subscribe { onNext(value!!.copy(messageText = it.value)) }
                disposer += isTextRequired.subscribe { onNext(value!!.copy(isTextRequired = it)) }
                disposer += hasAttachments.subscribe { onNext(value!!.copy(hasAttachments = it)) }
                disposer += Observable.combineLatest(
                    // правило проверки получателей для внешних ограничений
                    hasRecipients.map { viewModel.conversationInfo.sendButtonActiveByRecipients(it) },
                    // реактивное правило проверки получателей и их необходимости
                    Observable.combineLatest(
                        areRecipientsRequired,
                        hasRecipients,
                        // можно активировать кнопку, если получатели не обязательны или установлены
                        BiFunction<Boolean, Boolean, Boolean> { require, has -> !require || has }),
                    // агрегация разрешений - все правила должны разрешить активацию
                    BiFunction<Boolean, Boolean, Boolean> { externalRulePermit, requirementPermit ->
                        externalRulePermit && requirementPermit
                    }
                ).subscribe { onNext(value!!.copy(permitByRecipients = it)) }
            }.map { it.copy(
                // сейчас установка атрибута только при изменении смежных
                inviteSupported = viewModel.conversationInfo.inviteSupported
            ) }

    override val sendControlInvisible = BehaviorSubject.createDefault(false)

    override val sendControlClickable = BehaviorSubject.createDefault(true)

    /**
     * В реестре сообщений пременялось только при редактировании
     */
    override fun setSendCoreRestrictions(restricted: Boolean) {
        sendControlCoreRestrictions.onNext(restricted || !viewModel.conversationInfo.getSendButtonActivated())
    }

    /**
     * Установить кликабельность кнопки отправки.
     * Необходим для предотвращения инициации отправки до завершения каких-либо действий.
     */
    override fun setSendControlClickable(isClickable: Boolean) {
        sendControlClickable.onNext(isClickable)
    }

    override fun setSendControlEnabled(isEnabled: Boolean) {
        sendControlEnabled.onNext(isEnabled)
    }

    override fun setPanelMaxHeight(height: Int) {
        panelMaxHeight.onNext(height)
    }

    override fun setIsTextRequired(isRequired: Boolean) {
        isTextRequired.onNext(isRequired)
    }

    override fun setIsLandscape(isLandscape: Boolean) {
        this.isLandscape.onNext(isLandscape)
    }

    override fun setMinLines(count: Int) {
        minLines.onNext(count)
    }

    override fun onRecipientButtonClick() {
        if (viewModel.conversationInfo.customRecipientSelection) {
            postKeyboardEvent(ClosedByRequest)
            requestCustomRecipients()
        } else {
            recipientSelectionEvent.onNext(Unit)
        }
    }

    private fun showRecipientSelection() {
        val recipientsManager = viewModel.recipientsManager ?: return
        viewModel.requireRecipientInteractor { recipientsInteractor ->
            postKeyboardEvent(ClosedByRequest)
            recipientsManager.preselect(recipientsUuidList)
            requestRecipientsSelection()

            disposer += recipientsManager.getSelectionResultObservable()
                .firstElement()
                .filter { it.isSuccess }
                .flatMap { recipientsInteractor.loadRecipientModels(it.data) }
                .subscribe {
                    setRecipients(it, true)
                    postKeyboardEvent(OpenedByRequest)
                }
        }
    }

    override fun setRecipients(recipientsList: List<RecipientItem>, isUserSelected: Boolean) {
        //автоподстановка получателей будет блокироваться, если до этого был сделан намеренный выбор пользователем
        if (isUserSelected) {
            setRecipientsSelected(true)
        } else if (isRecipientsSelected()) {
            return
        }
        recipients.onNext(recipientsList)
    }

    override fun setRecipientsRequired(required: Boolean) {
        areRecipientsRequired.onNext(required)
    }

    override fun setRecipientsSelected(isSelected: Boolean) {
        recipientsSelected.onNext(isSelected)
    }

    override fun isRecipientsSelected(): Boolean = recipientsSelected.value!!

    override fun onRecipientClearButtonClick() {
        if (viewModel.conversationInfo.customRecipientSelection) {
            clearCustomRecipients()
        } else {
            viewModel.recipientsManager?.clear()
        }
        setRecipients(emptyList(), true)
        /*
        При нажатии на Х в панели адресатов нужно закрыть клавиатуру и прекратить ввод т.к. без адресата сценарий 
        некорректный (например, редактирование комментария)
        */
        if (viewModel.shouldHideKeyboardOnClear()) ordinalCancelClickListener()
    }

    override fun setRecipientsFeatureEnabled(enabled: Boolean) {
        recipientsFeatureEnabled.onNext(enabled)
    }

    // region MessagePanelDataControls implementation
    override fun onCompletelyVisibleLinesCountChanged(linesCount: Int, linesCountWithoutWidthLoss: Int) {
        val status = when {
            linesCount <= LINES_COUNT_THRESHOLD -> THRESHOLD_NOT_EXCEEDED
            linesCountWithoutWidthLoss > LINES_COUNT_THRESHOLD -> THRESHOLD_EXCEEDED
            else -> AMBIGUOUS
        }
        linesCountStatus.onNext(status)
    }

    override fun newDialogModeEnabled(enabled: Boolean) {
        newDialogModeEnabled.onNext(enabled)
    }
    // endregion

    override fun setSendControlInvisible(isInvisible: Boolean) {
        sendControlInvisible.onNext(isInvisible)
    }

    // region MessagePanelData setters
    override fun setConversationUuid(conversationUuid: UUID?) {
        this.conversationUuid.dataValue = conversationUuid
    }

    override fun setDocumentUuid(documentUuid: UUID?) {
        document.dataValue = documentUuid
    }

    override fun setQuotedMessageUuid(quotedMessageUuid: UUID?) {
        this.quotedMessageUuid.dataValue = quotedMessageUuid
    }

    override fun setAnsweredMessageUuid(answeredMessageUuid: UUID?) {
        this.answeredMessageUuid.dataValue = answeredMessageUuid
    }

    override fun setFolderUuid(folderUuid: UUID?) {
        this.folderUuid.dataValue = folderUuid
    }

    override fun setMessageUuid(messageUuid: UUID?) {
        this.messageUuid.dataValue = messageUuid
    }

    override fun setMessageText(text: String?) {
        messageText.dataValue = text
    }

    override fun setMessageText(text: RxContainer<String>) {
        messageText.onNext(text)
    }

    override fun concatMessageText(text: String) {
        val currentValue = messageText.dataValue ?: ""
        messageText.dataValue = "$currentValue$text"
    }

    override fun setMessageMetaData(metaData: String?) {
        messageMetaData.dataValue = metaData
    }

    override fun setQuickReplyButtonVisible(isVisible: Boolean) {
        Timber.v("CAN_QUICK_REPLY: button_visibility_in_message_panel - $isVisible")
        showQuickReplyButton.onNext(isVisible)
    }

    override fun setMentionsData(mentions: List<MentionData>) {
        this.mentionsData.onNext(mentions)
    }

    override fun getModifiedMessageMetaData(data: JSONObject?): String? {
        if (data == null) return messageMetaData.dataValue
        val baseMetaDataObject = getServiceObject(messageMetaData.dataValue ?: StringUtils.EMPTY) ?: data
        for (key in data.keys()) {
            baseMetaDataObject.put(key, data.get(key))
        }
        return baseMetaDataObject.toString()
    }

    override fun getMentionsObject(): JSONObject? = mentionsData.value?.let {
        if (it.isEmpty()) return@let null
        MentionParser.buildServiceObjectWithMentions(it)
    }

    override fun setDraftMentions(messageTextWithMentions: MessageTextWithMentions) {
        draftMentions.onNext(messageTextWithMentions)
    }

    override fun setRecipientsHintEnabled(isEnabled: Boolean) {
        isRecipientsHintEnabled.onNext(isEnabled)
    }

    override fun requireCheckAllMembers(isRequired: Boolean) {
        requireCheckAllMembers.onNext(isRequired)
    }

    // endregion

    /**
     * Логика необходимая для реализации функционала "Пользователь пишет".
     * Нотификация о пользовательском вводе, не чаще чем [USER_TYPING_THROTTLE_DURATION_MS].
     */
    private fun subscribeUserTyping() {
        disposer += typingText
            .skip(1)
            .filter { !it.value.isNullOrEmpty() }
            .throttleFirst(USER_TYPING_THROTTLE_DURATION_MS, TimeUnit.MILLISECONDS)
            .subscribe { viewModel.notifyUserTyping() }
    }
}
