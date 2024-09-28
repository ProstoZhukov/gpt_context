package ru.tensor.sbis.share_menu.utils.base_mvi.reducer

import com.arkivanov.mvikotlin.core.store.Reducer

/**
 * Use-case для обновления состояния [State] для функции [Reducer].
 *
 * @author vv.chekurda
 */
internal interface ReducerUseCase<State> {

    /**
     * Создать из текущего состояние новое.
     */
    fun State.reduce(): State
}