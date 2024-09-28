package ru.tensor.sbis.onboarding_tour.ui.store

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.onboarding_tour.data.TourPage
import ru.tensor.sbis.onboarding_tour.domain.TourInteractor
import ru.tensor.sbis.onboarding_tour.ui.store.OnboardingTourStore.*
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.DismissCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.builders.PageCommand
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect
import ru.tensor.sbis.verification_decl.onboarding_tour.data.BackgroundEffect.*
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/** Действия начальной загрузки обрабатываемые [TourExecutor]. */
internal sealed interface Action {
    /**
     * Начать загружать тур с названием [name] или следующий активный тур если [name] null.
     */
    class LoadTour(val name: OnboardingTour.Name) : Action

    /**
     * Восстановить тур с названием [name] и состоянием [state].
     */
    class RestoreTour(val name: OnboardingTour.Name, val state: State) : Action
}

/** Сообщение от [TourExecutor] обрабатываемые [TourReducer]. */
internal sealed interface Message {
    /** Сообщение об обновлении тура. */
    class UpdateTour(
        val pageCount: Int,
        val pagePosition: Int,
        val pages: List<TourPage>,
        val isSwipeEnabled: Boolean,
        val swipeCloseable: Boolean,
        val hasLaunchedCommand: Boolean,
        val backgroundEffect: BackgroundEffect,
        val dismissCommand: DismissCommand?
    ) : Message

    /** Сообщение об обновлении страницы в зависимости от требуемых разрешений. */
    class GrantedPermissions(
        val haveNotGranted: Boolean
    ) : Message

    /** Сообщение о необходимости выполнить переход на страницу с позицией [position]. */
    data class MoveToPosition(
        val position: Int
    ) : Message

    /** Сообщение об запуске анимации тура. */
    object ShowAnimation : Message

    /** Сообщение об запуске [PageCommand]. */
    data class OnInitiatedRestorableCommand(
        val position: Int
    ) : Message
}

