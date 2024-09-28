package ru.tensor.sbis.toolbox_decl.init

import kotlinx.coroutines.CoroutineScope

/**
 * Держатель состояния инициализации приложения.
 *
 * @author ar.leschev
 */
interface InitToolsHolder {
    /** Скоуп для инициализации. */
    val scope: CoroutineScope

    /** Установить состояние инициализации МП [state] и отправить событие через шину. */
    fun setInitState(state: InitializationState)

    companion object {
        /** Состояние инициализации МП. Находится тут, чтобы другие МП без базового App-делегата могли хранить значение. */
        @Volatile var appInitializationState: InitializationState = NotInitialized
    }
}