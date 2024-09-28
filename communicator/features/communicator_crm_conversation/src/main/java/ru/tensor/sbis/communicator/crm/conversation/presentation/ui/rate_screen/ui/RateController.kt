package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.RateFragment
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store.RateStore
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store.RateStoreFactory
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore

/**
 * Связывает [RateFragment] и компоненты MVI.
 *
 * @author dv.baranov
 */
internal class RateController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: RateView.Factory,
    rateStoreFactory: RateStoreFactory,
) {
    private val store = fragment.provideStore { rateStoreFactory.create(it) }

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

    private fun toIntent(event: RateView.Event): RateStore.Intent = when (event) {
        is RateView.Event.SendButtonClicked -> RateStore.Intent.SendButtonClicked(
            event.messageUuid,
            event.rateType,
            event.disableComment
        )
        is RateView.Event.OnCommentFocusChanged -> RateStore.Intent.OnCommentFocusChanged(event.isFocused)
        is RateView.Event.OnTextChanged -> RateStore.Intent.OnTextChanged(event.newText)
        is RateView.Event.OnRatingChanged -> RateStore.Intent.OnRatingChanged(event.rating)
    }

    private fun toModel(storeState: RateStore.State): RateView.Model =
        RateView.Model(
            storeState.currentRating,
            storeState.comment,
            storeState.showValidationStatus
        )

    private fun RateStore.Label.consume() = when (this) {
        RateStore.Label.End -> fragment.parentFragmentManager.beginTransaction().remove(fragment).commit()
    }
}
