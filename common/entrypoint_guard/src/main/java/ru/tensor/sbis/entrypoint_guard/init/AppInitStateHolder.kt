package ru.tensor.sbis.entrypoint_guard.init

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Хранитель состояния инициализации приложения.
 *
 * @author kv.martyshenko
 */
interface AppInitStateHolder<T> {
    /**
     * Статус инициализации.
     */
    val initStatus: StateFlow<InitStatus>

    /**
     * Статус прогресса.
     */
    val progressStatus: StateFlow<T>

    /**
     * Проверить, требуется ли взаимодействие с пользователем для данного состояния.
     *
     * @param state состояние прогресса.
     */
    @MainThread
    fun isProgressStatusRequireUserInteraction(state: T): Boolean

    /**
     * Статус инициализации, терминальный.
     * Если необходимо расширить количество состоянии, то их моделируем как [progressStatus].
     */
    sealed interface InitStatus {
        object NotInitialized : InitStatus
        object InitCompleted: InitStatus
        class InitFailure(val errorMessage: String) : InitStatus
    }
}