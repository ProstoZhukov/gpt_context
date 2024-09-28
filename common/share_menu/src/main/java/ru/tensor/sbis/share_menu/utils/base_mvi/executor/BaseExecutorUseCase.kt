package ru.tensor.sbis.share_menu.utils.base_mvi.executor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineScope

/**
 * Базовая реализация use-case [ExecutorUseCase] для [CoroutineExecutor].
 *
 * @author vv.chekurda
 */
internal abstract class BaseExecutorUseCase<State, Message, Label> : ExecutorUseCase<State, Message, Label> {

    protected lateinit var scope: CoroutineScope

    private lateinit var dispatchCallback: (Message) -> Unit
    private lateinit var publishCallback: (Label) -> Unit

    /**
     * Подножество use-case, которые вспомогательно используются в текущем use-case.
     */
    protected open val subUseCases: List<ExecutorUseCase<State, Message, Label>> = emptyList()

    final override fun prepare(
        executorScope: CoroutineScope,
        dispatch: (Message) -> Unit,
        publish: (Label) -> Unit
    ): ExecutorUseCase<State, Message, Label> = apply {
        scope = executorScope
        dispatchCallback = dispatch
        publishCallback = publish
        subUseCases.forEach { it.prepare(scope, dispatchCallback, publishCallback) }
    }

    override fun execute(getState: () -> State) = Unit

    protected fun dispatch(message: Message) {
        dispatchCallback(message)
    }

    protected fun publish(label: Label) {
        publishCallback(label)
    }
}