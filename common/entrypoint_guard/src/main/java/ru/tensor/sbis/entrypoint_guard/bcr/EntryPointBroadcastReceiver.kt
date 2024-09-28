package ru.tensor.sbis.entrypoint_guard.bcr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard

/**
 * Базовый класс [BroadcastReceiver] с реализацией [EntryPointGuard.EntryPoint].
 *
 * @author kv.martyshenko
 */
abstract class EntryPointBroadcastReceiver : BroadcastReceiver(), EntryPointGuard.EntryPoint {

    final override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        EntryPointGuard.broadCastAssistant
            .interceptOnReceive(
                receiver = this,
                context,
                intent,
                onReady = ::onReady
            )
    }

    /**
     * Метод будет в случае успешной инифиализации приложения.
     */
    protected abstract fun onReady(context: Context, intent: Intent)

}