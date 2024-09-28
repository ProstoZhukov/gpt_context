package ru.tensor.sbis.share_menu.utils.base_mvi.executor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineScope

/**
 * Use-case для подготовки и выполнения действий [CoroutineExecutor].
 *
 * @author vv.chekurda
 */
internal interface ExecutorUseCase<State, Message, Label> {

    /**
     * Подготовить use-case к выполнения.
     */
    fun prepare(
        executorScope: CoroutineScope,
        dispatch: (Message) -> Unit,
        publish: (Label) -> Unit
    ): ExecutorUseCase<State, Message, Label>

    /**
     * Выполнить use-case.
     */
    fun execute(getState: () -> State)
}