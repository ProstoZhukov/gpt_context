package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.LinkAdditionFragment
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore

/**
 * Связывает [LinkAdditionFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class LinkAdditionController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: LinkAdditionView.Factory,
    private val linkAdditionStoreFactory: LinkAdditionStoreFactory
) {

    private val store = fragment.provideStore { linkAdditionStoreFactory.create(it) }

    init {
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

    private fun toIntent(event: LinkAdditionView.Event): LinkAdditionStore.Intent = event.toIntent()

    private fun toModel(storeState: LinkAdditionStore.State): LinkAdditionView.Model =
        LinkAdditionView.Model(storeState.inputValue)

    private fun LinkAdditionStore.Label.consume() = when (this) {
        LinkAdditionStore.Label.Close -> fragment.parentFragment?.castTo<DialogFragment>()?.dismiss()
    }
}