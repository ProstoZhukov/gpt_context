package ru.tensor.sbis.entrypoint_guard.bcr

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import ru.tensor.sbis.entrypoint_guard.util.waitTerminalOrBlockingState
import timber.log.Timber

/**
 * Перехватчик событий [BroadcastReceiver].
 */
class BroadCastAssistant internal constructor(
    private val coroutineScope: CoroutineScope,
    private val applicationProvider: () -> Application?,
    private val appStateProvider: () -> AppInitStateHolder<*>
) {

    /**
     * Перехватить событие [BroadcastReceiver.onReceive].
     *
     * @param receiver
     * @param context
     * @param intent
     * @param onFailure колбэк в случае провала инициализации.
     * @param onReady колбэк в случае успешного завершения инициализации.
     */
    fun <T> interceptOnReceive(
        receiver: T,
        context: Context,
        intent: Intent,
        onFailure: (T, Context, error: String) -> Unit = { r, _, error ->
            Timber.w("Started ${r::class.java.simpleName}. Failed with init error: $error")
        },
        onReady: (Context, Intent) -> Unit
    ) where T : BroadcastReceiver, T : EntryPointGuard.EntryPoint {
        if (applicationProvider() == null) {
            onReady(context, intent)
            return
        }

        val (initStatus, progressState) = appStateProvider().waitTerminalOrBlockingState()
            .getOrElse { return }

        when (initStatus) {
            AppInitStateHolder.InitStatus.InitCompleted -> {
                onReady(context, intent)
            }

            is AppInitStateHolder.InitStatus.InitFailure -> {
                onFailure(receiver, context, initStatus.errorMessage)
            }

            AppInitStateHolder.InitStatus.NotInitialized -> {
                onFailure(receiver, context, "progressState = $progressState")
            }
        }
    }

}