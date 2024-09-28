package ru.tensor.sbis.entrypoint_guard.service

import android.app.Service
import android.content.Context
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Базовый класс [Service] с реализацией [EntryPointGuard.EntryPoint].
 *
 * @author kv.martyshenko
 */
abstract class EntryPointService : Service(), EntryPointGuard.EntryPoint {

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

    protected abstract fun onReady()

}