package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinkOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListFilterHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.State
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListComponentFactory
import timber.log.Timber
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [ConversationLinksListStore]
 * Содержит реализацию бизнес-логики экрана.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListStoreFactory @Inject constructor(
    private val context: Context,
    private val storeFactory: StoreFactory,
    private val listComponentFactory: ConversationLinksListComponentFactory,
    private val filterHolder: ConversationLinksListFilterHolder,
    private val conversationLinksListInteractor: ConversationLinksListInteractor,
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): ConversationLinksListStore =
        object :
            ConversationLinksListStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = STORE_NAME,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action

    private sealed interface Message

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {}

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.ShowLinkMenu -> { publish(Label.ShowLinkMenu(intent.model, intent.anchor)) }
            is Intent.MenuOptionSelected -> {
                scope.launch {
                    runCatching {
                        onOptionSelected(intent.option, intent.model, intent.anchor)
                    }.onFailure { error -> Timber.e(error) }
                }
                Unit
            }
            is Intent.SearchQueryChanged -> {
                filterHolder.setSearchQuery(intent.query)
                listComponentFactory.get()?.reset(filterHolder())
                Unit
            }
        }

        private suspend fun onOptionSelected(option: ConversationLinkOption, model: LinkViewModel, anchor: View) {
            when (option) {
                ConversationLinkOption.OPEN_LINK -> { doOnMain { anchor.fakeClick() } }
                ConversationLinkOption.PIN -> { conversationLinksListInteractor.pin(model.id) }
                ConversationLinkOption.UNPIN -> { conversationLinksListInteractor.unpin(model.id) }
                ConversationLinkOption.COPY -> {
                    doOnMain { ClipboardManager.copyToClipboard(context, model.link) }
                }
                ConversationLinkOption.GO_TO_MESSAGE -> { publish(Label.GoToMessage(model.messageId)) }
                ConversationLinkOption.DELETE -> { conversationLinksListInteractor.delete(model.id) }
            }
        }

        private suspend fun doOnMain(function: () -> Unit) {
            withContext(Dispatchers.Main) { function() }
        }

        private fun View.fakeClick() {
            dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0))
            dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0))
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = State()
    }
}

private const val STORE_NAME = "STORE_NAME"
