package ru.tensor.sbis.entrypoint_guard.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import timber.log.Timber

/**
 * Метод для получения первого блокирующего прогресса или терминального состояния.
 *
 * @param appInitStateHolder
 * @param progressHandler
 *
 * @author kv.martyshenko
 */
internal fun <T> CoroutineScope.waitTerminalOrBlockingState(
    appInitStateHolder: AppInitStateHolder<T>
): Deferred<Pair<AppInitStateHolder.InitStatus, T>> {
    return async(Dispatchers.IO) {
        withContext(Dispatchers.Default) {
            appInitStateHolder.initStatus.combine(appInitStateHolder.progressStatus, ::Pair)
                .distinctUntilChanged()
                .first { (state, progress) ->
                    state != AppInitStateHolder.InitStatus.NotInitialized ||
                            appInitStateHolder.isProgressStatusRequireUserInteraction(progress)
                }
        }
    }
}

/**
 * Метод для получения терминального состояния.
 */
private fun <T> CoroutineScope.waitTerminalInitState(
    appInitStateHolder: AppInitStateHolder<T>
): Deferred<AppInitStateHolder.InitStatus> {
    return async(Dispatchers.IO) {
        withContext(Dispatchers.Default) {
            appInitStateHolder.initStatus
                .first { state ->
                    state != AppInitStateHolder.InitStatus.NotInitialized
                }
        }
    }
}


/**
 * Обёрка с обработкой ошибок для [waitTerminalOrBlockingState].
 */
internal fun <T> AppInitStateHolder<T>.waitTerminalOrBlockingState() =
    try {
        runBlocking {
            Result.success(waitTerminalOrBlockingState(this@waitTerminalOrBlockingState).await())
        }
    } catch (e: Exception) {
        Timber.i(e)
        Result.failure(e)
    }

/**
 * Обертка с обработкой ошибок для [waitTerminalInitState].
 */
internal fun <T> AppInitStateHolder<T>.waitTerminalState() =
    try {
        runBlocking {
            Result.success(waitTerminalInitState(this@waitTerminalState).await())
        }
    } catch (e: Exception) {
        Timber.i(e)
        Result.failure(e)
    }