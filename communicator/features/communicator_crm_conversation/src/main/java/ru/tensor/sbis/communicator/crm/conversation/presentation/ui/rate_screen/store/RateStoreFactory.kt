package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.data.LOWEST_RATING
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store.RateStore.Intent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store.RateStore.Label
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store.RateStore.State
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.LOW_RATING_LIMIT
import ru.tensor.sbis.communicator.generated.MessageController
import timber.log.Timber
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [RateStore]
 * Содержит реализацию бизнес-логики экрана.
 *
 * @author dv.baranov
 */
internal class RateStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    private val messageController: MessageController by lazy { MessageController.instance() }

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): RateStore =
        object :
            RateStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = RATE_STORE_NAME,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action

    private sealed interface Message {

        /** Изменили текст в поле ввода. */
        data class OnTextChanged(val newText: CharSequence) : Message

        /** Изменили рейтинг. */
        data class OnRatingChanged(val rating: Int) : Message

        /**
         * Нужно включить/отключить валидацию поля ввода комментария.
         */
        data class NeedShowValidation(val showValidationStatus: Boolean) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {}

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.OnCommentFocusChanged -> {}
            is Intent.OnTextChanged -> dispatch(Message.OnTextChanged(intent.newText))
            is Intent.SendButtonClicked -> {
                val state = getState()
                val ratingValid = state.currentRating > 0
                when {
                    ratingValid && intent.disableComment -> {
                        rateChat(intent, state)
                    }
                    state.currentRating in LOWEST_RATING..LOW_RATING_LIMIT && state.comment.isEmpty() -> {
                        dispatch(Message.NeedShowValidation(true))
                    }
                    ratingValid -> {
                        rateChat(intent, state)
                    }
                }
                Unit
            }
            is Intent.OnRatingChanged -> {
                if (intent.rating > LOW_RATING_LIMIT) {
                    dispatch(Message.NeedShowValidation(false))
                }
                dispatch(Message.OnRatingChanged(intent.rating))
            }
        }

        private fun rateChat(intent: Intent.SendButtonClicked, state: State) = scope.launch {
            val status = withContext(Dispatchers.IO) {
                messageController.rateChat(
                    intent.messageUuid,
                    intent.rateType,
                    state.currentRating,
                    state.comment,
                )
            }
            if (status.errorCode != ErrorCode.SUCCESS) {
                Timber.e("${RateStoreFactory::class.java} - ${status.errorMessage}")
            }
            publish(Label.End)
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.OnTextChanged -> copy(comment = msg.newText.toString())
            is Message.OnRatingChanged -> copy(currentRating = msg.rating)
            is Message.NeedShowValidation -> copy(showValidationStatus = msg.showValidationStatus)
        }
    }
}

private const val RATE_STORE_NAME = "RATE_STORE_NAME"
