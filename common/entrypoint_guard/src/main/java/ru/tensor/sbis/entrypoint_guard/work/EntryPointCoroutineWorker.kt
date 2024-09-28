package ru.tensor.sbis.entrypoint_guard.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Базовый класс для [CoroutineWorker] с реализацией [EntryPointGuard.EntryPoint].
 *
 * @author kv.martyshenko
 */
abstract class EntryPointCoroutineWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params),
    EntryPointGuard.EntryPoint {

    final override suspend fun doWork(): Result {
        return EntryPointGuard.workerAssistant.interceptDoWork(
            worker = this,
            onReady = ::onReady
        )
    }

    /**
     * Метод будет в случае успешной инифиализации приложения.
     */
    protected abstract suspend fun onReady(): Result
}