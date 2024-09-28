package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui

import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.communicator_share_messages.databinding.CommunicatorShareMessagesFragmentBinding
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.MessagesShareFragment
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.data.MessagesShareState
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ShareMessagePanelCoreFactory
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ShareMessageServiceDependency
import ru.tensor.sbis.communicator.core.views.conversation_views.ConversationItemView
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool
import ru.tensor.sbis.design.cloud_view.content.utils.DefaultMessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.model.DefaultCloudViewData
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.delegate.MessagePanelInitializerDelegate

/**
 * Реализация View содержиого экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
internal class MessagesShareViewImpl(
    private val binding: CommunicatorShareMessagesFragmentBinding,
    private val fragment: MessagesShareFragment
) : BaseMviView<MessagesShareView.Model, MessagesShareView.Event>(),
    MessagesShareView {

    private var messagePanelController: MessagePanelController<Any, Any, Any>? = null
    private var themeResult: ConversationModel? = null
    private var isSendButtonEnabled: Boolean = false
    private val shareMessagePanelCoreFactory = ShareMessagePanelCoreFactory()

    private val messagePanel = binding.communicatorShareMessagesMessagePanel
    private val selectedConversationContainer: FrameLayout = binding.communicatorShareMessagesSelectedConversation
    private val conversationListSelection = binding.communicatorShareMessagesSelectionContainer
    private val cloudView = binding.communicatorShareMessagesSendingMessage

    init {
        cloudView.setTextHolder(DefaultMessageBlockTextHolder())
        cloudView.textMaxLines = MAX_LINES_OF_CLOUD_VIEW
    }

    override val renderer: ViewRenderer<MessagesShareView.Model> =
        diff {
            diff(
                get = MessagesShareView.Model::shareState,
                set = { handleNewState(it) }
            )
            diff(
                get = MessagesShareView.Model::selectedConversation,
                set = { handleSelectedConversation(it) }
            )
            diff(
                get = MessagesShareView.Model::isSendButtonEnabled,
                set = { handleSendButtonEnabled(it) }
            )
            diff(
                get = MessagesShareView.Model::messagePanelText,
                set = { handleMessagePanelText(it) }
            )
        }

    private fun handleNewState(shareState: MessagesShareState) {
        when (shareState) {
            MessagesShareState.CONVERSATION_SELECTION -> {
                conversationListSelection.isVisible = true
                selectedConversationContainer.isVisible = false
                cloudView.isVisible = false
                closeMessagePanel()
            }
            MessagesShareState.ENTERING_COMMENT -> {
                conversationListSelection.isVisible = false
                selectedConversationContainer.isVisible = true
                initMessagePanelController()
                openMessagePanel()
            }
            MessagesShareState.SENDING -> {
                cloudView.isVisible = !cloudView.data.text.isNullOrEmpty()
                closeMessagePanel()
            }
        }
    }

    private fun openMessagePanel() {
        messagePanel.showKeyboard()
        messagePanel.isVisible = true
    }

    private fun closeMessagePanel() {
        messagePanel.hideKeyboard()
        messagePanel.isVisible = false
    }

    private fun initMessagePanelController() {
        messagePanelController = MessagePanelInitializerDelegate(
            context = fragment.requireContext(),
            fragment = fragment,
            withAudioMessage = false,
            messageServiceDependency = ShareMessageServiceDependency()
        ).initMessagePanel(messagePanel, ShareMessagePanelCoreFactory().createCoreConversation())
        with(messagePanelController!!) {
            onFocusChanged = { isFocused -> dispatch(MessagesShareView.Event.OnMessagePanelFocusChanged(isFocused)) }
            onMessageSending = { dispatch(MessagesShareView.Event.SendButtonClicked) }
            onTextChanged = { newText -> dispatch(MessagesShareView.Event.OnTextChanged(newText)) }
        }
    }

    private fun handleSelectedConversation(model: ConversationModel?) {
        themeResult = model
        if (model != null) {
            handleSendButtonEnabled(isSendButtonEnabled)
            selectedConversationContainer.isVisible = true
            selectedConversationContainer.let {
                val context = binding.root.context
                val conversationView = ConversationItemView(
                    context = context,
                    viewPool = ConversationItemsViewPool(context)
                ).apply {
                    isSharingMode = true
                    bind(model)
                    background = null
                }
                selectedConversationContainer.removeAllViews()
                selectedConversationContainer.addView(conversationView)
            }
        } else {
            selectedConversationContainer.isVisible = false
        }
    }

    private fun handleSendButtonEnabled(isEnabled: Boolean) {
        isSendButtonEnabled = isEnabled
        val conversationUuid = if (isEnabled) themeResult?.uuid else null
        messagePanelController?.setConversationInfo(
            shareMessagePanelCoreFactory.createCoreConversation(conversationUuid)
        )
    }

    private fun handleMessagePanelText(text: String) {
        cloudView.data = DefaultCloudViewData(text)
    }

    override fun onKeyboardMeasure(keyboardHeight: Int): Boolean {
        if (keyboardHeight == 0) {
            messagePanel.onKeyboardCloseMeasure(keyboardHeight)
        } else {
            messagePanel.onKeyboardOpenMeasure(keyboardHeight)
        }
        return true
    }
}

private const val MAX_LINES_OF_CLOUD_VIEW = 2
