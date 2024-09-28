package ru.tensor.sbis.entrypoint_guard.service

import android.app.Application
import android.app.Service
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import ru.tensor.sbis.entrypoint_guard.BaseContextPatcher
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder
import ru.tensor.sbis.entrypoint_guard.util.waitTerminalOrBlockingState
import timber.log.Timber

class ServiceAssistant internal constructor(
    private val coroutineScope: CoroutineScope,
    private val applicationProvider: () -> Application?,
    private val appStateProvider: () -> AppInitStateHolder<*>,
    private val baseContextPatcherProvider: () -> BaseContextPatcher
) {

    fun <T> interceptAttachBaseContext(
        service: T,
        newBase: Context?,
        superMethod: (Context?) -> Unit
    ) where T : Service, T : EntryPointGuard.EntryPoint {
        if (applicationProvider() == null) {
            superMethod(newBase)
            return
        }
        superMethod(baseContextPatcherProvider().invoke(newBase))
    }

    fun <T> interceptOnCreate(
        service: T,
        superMethod: () -> Unit,
        onFailure: (T, error: String) -> Unit = { s, error ->
            Timber.w("${s::class.java.simpleName}#onCreate called, but init failed with $error")
        },
        onReady: () -> Unit
    ) where T : Service, T : EntryPointGuard.EntryPoint {
        if (applicationProvider() == null) {
            superMethod()
            onReady()
            return
        }

        val (initStatus, progressState) = appStateProvider().waitTerminalOrBlockingState()
            .getOrElse { return }

        superMethod()
        when (initStatus) {
            AppInitStateHolder.InitStatus.InitCompleted -> {
                onReady()
            }

            is AppInitStateHolder.InitStatus.InitFailure -> {
                onFailure(service, initStatus.errorMessage)
                service.stopSelf()
            }

            AppInitStateHolder.InitStatus.NotInitialized -> {
                onFailure(service, "progressState = $progressState")
                service.stopSelf()
            }
        }
    }

}