package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.util.result_mediator.MessageUuidMediator
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ConversationLinksListFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/**
 * Связывает [ConversationLinksListFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted viewFactory: ConversationLinksListView.Factory,
    private val router: ConversationLinksListRouter,
    private val someStoreFactory: ConversationLinksListStoreFactory
) {

    private val store = fragment.provideStore { someStoreFactory.create(it) }

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))

        with(fragment) {
            attachBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
                bind {
                    view.events.map(::toIntent) bindTo store
                    store.states.map(::toModel) bindTo view
                    store.labels bindTo { it.consume() }
                }
            }
        }
    }

    private fun toIntent(event: ConversationLinksListView.Event): Intent =
        event.toIntent()

    private fun toModel(storeState: ConversationLinksListStore.State): ConversationLinksListView.Model =
        ConversationLinksListView.Model()

    private fun ConversationLinksListStore.Label.consume() = when (this) {
        is ConversationLinksListStore.Label.ShowLinkMenu -> {
            router.showLinkMenu(anchor, model.isPinned, model.messageId != null) { option ->
                store.accept(Intent.MenuOptionSelected(option, model, anchor))
            }
        }
        is ConversationLinksListStore.Label.GoToMessage -> {
            uuid?.let { MessageUuidMediator().provideResult(fragment, it) }
        }
    }

    /** Обработать нажатие кнопки *назад* */
    fun onBackPressed(): Boolean = false

    fun setSearchQuery(query: String) {
        store.accept(Intent.SearchQueryChanged(query))
    }
}