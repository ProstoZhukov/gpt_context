package ru.tensor.sbis.design.message_panel.view.controller

import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.arch.core.util.Function
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility
import ru.tensor.sbis.design.message_panel.MessagePanelPlugin
import ru.tensor.sbis.design.message_panel.R
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelLayout
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Вью-контроллер панели ввода сообщений.
 * TODO WIP https://online.sbis.ru/opendoc.html?guid=6d77f60d-d7c3-455e-b235-5a51bbb843b5
 *
 * @author vv.chekuda
 */
internal class MessagePanelViewController {

    private lateinit var layout: MessagePanelLayout
    private lateinit var viewScope: CoroutineScope

    /**
     * Признак блокировки ввода/установки текста в поле ввода.
     */
    var isInputLocked: Boolean
        get() = layout.inputView.isInputLocked
        set(value) {
            layout.inputView.isInputLocked = value
        }

    val viewModel: MessagePanelViewModel by lazy {
        MessagePanelPlugin.component
            .viewModelComponent
            .create(layout.rootView)
            .viewModel
            .also { initLazyViewModel?.apply(it) }
    }

    private var initLazyViewModel: Function<MessagePanelViewModel, Unit>? = null

    fun attachLayout(layout: MessagePanelLayout, set: AttributeSet?) {
        this.layout = layout
        obtainStyle(set)
    }

    fun onAttachedToWindow() {
        viewScope = MainScope()
        bindLayout()
        viewModel.attachInputView(layout.inputView)
    }

    fun onDetachedFromWindow() {
        viewScope.cancel()
        viewModel.detachInputView()
    }

    fun setTranslationY(translationY: Float) {
        if (translationY <= 0) {
            viewModel.onBottomOffsetChanged(-translationY.toInt())
        }
    }

    private fun obtainStyle(set: AttributeSet?) {
        var inputHintRes = R.string.design_message_panel_enter_message_hint
        var recipientsHint = EMPTY
        var allChosenRecipientsText = EMPTY
        var showRecipients = true
        layout.rootView.context.withStyledAttributes(set = set, attrs = RMPCommon.styleable.MessagePanel) {
            layout.topOffset = getDimensionPixelSize(RMPCommon.styleable.MessagePanel_MessagePanel_topOffset, 0)
            inputHintRes = getResourceId(RMPCommon.styleable.MessagePanel_MessagePanel_input_hint, inputHintRes)
            recipientsHint = getString(RMPCommon.styleable.MessagePanel_MessagePanel_recipientsHintText).orEmpty()
            allChosenRecipientsText = getString(RMPCommon.styleable.MessagePanel_MessagePanel_recipientsAllChosenText).orEmpty()
            showRecipients = getBoolean(RMPCommon.styleable.MessagePanel_MessagePanel_showRecipients, showRecipients)
        }
        initLazyViewModel = Function { viewModel: MessagePanelViewModel ->
            viewModel.apply {
                setHint(hintRes = inputHintRes)
                setRecipientsHint(recipientsHint)
                setRecipientsAllChosenText(allChosenRecipientsText)
                changeRecipientsVisibility(isVisible = showRecipients)
            }
            initLazyViewModel = null
        }
    }

    private fun bindLayout() {
        bindInputField()
        bindSendButton()
        bindAttachButton()
        bindRecipientsPanel()
        bindQuotePanel()
        bindAttachmentsView()
        bindSignButton()
        bindRecordButtons()
        bindOffsets()
    }

    private fun bindInputField() {
        layout.inputView.apply {
            doAfterTextChanged { text -> viewModel.setText(text.toString()) }
            viewModel.text.launchDistinctCollect(::setTextKeepState)
            viewModel.text.combine(viewModel.hint) { text, hint -> text to hint }
                .launchDistinctCollect { (text, hintStringRes) ->
                    setTextKeepState(text)
                    ellipsize = TextUtils.TruncateAt.END.takeIf { text.isNotEmpty() }
                    hint = if (text.isEmpty()) resources.getString(hintStringRes) else null
                }
            viewModel.isEnabled.launchDistinctCollect {
                isEnabled = it
                isFocusableInTouchMode = it
            }
            viewModel.isNewDialog.launchDistinctCollect { isNewDialog ->
                if (isNewDialog) {
                    gravity = Gravity.TOP
                    minLines = INPUT_NEW_DIALOG_MODE_MIN_LINES
                } else {
                    gravity = Gravity.CENTER_VERTICAL
                    minLines = INPUT_MIN_LINES
                }
            }
        }
    }

