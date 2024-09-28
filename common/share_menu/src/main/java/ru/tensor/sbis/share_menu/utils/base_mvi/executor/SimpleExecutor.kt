package ru.tensor.sbis.share_menu.utils.base_mvi.executor

import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor

/**
 * Реализация [CoroutineExecutor] с шаблонным выполнением [ExecutorUseCase].
 *
 * @author vv.chekurda
 */
internal class SimpleExecutor<in Intent, in Action, State : Any, Message : Any, Label : Any> :
    CoroutineExecutor<Intent, Action, State, Message, Label>()
    where Action : ExecutorUseCase<State, Message, Label>, Intent : ExecutorUseCase<State, Message, Label> {

    override fun executeAction(action: Action, getState: () -> State) {
        action.prepare(scope, ::dispatch, ::publish)
            .execute(getState)
    }

    override fun executeIntent(intent: Intent, getState: () -> State) {
        intent.prepare(scope, ::dispatch, ::publish)
            .execute(getState)
    }
}