package ru.tensor.sbis.entrypoint_guard.work

import android.app.Application
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import kotlinx.coroutines.coroutineScope
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import ru.tensor.sbis.entrypoint_guard.util.waitTerminalOrBlockingState

/**
 * Компонент, перехватывающий события [ListenableWorker].
 */
class WorkerAssistant internal constructor(
    private val applicationProvider: () -> Application?,
    private val appStateProvider: () -> AppInitStateHolder<*>
) {

    /**
     * Перехватить событие [CoroutineWorker.doWork].
     *
     * @param worker
     * @param onFailure колбэк в случае провала инициализации.
     * @param onReady колбэк в случае успешного завершения инициализации.
     */
    suspend fun <T> interceptDoWork(
        worker: T,
        onFailure: suspend (String?) -> ListenableWorker.Result = { it ->
            ListenableWorker.Result.failure(
                Data.Builder()
                    .putString(KEY_ERROR_REASON, it)
                    .build()
            )
        },
        onReady: suspend () -> ListenableWorker.Result
    ): ListenableWorker.Result where T : CoroutineWorker, T : EntryPointGuard.EntryPoint {
        return coroutineScope {
            if (applicationProvider() == null) {
                return@coroutineScope onReady()
            }

            val (initStatus, progressState) = waitTerminalOrBlockingState(
                appStateProvider()
            ).await()

            when (initStatus) {
                AppInitStateHolder.InitStatus.InitCompleted -> {
                    onReady()
                }

                is AppInitStateHolder.InitStatus.InitFailure -> {
                    onFailure(initStatus.errorMessage)
                }

                AppInitStateHolder.InitStatus.NotInitialized -> {
                    onFailure("progressState = $progressState")
                }
            }
        }
    }

    companion object {
        private const val KEY_ERROR_REASON = "error_reason"
    }

}