    private fun bindSendButton() {
        layout.sendButton.apply {
            viewModel.isEnabled.launchCollect { isPanelEnabled ->
                isActivated = isPanelEnabled
                isEnabled = isPanelEnabled
            }
            setDebounceClickListener {
                // TODO state of ViewModel
                val requireRecipients = false
                when {
                    requireRecipients -> viewModel.onRecipientSelectionClicked(context)
                    isActivated -> viewModel.onSendClicked()
                }
            }
        }
    }

    private fun bindAttachButton() {
        layout.attachButton.apply {
            viewModel.attachmentButtonVisible.launchDistinctCollect(::isVisible::set)
            viewModel.isEnabled.launchDistinctCollect(::setEnabled)
            setDebounceClickListener {
                viewModel.onAttachButtonClicked(this)
            }
            // TODO ??? Mock для отображения скрепки, нужно понять где вставлять этот обработчик.
            viewModel.attachmentsSelectionRequest.launchCollect { }
        }
    }

    private fun bindRecipientsPanel() {
        layout.recipientsView.apply {
            viewModel.recipientsHint.launchCollect(::hintText::set)
            viewModel.recipientsAllChosenText.launchCollect(::allChosenText::set)
            viewModel.recipientsVisible.launchCollect(::isVisible::set)
            // TODO у ViewModel пока не работает старая логика
            viewModel.recipients.launchCollect(::data::set)
            recipientsClearListener = viewModel::clearRecipients
        }
    }

    private fun bindQuotePanel() {
        layout.quoteView.apply {
            setCloseListener(viewModel::onQuoteClearClicked)
            viewModel.quoteVisible.launchCollect(::isVisible::set)
            viewModel.quote.launchCollect(layout.quoteView::data::set)
        }
    }

    private fun bindAttachmentsView() {
        layout.attachmentsView.apply {
            actionListener = viewModel
            viewModel.viewAttachments.launchDistinctCollect { attachments ->
                // TODO доработать ограничение видимости на UI
                val hasSpacing = true
                setVisibility(
                    when {
                        attachments.isEmpty() -> AttachmentsViewVisibility.GONE
                        hasSpacing -> AttachmentsViewVisibility.VISIBLE
                        else -> AttachmentsViewVisibility.PARTIALLY
                    }
                )
                submitList(attachments)
            }
        }
    }

    private fun bindSignButton() {
        layout.signButton.apply {
            setDebounceClickListener {
                // TODO ??? Слушатель
            }
            viewModel.viewAttachments.launchDistinctCollect {
                isVisible = it.find {
                    // TODO ??? Из текущей модели никак не вытянуть флаг isOffice
                    false
                } != null
            }
        }
    }

    private fun bindRecordButtons() {
        layout.audioRecordButton.apply {
            isVisible = false
            hasActionListenerState.launchCollect {
                isVisible = it
            }
        }
        layout.videoRecordButton.apply {
            isVisible = false
            hasActionListenerState.launchCollect {
                isVisible = it
            }
        }
    }

    private fun bindOffsets() {
        viewModel.keyboardHeight.launchDistinctCollect(layout::bottomOffset::set)
    }

    private inline fun <T> Flow<T>.launchCollect(crossinline collectHandler: (value: T) -> Unit) {
        viewScope.launch {
            collect { collectHandler(it) }
        }
    }

    private inline fun <T> Flow<T>.launchDistinctCollect(crossinline collectHandler: (value: T) -> Unit) {
        viewScope.launch {
            distinctUntilChanged().collect { collectHandler(it) }
        }
    }

    private inline fun View.setDebounceClickListener(
        debounceTimeMs: Long = 500,
        crossinline onClick: (View) -> Unit
    ) {
        setOnClickListener { view ->
            DebounceActionHandler(debounceTimeMs).handle {
                onClick(view)
            }
        }
    }
}

private const val INPUT_MIN_LINES = 1
private const val INPUT_NEW_DIALOG_MODE_MIN_LINES = 5