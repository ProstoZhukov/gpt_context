package ru.tensor.sbis.message_panel.view

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.Spannable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwnerCompat
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.common.rx.livedata.bindWithDistinct
import ru.tensor.sbis.common.rx.livedata.twoWayObserver
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common_views.sbisview.SbisEditTextWithHideKeyboardListener
import ru.tensor.sbis.communication_decl.communicator.media.getServiceObject
import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.SelectionMenuDelegate
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPerson
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.decl.quote.QuoteView
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelEditText
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelLayout
import ru.tensor.sbis.design.message_panel.view.layout.RecordButton
import ru.tensor.sbis.design.utils.extentions.setRightPadding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.contract.FocusChangeListener
import ru.tensor.sbis.message_panel.contract.MessagePanelBindingDependency
import ru.tensor.sbis.message_panel.di.MessagePanelComponentProvider
import ru.tensor.sbis.message_panel.helper.EditTextFocusCleaner
import ru.tensor.sbis.message_panel.helper.MessagePanelMentionFeature
import ru.tensor.sbis.message_panel.helper.MessagePanelTextParamsMapper
import ru.tensor.sbis.message_panel.helper.applyTextParams
import ru.tensor.sbis.message_panel.helper.hideKeyboard
import ru.tensor.sbis.message_panel.helper.markNewMessageMode
import ru.tensor.sbis.message_panel.helper.observeRecorderViewVisibility
import ru.tensor.sbis.message_panel.helper.showKeyboard
import ru.tensor.sbis.message_panel.helper.skipOpenedByRequestIfDisabled
import ru.tensor.sbis.message_panel.helper.toAttachments
import ru.tensor.sbis.message_panel.helper.toFocusRequestObservable
import ru.tensor.sbis.message_panel.view.mentions.EnterMentionSpan
import ru.tensor.sbis.message_panel.view.mentions.MentionParser
import ru.tensor.sbis.message_panel.view.mentions.MentionSpan
import ru.tensor.sbis.message_panel.view.mentions.MentionTextWatcher
import ru.tensor.sbis.message_panel.view.mentions.MentionTextWatcherImpl
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.BackButtonListener
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.FocusListener
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventHandlerImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediator
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MESSAGE_PANEL_SELECTION_MENU_REQUEST_KEY
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.R as RMPDesign

interface MessagePanelViewDependency {
    /** @SelfDocumented */
    val shouldAdjustPositionOnKeyboardChanges: Boolean

    fun showKeyboard()
    fun hideKeyboard()
    fun getContext(): Context
}