internal class TourExecutor(
    private val tourName: OnboardingTour.Name,
    private val tourInteractor: TourInteractor,
    private val ioDispatcher: CoroutineDispatcher
) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

    override fun executeAction(
        action: Action,
        getState: () -> State
    ) = when (action) {
        is Action.LoadTour -> Intent.ShowTour
        is Action.RestoreTour -> Intent.ReshowTour(action.state)
    }.let(::executeIntent)

    override fun executeIntent(
        intent: Intent,
        getState: () -> State
    ) {
        when (intent) {
            is Intent.ShowTour -> {
                flow {
                    emit(tourInteractor.getContent(tourName))
                }
                    .flowOn(ioDispatcher)
                    .onEach {
                        val effect = if (it.rules.backgroundEffect == DYNAMIC) {
                            STATIC
                        } else {
                            it.rules.backgroundEffect
                        }
                        val message = Message.UpdateTour(
                            pageCount = it.pages.size,
                            pagePosition = it.startPosition,
                            pages = it.pages,
                            isSwipeEnabled = it.rules.swipeTransition,
                            swipeCloseable = it.rules.swipeCloseable,
                            hasLaunchedCommand = false,
                            backgroundEffect = effect,
                            dismissCommand = it.command
                        )
                        dispatch(message)
                        if (it.rules.backgroundEffect == DYNAMIC) {
                            executeIntent(Intent.ShowBackgroundEffect)
                        }
                        withContext(ioDispatcher) {
                            tourInteractor.markShown(tourName)
                        }
                    }
                    .launchIn(scope)
            }

            is Intent.ReshowTour -> {
                flow {
                    emit(tourInteractor.getContent(tourName, intent.state.pageId))
                }
                    .flowOn(ioDispatcher)
                    .onEach { content ->
                        val message = Message.UpdateTour(
                            pageCount = content.pages.size,
                            pagePosition = content.startPosition,
                            pages = content.pages,
                            isSwipeEnabled = intent.state.isSwipeSupported,
                            swipeCloseable = intent.state.swipeCloseable,
                            hasLaunchedCommand = intent.state.hasLaunchedRestorableCommand,
                            backgroundEffect = intent.state.backgroundEffect,
                            dismissCommand = content.command
                        )
                        dispatch(message)
                        if (intent.state.hasLaunchedRestorableCommand) {
                            val command = content.pages.getOrNull(content.startPosition)?.button?.command
                            if (command != null) {
                                publish(
                                    Label.InitiateCommand(
                                        position = content.startPosition,
                                        command = command,
                                        isLastPage = intent.state.isInLastPassage,
                                        byUser = false
                                    )
                                )
                            }
                        }
                    }
                    .launchIn(scope)
            }

            is Intent.UpdateForthcomingPages -> {
                flow {
                    emit(tourInteractor.getContent(tourName, getState().pageId))
                }
                    .flowOn(ioDispatcher)
                    .onEach { content ->
                        val message = Message.UpdateTour(
                            pageCount = content.pages.size,
                            pagePosition = content.startPosition,
                            pages = content.pages,
                            isSwipeEnabled = getState().isSwipeSupported,
                            swipeCloseable = getState().swipeCloseable,
                            hasLaunchedCommand = getState().hasLaunchedRestorableCommand,
                            backgroundEffect = getState().backgroundEffect,
                            dismissCommand = getState().dismissCommand
                        )
                        dispatch(message)
                        if (!getState().isInLastPassage) {
                            dispatch(
                                Message.MoveToPosition(position = intent.position.inc())
                            )
                        }
                    }
                    .launchIn(scope)
            }

            is Intent.ShowBackgroundEffect -> {
                flow {
                    emit(tourInteractor.isAnimated())
                }
                    .flowOn(ioDispatcher)
                    .onEach { isAnimated ->
                        if (isAnimated) dispatch(Message.ShowAnimation)
                    }
                    .launchIn(scope)
            }

            is Intent.OnPageChanged -> getState().apply {
                if (pagePosition != intent.position) {
                    dispatch(Message.MoveToPosition(intent.position))
                }
            }

            is Intent.GrantedPermissions -> {
                val state = getState()
                if (state.pagePosition != intent.position) return
                val isMandatory = state.passages[intent.position].isMandatoryPermits
                val haveNotGrantedOnRequest = intent.onRequest && intent.notGranted.isNotEmpty()
                val message = if (isMandatory && haveNotGrantedOnRequest) {
                    Message.GrantedPermissions(true)
                } else if (intent.onRequest) {
                    if (!state.isInLastPassage && !state.requireCommand) {
                        Message.MoveToPosition(state.pagePosition.inc())
                    } else {
                        Message.GrantedPermissions(false)
                    }
                } else {
                    Message.GrantedPermissions(intent.notGranted.isNotEmpty())
                }
                dispatch(message)
            }

            is Intent.InitDeferredCommand -> getState().apply {
                if (intent.command.restorable) {
                    dispatch(Message.OnInitiatedRestorableCommand(intent.position))
                }
                publish(
                    Label.InitiateCommand(
                        position = intent.position,
                        command = intent.command,
                        isLastPage = intent.isLastPage,
                        byUser = true
                    )
                )
            }

            is Intent.ObserveDeferredCommand -> getState().apply {
                scope.launch {
                    intent.flow.first { action ->
                        publish(
                            Label.PageCommandResult(
                                position = intent.position,
                                action = action,
                                isLastPage = intent.isLastPage
                            )
                        )
                        true
                    }
                }
            }

            is Intent.OnCommandPerformed -> getState().apply {
                if (pagePosition != intent.position) return
                if (isInLastPassage) return
                when (intent.action) {
                    PageCommand.ResultantAction.GO_AHEAD -> {
                        dispatch(Message.MoveToPosition(position = pagePosition.inc()))
                    }

                    PageCommand.ResultantAction.UPDATE_AND_GO -> {
                        executeIntent(Intent.UpdateForthcomingPages(intent.position))
                    }

                    PageCommand.ResultantAction.NOTHING -> Unit
                }
            }

            is Intent.OnCloseTour -> getState().apply {
                dismissCommand?.invoke()
                tourInteractor.onTourClose()
            }
        }
    }
}