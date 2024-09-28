package ru.tensor.sbis.common

import android.accounts.Account
import android.content.ContentProviderClient
import android.content.SyncResult
import android.os.Bundle
import ru.tensor.sbis.toolbox_decl.syncmanager.SyncType
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Created by gs.raudiyaynen on 05.02.2018.
 */
interface ModuleSyncAdapter : Feature {

    fun performSync(account: Account,
                    extras: Bundle,
                    authority: String,
                    provider: ContentProviderClient,
                    syncResult: SyncResult)

    fun getType(): SyncType
}