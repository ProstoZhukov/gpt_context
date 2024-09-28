package ru.tensor.sbis.entrypoint_guard.service

import android.app.IntentService
import android.content.Context
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard


/**
 * Базовый класс [IntentService] с реализацией [EntryPointGuard.EntryPoint].
 *
 * @author ar.leschev
 */
abstract class EntryPointIntentService(threadName: String) : IntentService(threadName), EntryPointGuard.EntryPoint {

    final override fun attachBaseContext(newBase: Context?) {
        EntryPointGuard.serviceAssistant
            .interceptAttachBaseContext(
                service = this,
                newBase,
                superMethod = { super.attachBaseContext(it) }
            )
    }

    final override fun onCreate() {
        EntryPointGuard.serviceAssistant
            .interceptOnCreate(
                service = this,
                superMethod = { super.onCreate() },
                onReady = ::onReady
            )
    }

    /** Колбэк в случае успешного завершения инициализации. */
    protected abstract fun onReady()

}