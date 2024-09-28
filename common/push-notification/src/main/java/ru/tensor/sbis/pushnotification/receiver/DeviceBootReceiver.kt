package ru.tensor.sbis.pushnotification.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import ru.tensor.sbis.pushnotification.service.KeepAliveForegroundService
import ru.tensor.sbis.pushnotification.util.PushLogger.event

/**
 * Приемник сигнала о загрузке устройства. Обновляет состояние [KeepAliveForegroundService].
 *
 * @author am.boldinov
 */
class DeviceBootReceiver : EntryPointBroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReady(context: Context, intent: Intent) {
        event("Device boot completed. Update stable service state...")
        KeepAliveForegroundService.startIfNeed(context)
    }
}