open class MessagePanelBindingDelegate constructor(
    protected val messagePanel: ViewGroup,
    private val viewDependency: MessagePanelViewDependency,
    private val autoTranslateOnFocus: Boolean = false
) {

    internal constructor(
        layout: MessagePanelLayout,
        viewDependency: MessagePanelViewDependency,
        autoTranslateOnFocus: Boolean = false
    ) : this(
        layout.also {
            it.init()
            it.sendButton.id = R.id.message_panel_send_button_view
            it.inputView.id = R.id.message_panel_edit_text_view
            it.attachmentsView.id = R.id.message_panel_attachments_view
            it.messageContainer.id = R.id.message_panel_message_container_view
            it.attachButton.id = R.id.message_panel_attach_button_view
            it.signButton.id = R.id.message_panel_sign_button
            it.recipientsView.id = R.id.message_panel_recipients_view
            it.quoteView.id = R.id.message_panel_quote_view
        }.rootView,
        viewDependency,
        autoTranslateOnFocus
    )

    private val sendButton: View? = messagePanel.findViewById(R.id.message_panel_send_button_view)
    private val editTextView: SbisEditTextWithHideKeyboardListener = messagePanel.findViewById(R.id.message_panel_edit_text_view)
    private val attachButton: View? = messagePanel.findViewById(R.id.message_panel_attach_button_view)
    private val attachmentsView: AttachmentsView? = messagePanel.findViewById(R.id.message_panel_attachments_view)
    private val signButton: View? = messagePanel.findViewById(R.id.message_panel_sign_button)
    private val recipients: RecipientsView? = messagePanel.findViewById(R.id.message_panel_recipients_view)
    private val quote: QuoteView? = messagePanel.findViewById(R.id.message_panel_quote_view)
    private val audioRecordButton: RecordButton? = messagePanel.findViewById(RMPDesign.id.design_message_panel_audio_record_button)
    private val videoRecordButton: RecordButton? = messagePanel.findViewById(RMPDesign.id.design_message_panel_video_record_button)
    private val messageContainer: View = messagePanel.findViewById<View>(R.id.message_panel_message_container_view).apply {
        clipToOutline = true
    }
    private val quickReplyButton: View? = messagePanel.findViewById(RMPDesign.id.design_message_panel_quick_reply_button)
    private var movablePanelContainer: ViewGroup? = null

    private val lifecycleOwner = object : LifecycleOwnerCompat {
        override fun getLifecycleCompat(): Lifecycle {
            return lifecycleRegistry
        }
    }

    private var isTablet: Boolean = false

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(lifecycleOwner)
    private lateinit var mProgressDialogDelegate: ProgressDialogDelegate
    private lateinit var mAlertDialogDelegate: AlertDialogDelegate
    protected lateinit var bindingDependency: MessagePanelBindingDependency<Any, Any>
    protected lateinit var panelViewModel: MessagePanelViewModel<Any, Any, Any>
    protected val disposer = CompositeDisposable()
    private val messagePanelHelper = MessagePanelBindingHelper(messagePanel.resources)
    private val mentionTextWatcher: MentionTextWatcher = MentionTextWatcherImpl(messagePanel.context)
    private val mentionParser = MentionParser(messagePanel.context)
    private val onEditTextTouchListener = OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            mentionTextWatcher.carriagePosition = editTextView.selectionStart
            mentionTextWatcher.updateSearchQuery(editTextView.text)
        }
        v.performClick()
    }

    val viewModel: MessagePanelViewModel<Any, Any, Any>
        get() = panelViewModel

    // isInitialize нужно, чтобы не вызывать bind() в onAttachedToWindow(), когда зависимости еще не инициализированы
    var isInitialize = false

    // isBinded нужно, чтобы не вызывать bind() повторно в onAttachedToWindow(), если он уже был вызван после initDelegate
    var isBinded = false

    /**
     * Инициализация зависимостей панели
     */
    fun <MESSAGE_RESULT, MESSAGE_SENT_RESULT> initDelegate(
        dependency: MessagePanelBindingDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT>,
        viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, *>,
        progressDialogDelegate: ProgressDialogDelegate,
        alertDialogDelegate: AlertDialogDelegate,
        @IdRes movablePanelContainerId: Int
    ) {
        bindingDependency = dependency as MessagePanelBindingDependency<Any, Any>
        panelViewModel = viewModel as MessagePanelViewModel<Any,Any,Any>
        mProgressDialogDelegate = progressDialogDelegate
        mAlertDialogDelegate = alertDialogDelegate
        movablePanelContainer = messagePanel.rootView.findViewById(movablePanelContainerId)
        isInitialize = true
    }

    @CheckReturnValue
    open fun bind() {
        isTablet = DeviceConfigurationUtils.isTablet(messagePanel.context)

        unbind()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        val liveData = panelViewModel.liveData

        sendButton?.apply {
            disposer += liveData.sendControlActivated.bindWithDistinct {
                isActivated = it == true
                isEnabled = it == true
            }
            disposer += liveData.sendControlClickable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isClickable = it }
            disposer += liveData.sendControlInvisible.subscribe { isInvisible = it }
            disposer += RxView.clicks(this)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    when {
                        // запросить установку адресата, если обязательны
                        liveData.requireRecipients -> liveData.onRecipientButtonClick()
                        // отправлять разрешаем только, если не требуется установка получателей
                        isActivated && isClickable -> {
                            panelViewModel.sendMessage()
                        }
                    }
                }
        }

        fun RecordButton.bind() {
            disposer += observeRecorderViewVisibility(
                panelViewModel.liveData,
                hasActionListener
            ).subscribe { isVisible ->
                this.isVisible = isVisible
            }
            disposer += liveData.messagePanelEnabled.subscribe {
                isEnabled = it
            }
        }
        disposer += liveData.recorderDecorData.subscribe {
            viewModel.onRecorderDecorDataChanged(it)
        }

        audioRecordButton?.bind()
        videoRecordButton?.bind()

        editTextView.apply {
            disposer += liveData.messageText.twoWayObserver(this, liveData::setMessageText)
            if (MessagePanelMentionFeature.isActive) {
                addTextChangedListener(mentionTextWatcher)
                disposer += mentionTextWatcher.currentMentions.subscribe { mentions ->
                    viewModel.liveData.setMentionsData(mentions.map { it.data })
                }
            }
            setOnTouchListener(onEditTextTouchListener)

            disposer += liveData.messagePanelEnabled.subscribe {
                isEnabled = it
                /*
                Перед установкой isEnabled = true поле не должно быть focusableInTouchMode, иначе может быть
                принудительно выставлен фокус
                */
                isFocusableInTouchMode = it
            }

            val focusChangeListener = object : FocusChangeListener {
                override fun invoke(isFocused: Boolean) {
                    bindingDependency.onFocusChanged?.invoke(isFocused)
                }
            }
            startKeyboardEventHandling(liveData, liveData.messagePanelEnabled, focusChangeListener)

            (editTextView as? MessagePanelEditText)?.also(::startClipboardFilesHandling)

            disposer += liveData.newDialogModeEnabled.subscribe(::markNewMessageMode)

            disposer += Observable.combineLatest(
                liveData.newDialogModeEnabled,
                liveData.editTextMaxHeight,
                liveData.messageText,
                liveData.messageHint,
                liveData.minLines,
                MessagePanelTextParamsMapper()
            ).distinctUntilChanged()
                .subscribe(::applyTextParams)

            disposer += liveData.draftMentions
                .filter {
                    it.messageText.isNotBlank()
                }.subscribe { messageTextWithMentions ->
                    val mentionsObject = getServiceObject(messageTextWithMentions.mentionsJson)
                    val mentions = mentionParser.getMentionsFromServiceObject(mentionsObject)
                    if (mentions.isNotEmpty()) {
                        mentionTextWatcher.setMentionsFromDraft(mentions)
                        mentionTextWatcher.setDraftMessage(messageTextWithMentions.messageText)
                    }
                }
        }

        attachButton?.apply {
            setOnClickListener {
                liveData.onAttachButtonClick()
                if (isTablet) viewModel.onForceHideKeyboard()
                bindingDependency.attachmentsDelegate.onBottomMenuClick(this)
            }

            disposer += liveData.attachmentsButtonVisible
                .distinctUntilChanged()
                .subscribe {
                    visibility = if (it) View.VISIBLE else View.GONE
                    messagePanelHelper.setMessageContainerMargins(this, messageContainer)
                }

            disposer += liveData.attachmentsButtonEnabled.subscribe { isEnabled = it }
        }

        quickReplyButton?.apply {
            disposer += liveData.showQuickReplyButton.subscribe {
                isVisible = it
                val padding = if (isVisible && quickReplyButton.width == 0) {
                    context.getDimenPx(RDesign.attr.iconSize_7xl)
                } else {
                    quickReplyButton.width
                }
                editTextView.setRightPadding(padding)
            }
        }

        disposer += liveData.attachmentsDeletable
            .distinctUntilChanged()
            .subscribe { isRemovable ->
                attachmentsView?.setAttachmentsDeletable(isRemovable)
            }

        disposer += liveData.attachmentsRestartable
            .distinctUntilChanged()
            .subscribe { isRestartable ->
                attachmentsView?.setAttachmentsRestartable(isRestartable)
            }

        disposer += liveData.attachmentsErrorVisible
            .distinctUntilChanged()
            .subscribe { isErrorVisible ->
                attachmentsView?.setAttachmentsErrorVisible(isErrorVisible)
            }

        bindingDependency.signDelegate?.apply {
            checkNotNull(signButton) { "Unable to activate signing without sign button" }.let { button ->
                button.setOnClickListener { onSignButtonClicked() }
                //Необходимо учитывать видимость вложений, так как если на экране мало места и вложения скрываются,
                //кнопку подписи тоже скрываем
                disposer += combineLatest(panelViewModel.attachmentPresenter.attachmentsObservable
                    .observeOn(Schedulers.computation())
                    .map { it.toAttachments() }
                    .map { isSignButtonVisible(it) },
                    liveData.attachmentsVisibility,
                    liveData.isEditing
                ) { buttonVisibility, attachmentsVisibility, isEditing ->
                    buttonVisibility && attachmentsVisibility != AttachmentsViewVisibility.GONE && !isEditing
                }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { button.isVisible = it }
            }
        }

        recipients?.apply {
            setOnClickListener { liveData.onRecipientButtonClick() }

            recipientsClearListener = liveData::onRecipientClearButtonClick
            disposer += liveData.recipientsViewData.subscribe { data = it }
            disposer += liveData.recipientsVisibility.subscribe { isVisible = it }
        }
        liveData.setRecipientsFeatureEnabled(
            MessagePanelComponentProvider[messagePanel.context].recipientSelectionProvider != null
        )

        quote?.apply {
            setCloseListener(liveData::ordinalCancelClickListener)
            disposer += combineLatest(
                liveData.originalMessageTitle,
                liveData.originalMessageSubtitle
            ) { title, subtitle -> MessagePanelQuote(title.toString(), subtitle.toString()) }
                .subscribe(::data::set)
            disposer += liveData.quotePanelVisible.subscribe { isVisible = it }
        }

        disposer += liveData.toast
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { SbisPopupNotification.pushToast(messagePanel.context, it) }

        disposer += liveData.recipientSelectionScreen
            .delay(NAVIGATE_DELAY_MS, TimeUnit.MILLISECONDS)
            .subscribe { config ->
                val recipientSelectionProvider = MessagePanelComponentProvider[messagePanel.context]
                    .recipientSelectionProvider ?: return@subscribe

                viewDependency.hideKeyboard()
                val intent = recipientSelectionProvider.getRecipientSelectionIntent(messagePanel.context, config)
                // Теоретически, возможна ситуация когда панелька создана от AppContext
                // и вызов реципиентов происходит в тот момент, когда активити уже ушла в бекграунд.
                // Я надеюсь что панель никогда не будет использоваться в подобных сценариях,
                // но по просьбе заказчиков компонента добавлена эта проверка.
                // FLAG_ACTIVITY_NEW_TASK меняет поведение backstack поэтому под if.
                if (messagePanel.context.applicationContext === messagePanel.context)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                messagePanel.context.startActivity(intent)
            }

        disposer += liveData.recipientSelectionMenu
            .subscribe { config ->
                val container = movablePanelContainer ?: return@subscribe
                val existingMenuDelegate = tryToGetExistSelectionMenuDelegate(container)
                if (existingMenuDelegate != null) {
                    existingMenuDelegate.show()
                } else {
                    setupMentionsMenu(config, container)
                }
            }

        disposer += mentionTextWatcher.needStartMentionSelection.subscribe { needStart ->
            if (needStart) panelViewModel.liveData.requestRecipientSelectionMenu()
        }

        disposer += liveData.requestCustomRecipient.subscribe {
            bindingDependency.customRecipientListener?.showRecipientSelectionDialog()
        }

        disposer += liveData.clearCustomRecipients.subscribe {
            bindingDependency.customRecipientListener?.clearRecipientSelectionResult()
        }

        disposer += panelViewModel.messageSending.subscribe {
            bindingDependency.onMessageSending?.invoke()
        }
        disposer += panelViewModel.messageSent.subscribe { bindingDependency.onMessageSent?.invoke(it) }
        disposer += panelViewModel.messageEdit.subscribe { bindingDependency.onMessageEdit?.invoke(it) }
        disposer += panelViewModel.messageEditCanceled.subscribe { bindingDependency.onMessageEditCanceled?.invoke() }
        disposer += panelViewModel.messageAttachErrorClicked.subscribe { bindingDependency.onMessageAttachmentErrorClicked?.invoke(it) }
        disposer += panelViewModel.onKeyboardForcedHidden.subscribe { bindingDependency.onKeyboardForcedHidden?.invoke() }
        disposer += liveData.recipients.withLatestFrom(liveData.recipientsSelected, ::Pair)
            .skip(1)
            .subscribe { (recipients, selectedByUser) ->
                val departmentsPersons = recipients.filterIsInstance<RecipientDepartmentItem>()
                    .map { it.personModels }
                    .flatten()
                val persons = recipients.filterIsInstance<RecipientPersonItem>()
                val allPersons = departmentsPersons + persons
                bindingDependency.onRecipientsChanged?.invoke(allPersons, selectedByUser)
            }
        disposer += liveData.messageText.subscribe {
            mentionTextWatcher.carriagePosition = editTextView.selectionStart
            bindingDependency.onTextChanged?.invoke(it.value.orEmpty())
        }
        disposer += panelViewModel.attachmentPresenter.attachmentsObservable.subscribe { bindingDependency.onAttachmentListChanged?.invoke(it) }

        disposer += mProgressDialogDelegate.bind(panelViewModel.liveData.progressDialog)
        disposer += mAlertDialogDelegate.bind(panelViewModel.liveData.alertDialog)
        isBinded = true
    }

    private fun setupMentionsMenu(config: RecipientSelectionMenuConfig, container: ViewGroup) {
        val recipientSelectionMenuProvider = MessagePanelComponentProvider[messagePanel.context]
            .recipientSelectionMenuProvider ?: return
        val selectionMenu = recipientSelectionMenuProvider.getRecipientSelectionMenu(config)
        val childManager = bindingDependency.childFragmentManager
        selectionMenu.setupMenu(childManager, container.id)
        val delegate = selectionMenu.getSelectionMenuDelegate()
        startMentionSelection(delegate)
    }

    private fun startClipboardFilesHandling(editTextView: MessagePanelEditText) {
        disposer += editTextView.clipboardFileUri
            .subscribeOn(Schedulers.io())
            .subscribe { stringUri ->
                viewModel.attachmentPresenter.addAttachments(listOf(stringUri))
            }
    }

    private fun tryToGetExistSelectionMenuDelegate(container: ViewGroup): SelectionMenuDelegate? {
        return if (container.childCount > 0) {
            val selectionMenu = bindingDependency.childFragmentManager
                .fragments
                .find { it is SelectionMenu }
                as? SelectionMenu
            selectionMenu?.getSelectionMenuDelegate()
        } else {
            null
        }
    }

    private fun startMentionSelection(menuDelegate: SelectionMenuDelegate) {
        subscribeOnMentionTextWatcher(menuDelegate)
        waitMentionSelectionResult()
    }

    private fun subscribeOnMentionTextWatcher(menuDelegate: SelectionMenuDelegate) {
        disposer += mentionTextWatcher.needShowMentionSelection.subscribe { needShow ->
            if (needShow) menuDelegate.show() else menuDelegate.hide()
        }
        disposer += mentionTextWatcher.mentionSearchQuery.subscribe { query ->
            menuDelegate.searchQuery.tryEmit(query)
        }
        messagePanel.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            menuDelegate.hasSelectableItems.collect {
                if (!it) {
                    editTextView.text?.let { text -> mentionTextWatcher.limitEnterMention(text) }
                }
            }
        }
    }

    fun unbind() {
        if (lifecycleRegistry.currentState != Lifecycle.State.INITIALIZED) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
        disposer.clear()
        isBinded = false
        mentionTextWatcher.reset()
    }

    private fun startKeyboardEventHandling(
        mediator: KeyboardEventMediator,
        enabledStateObservable: Observable<Boolean>,
        onFocusChanged: FocusChangeListener?
    ) {
        editTextView.setOnKeyPreImeListener(BackButtonListener(mediator))
        editTextView.onFocusChangeListener = FocusListener(mediator, onFocusChanged)

        val editTextFocusCleaner = EditTextFocusCleaner()
        val keyboardHandler = KeyboardEventHandlerImpl()
        if (viewDependency.shouldAdjustPositionOnKeyboardChanges) {
            disposer += keyboardHandler.transitionY.map(Int::toFloat).subscribe(messagePanel::setTranslationY)
        }
        // обработка запросов на подъём клавиатуры
        disposer += keyboardHandler.showKeyboard
            .filter { it }
            .doOnNext { editTextFocusCleaner.setIgnoreAdjustHelperEvents(true) }
            .toFocusRequestObservable(editTextView)
            /*
            Важно запрашивать клавиатуру в mainThread так как опубликованный из фонового потока запрос может быть
            исполнен уже после поворота экрана (когда это неактуально)
             */
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable {
                editTextView.showKeyboard()
                    .doOnComplete { editTextFocusCleaner.setIgnoreAdjustHelperEvents(false) }
            }
            .subscribe()
        // обработка запросов на скрытие клавиатуры
        disposer += keyboardHandler.showKeyboard
            .filter { !it }
            .flatMapCompletable { editTextView.hideKeyboard() }
            .subscribe()
        disposer += Observable.combineLatest(
            mediator.keyboardState,
            enabledStateObservable,
            { state, enabled -> state to enabled })
            .skipOpenedByRequestIfDisabled()
            .subscribe(keyboardHandler)

        disposer += editTextFocusCleaner.subscribeOnFocusClearing(editTextView, mediator.keyboardState)
    }

    private fun waitMentionSelectionResult() {
        viewModel.recipientsManager?.let { recipientsManager ->
            disposer += recipientsManager.getSelectionResultObservable(MESSAGE_PANEL_SELECTION_MENU_REQUEST_KEY)
                .subscribe {
                    it.data.allPersons.lastOrNull()?.let { person ->
                        insertMentionInMessagePanel(person)
                        if (it.data.appended) { panelViewModel.addRecipients(listOf(person.uuid), true) }
                    }
                }
        }
    }

    private fun insertMentionInMessagePanel(mentionPerson: RecipientPerson) {
        val text = editTextView.text ?: return
        val enterMention = mentionTextWatcher.getEnterMention(text) ?: return
        mentionTextWatcher.onBeforeMentionInsertion()
        text.insertMention(mentionPerson, enterMention)
        editTextView.setSelection(editTextView.text?.length ?: 0)
        mentionTextWatcher.onMentionInserted(editTextView.editableText)
    }

    private fun Editable.insertMention(mentionPerson: RecipientPerson, enterMention: EnterMentionSpan) {
        val mention = "@${mentionPerson.name.lastName} ${mentionPerson.name.firstName}".trim()
        val startPos = enterMention.data.start
        val mentionEnd = startPos + mention.lastIndex + 1
        removeSpan(enterMention)
        replace(
            startPos,
            enterMention.data.end,
            "$mention "
        )
        setSpan(
            MentionSpan(messagePanel.context).apply { setPersonUuid(mentionPerson.uuid) },
            startPos,
            mentionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

private const val NAVIGATE_DELAY_MS = 70L