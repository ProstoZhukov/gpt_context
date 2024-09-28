package ru.tensor.sbis.toolbox_decl.syncmanager

import android.accounts.Account
import android.os.Bundle

/**
 * Created by gs.raudiyaynen on 05.02.2018.
 */
interface SyncManager {

    fun requestSyncIfNotActive(account: Account, extras: Bundle)

    fun requestSync(syncType: SyncType)

    fun enableSync()

    fun disableSync()

}