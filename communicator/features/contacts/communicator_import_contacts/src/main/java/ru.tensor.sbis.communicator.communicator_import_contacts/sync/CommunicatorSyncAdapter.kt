package ru.tensor.sbis.communicator.communicator_import_contacts.sync

import android.accounts.Account
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.content.pm.PackageManager
import android.os.Bundle
import ru.tensor.sbis.common.ModuleSyncAdapter
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communicator.communicator_import_contacts.ImportContactsPlugin.getImportContactsSingletoneComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.toolbox_decl.syncmanager.SyncType
import ru.tensor.sbis.frescoutils.FrescoCache
import javax.inject.Inject

/**
 * SyncAdapter для импорта контактов с устройства
 *
 * @author rv.krohalev
 */
@Suppress("unused")
class CommunicatorSyncAdapter(val context: Context) : ModuleSyncAdapter {

    private lateinit var networkUtils: NetworkUtils

    private lateinit var importContactsHelper: ImportContactsHelper

    private lateinit var loginInterface: LoginInterface

    init {
        getImportContactsSingletoneComponent().inject(this)
    }

    /** @SelfDocumented */
    @Inject
    internal fun setNetworkUtils(networkUtils: NetworkUtils) {
        this.networkUtils = networkUtils
    }

    /** @SelfDocumented */
    @Inject
    internal fun setImportContactsHelper(importContactsHelper: ImportContactsHelper) {
        this.importContactsHelper = importContactsHelper
    }

    /** @SelfDocumented */
    @Inject
    internal fun setLoginInterface(loginInterface: LoginInterface) {
        this.loginInterface = loginInterface
    }

    /** @SelfDocumented */
    override fun performSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
        updateCaches()

        val permission = android.Manifest.permission.READ_CONTACTS
        if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            importContactsHelper.importContactsUnsafe()
        }
    }

    /** @SelfDocumented */
    override fun getType(): SyncType = SyncType.COMMUNICATOR

    private fun updateCaches() {
        loginInterface.getCurrentAccount()?.let(FrescoCache::evictCurrentAccountPhoto)
    }

}