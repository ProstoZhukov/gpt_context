package ru.tensor.sbis.share_menu.utils.base_mvi.reducer

import com.arkivanov.mvikotlin.core.store.Reducer

/**
 * Реализация [Reducer] с шаблонным обновлением состояния через [ReducerUseCase].
 *
 * @author vv.chekurda
 */
internal class SimpleReducer<State, Message> :
    Reducer<State, Message> where Message : ReducerUseCase<State> {

    override fun State.reduce(msg: Message): State =
        msg.run { reduce() }
}

