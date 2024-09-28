package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.share.ThemeShareSelectionResultListener
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.MessagesShareFragment
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store.MessagesShareStore
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store.MessagesShareStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.mvi_extension.subscribe
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate

/**
 * Связывает [MessagesShareFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class MessagesShareController @AssistedInject constructor(
    @Assisted fragment: Fragment,
    viewFactory: MessagesShareView.Factory,
    private val router: MessagesShareRouter,
    private val messagesShareStoreFactory: MessagesShareStoreFactory
) : ThemeShareSelectionResultListener {

    private val store = fragment.provideStore { messagesShareStoreFactory.create(it) }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))

        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                viewLifecycleOwner.lifecycle.subscribe(
                    onStart = { store.accept(MessagesShareStore.Intent.GoToConversationSelectionState) }
                )
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
        }
    }

    private fun toIntent(event: MessagesShareView.Event): MessagesShareStore.Intent = when (event) {
        is MessagesShareView.Event.SendButtonClicked -> MessagesShareStore.Intent.SendButtonClicked
        is MessagesShareView.Event.OnMessagePanelFocusChanged -> MessagesShareStore.Intent.OnMessagePanelFocusChanged(event.isFocused)
        is MessagesShareView.Event.OnTextChanged -> MessagesShareStore.Intent.OnTextChanged(event.newText)
    }

    private fun toModel(storeState: MessagesShareStore.State): MessagesShareView.Model =
        MessagesShareView.Model(
            storeState.shareState,
            storeState.isSendButtonEnabled,
            storeState.selectedConversation,
            storeState.messagePanelText.toString()
        )

    private fun MessagesShareStore.Label.consume() = when (this) {
        MessagesShareStore.Label.EndShare -> router.endShare()
    }

    override fun onConversationSelected(model: ConversationModel) {
        store.accept(
            MessagesShareStore.Intent.HandleSelectionResult(model)
        )
    }

    /** Инициализировать вью-контролллер меню "поделиться" */
    fun initMenuController(controller: ShareMenuDelegate) {
        store.accept(MessagesShareStore.Intent.InitMenuController(controller))
    }

    /** Обработать нажатие кнопки *назад* */
    fun onBackPressed(): Boolean {
        store.accept(MessagesShareStore.Intent.NavigateBack)
        return true
    }
}
