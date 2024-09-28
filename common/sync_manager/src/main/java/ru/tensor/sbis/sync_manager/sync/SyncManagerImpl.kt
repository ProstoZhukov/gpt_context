package ru.tensor.sbis.sync_manager.sync

import android.accounts.Account
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import ru.tensor.sbis.toolbox_decl.syncmanager.SyncManager
import ru.tensor.sbis.toolbox_decl.syncmanager.SyncType
import ru.tensor.sbis.verification_decl.BuildConfig
import ru.tensor.sbis.sync_manager.SyncManagerPlugin
import timber.log.Timber

private const val SECONDS_IN_DAY: Long = 86400
private const val SYNC_PERIOD_IN_SECONDS = SECONDS_IN_DAY / 4

const val AUTHORITY = BuildConfig.CONTENT_AUTHORITY

/**
 * Менеджер синхронизации
 *
 * @author kv.martyshenko
 */
internal class SyncManagerImpl(val context: Context) : SyncManager {

    override fun requestSyncIfNotActive(account: Account, extras: Bundle) {
        if (!ContentResolver.isSyncPending(account, AUTHORITY)) {
            ContentResolver.requestSync(account, AUTHORITY, extras)
        }
    }

    override fun requestSync(syncType: SyncType) {
        try {
            val settingsBundle = Bundle()
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            settingsBundle.putBoolean(EXTRA_SYNC_AUTH_PARAMS_SET, true)
            settingsBundle.putSerializable(EXTRA_SYNC_TYPE_SET, syncType)

            val account = loadCurrentAccount()
            if (account != null) {
                requestSyncIfNotActive(account, settingsBundle)
            }

        } catch (ex: Exception) {
            Timber.e(ex, "Failed to start sync adapter manually")
        }
    }

    override fun enableSync() {
        val userAccount = loadCurrentAccount()
        userAccount?.let {
            ContentResolver.setSyncAutomatically(it, AUTHORITY, true)
            ContentResolver.addPeriodicSync(it, AUTHORITY, Bundle.EMPTY, SYNC_PERIOD_IN_SECONDS)
        }
    }

    override fun disableSync() {
        val userAccount = loadCurrentAccount()
        userAccount?.let {
            ContentResolver.cancelSync(it, AUTHORITY)
            ContentResolver.removePeriodicSync(it, AUTHORITY, Bundle.EMPTY)
        }
    }

    private fun loadCurrentAccount(): Account? {
        return SyncManagerPlugin.loginInterfaceProvider.get()
            .loginInterface
            .getCurrentAccount()
    }

}