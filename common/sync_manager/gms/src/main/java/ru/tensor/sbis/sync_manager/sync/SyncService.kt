package ru.tensor.sbis.sync_manager.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.tensor.sbis.entrypoint_guard.service.EntryPointService

import ru.tensor.sbis.sync_manager.SyncManagerPlugin

/**
 * Сервис синхронизации
 *
 * @author kv.martyshenko
 */
class SyncService : EntryPointService() {
    private val syncAdapter by lazy {
        SyncAdapter(application).also { SyncManagerPlugin.syncAdapter = it }
    }

    override fun onReady() { }

    override fun onBind(intent: Intent): IBinder? {
        return syncAdapter.syncAdapterBinder
    }

}