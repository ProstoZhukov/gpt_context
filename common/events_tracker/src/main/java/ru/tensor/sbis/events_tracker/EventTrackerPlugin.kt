package ru.tensor.sbis.events_tracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.withContext
import ru.tensor.sbis.events_tracker.storage.ControllerPageStatisticStorage
import ru.tensor.sbis.events_tracker.storage.ControllerStatisticStorage
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.statistic.PageStatisticService
import ru.tensor.sbis.statistic.StatisticService
import ru.tensor.sbis.statistic.model.UserInfo
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent.EventType

/**
 * Плагин с утилитами логирования событий посредством FirebaseAnalytics
 *
 * @author as.chadov
 */
object EventTrackerPlugin : BasePlugin<Unit>() {
    private val coroutineScope = MainScope()
    @Volatile
    private var isStatisticStorageConfigured = false

    @Suppress("MemberVisibilityCanBePrivate")
    val eventsTracker: EventsTracker by lazy { EventsTracker(application) }

    private var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(EventsTracker::class.java) { eventsTracker }
    )

    override val dependency = Dependency.Builder()
        .optional(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        super.doAfterInitialize()

        configureStatisticService()
    }

    @Synchronized
    private fun configureStatisticService() {
        if (!isStatisticStorageConfigured) {
            with(StatisticService) {
                application.setupStorage(ControllerStatisticStorage())
            }
            with(PageStatisticService) {
                application.setupStorage(ControllerPageStatisticStorage())
            }
            isStatisticStorageConfigured = true
        }

        loginInterfaceProvider?.get()?.loginInterface?.let { loginInterface ->
            coroutineScope.launch(Dispatchers.IO) {
                loginInterface.userAccountObservable.asFlow()
                    .combine(loginInterface.eventsObservable.asFlow(), ::Pair)
                    .retry()
                    .collect { (account, authEvent) ->
                        val userInfo = if (authEvent.eventType == EventType.LOGOUT) {
                            null
                        } else {
                            UserInfo(account.userId.toLong(), account.clientId.toLong())
                        }
                        withContext(Dispatchers.Main.immediate) {
                            with(StatisticService) { application.setUser(userInfo) }
                        }
                    }
            }
        }
    }
}