package ru.tensor.sbis.sync_manager

import android.app.Application
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.ModuleSyncAdapter
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.sync_manager.sync.SyncAdapter
import ru.tensor.sbis.sync_manager.sync.SyncManagerImpl

/**
 * Плагин для синхронизации через встроенные гугл-сервисы
 *
 * @author kv.martyshenko
 */
object SyncManagerPlugin : BasePlugin<Unit>() {
    private val syncManager by lazy {
        SyncManagerImpl(application)
    }
    internal var syncAdapter: SyncAdapter? = null

    internal lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private var syncAdapters: Set<FeatureProvider<ModuleSyncAdapter>>? = null

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .optionalSet(ModuleSyncAdapter::class.java) { syncAdapters = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        if(!syncAdapters.isNullOrEmpty()) {
            syncAdapters!!.forEach {
                syncAdapter?.registerModuleSyncAdapter(it.get())
            }

            subscribeOnAuthEvent()
        }
    }

    private fun subscribeOnAuthEvent(): Disposable {
        return loginInterfaceProvider.get().loginInterface.eventsObservable
            .subscribe { authEvent ->
                when (authEvent.eventType) {
                    AuthEvent.EventType.LOGIN      -> {
                        syncManager.enableSync()
                    }
                    AuthEvent.EventType.LOGOUT     -> {
                        syncManager.disableSync()
                    }
                    AuthEvent.EventType.AUTHORIZED -> {
                        syncManager.enableSync()
                    }
                    else                           -> {
                    }
                }
            }
    }

}