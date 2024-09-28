package ru.tensor.sbis.sync_manager.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.ModuleSyncAdapter
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.toolbox_decl.syncmanager.SyncType
import ru.tensor.sbis.sync_manager.SyncManagerPlugin
import timber.log.Timber
import java.util.*

const val EXTRA_SYNC_AUTH_PARAMS_SET = "EXTRA_SYNC_AUTH_PARAMS_SET"
const val EXTRA_SYNC_TYPE_SET = "EXTRA_SYNC_TYPE_SET"

/**
 * Адаптер синхронизации
 *
 * @author kv.martyshenko
 */
internal class SyncAdapter(context: Context) : AbstractThreadedSyncAdapter(context, true) {

    private val loginInterface: LoginInterface by lazy {
        SyncManagerPlugin.loginInterfaceProvider.get().loginInterface
    }

    /**
     * Map of registered module sync adapters.
     */
    private val moduleSyncAdapters = HashMap<SyncType, ModuleSyncAdapter>()

    fun registerModuleSyncAdapter(vararg moduleSyncAdapters: ModuleSyncAdapter) {
        moduleSyncAdapters.forEach {
            this.moduleSyncAdapters[it.getType()] = it
        }
    }

    override fun onPerformSync(
        account: Account,
        extras: Bundle,
        authority: String,
        provider: ContentProviderClient,
        syncResult: SyncResult
    ) {
        if (loginInterface.isAuthorized) {
            if (extras.containsKey(EXTRA_SYNC_TYPE_SET)) {
                val type = extras.getSerializable(EXTRA_SYNC_TYPE_SET) as SyncType
                val moduleSyncAdapter = moduleSyncAdapters[type]
                moduleSyncAdapter?.performSync(account, extras, authority, provider, syncResult)
            } else {
                Observable.fromIterable(moduleSyncAdapters.values)
                    .flatMap { moduleSyncAdapter ->
                        Observable.just(moduleSyncAdapter)
                            .doOnNext { moduleAdapter ->
                                moduleAdapter.performSync(account, extras, authority, provider, syncResult)
                            }
                            .onErrorResumeNext { error: Throwable ->
                                Timber.w(error, "Failed to sync adapter %s", moduleSyncAdapter.getType())
                                Observable.empty()
                            }
                            .subscribeOn(Schedulers.io())
                    }
                    .blockingSubscribe(Functions.emptyConsumer()) { error ->
                        Timber.w(error, "Failed to sync module adapters")
                    }
            }
        } else {
            syncResult.stats.numAuthExceptions++
        }
    }

}