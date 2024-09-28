package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.CXX.SbisException
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyFilterHolder
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore.Intent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore.Label
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.store.QuickReplyStore.State
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.QuickReplyListComponentFactory
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.consultations.generated.QuickReplyCollectionProvider
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.message_view.utils.castTo
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [QuickReplyStore]
 * Содержит реализацию бизнес-логики экрана.
 *
 * @author dv.baranov
 */
internal class QuickReplyStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: QuickReplyListComponentFactory,
    private val filterHolder: QuickReplyFilterHolder,
    private val quickReplyCollectionProvider: DependencyProvider<QuickReplyCollectionProvider>,
    private val quickReplyParams: QuickReplyParams,
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): QuickReplyStore =
        object :
            QuickReplyStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = QUICK_REPLY_STORE_NAME,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = { ExecutorImpl(listComponentFactory, filterHolder) },
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action

    private sealed interface Message {

        /** Обновить состояние строки поискового запроса. */
        data class EnterSearchQuery(val query: String) : Message

        /** Обновить состояние заголовка папки после её смены. */
        data class FolderChanged(val folderTitle: String) : Message
    }

    private inner class ExecutorImpl(
        private val listComponentFactory: QuickReplyListComponentFactory,
        private val filterHolder: QuickReplyFilterHolder,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {}

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.EnterSearchQuery -> {
                filterHolder.setSearchQuery(intent.query)
                listComponentFactory.get()?.reset()
                dispatch(Message.EnterSearchQuery(intent.query))
            }
            is Intent.FolderChanged -> {
                filterHolder.setParentId(intent.uuid)
                listComponentFactory.get()?.reset()
                dispatch(Message.FolderChanged(intent.folderTitle))
            }
            is Intent.OnPinClick -> {
                quickReplyParams.channelUUID?.let {
                    scope.launch {
                        runCatching {
                            changePinnedQuickReply(intent.uuid, intent.isPinned, it)
                        }.onFailure { error ->
                            error.castTo<SbisException>()?.errorMessage?.let {
                                SbisPopupNotification.push(
                                    SbisPopupNotificationStyle.WARNING,
                                    it,
                                    SbisMobileIcon.Icon.smi_information.character.toString()
                                )
                            }
                            Timber.w(error.cause)
                        }
                    }
                }
                Unit
            }
            Intent.NoAction -> Unit
        }

        private suspend fun changePinnedQuickReply(
            id: UUID,
            isPinned: Boolean,
            channel: UUID,
        ) = withContext(Dispatchers.IO) {
            quickReplyCollectionProvider.get().changePinnedQuickReply(id, isPinned, channel)
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.EnterSearchQuery -> copy(searchText = msg.query)
            is Message.FolderChanged -> copy(folderTitle = msg.folderTitle)
        }
    }
}

private const val QUICK_REPLY_STORE_NAME = "QUICK_REPLY_STORE_NAME"
