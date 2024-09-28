package ru.tensor.sbis.main_screen.widget

import android.annotation.SuppressLint
import ru.tensor.sbis.main_screen.widget.storage.MainScreenStorage
import ru.tensor.sbis.main_screen.widget.storage.NavigationItemStorage
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.apptools.AutotestsParametersProvider
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenProvider
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.AuthEventsObservableProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider
import timber.log.Timber

/**
 * Плагин компонента "Главного" экрана.
 *
 * @author kv.martyshenko
 */
object MainScreenPlugin : BasePlugin<Unit>() {
    private lateinit var eventsTrackerProvider: FeatureProvider<ru.tensor.sbis.events_tracker.EventsTracker>
    internal var navigationServiceProvider: FeatureProvider<NavigationService>? = null
    internal var tabsVisibilityController: FeatureProvider<ToolbarTabsController>? = null
    internal var loginInterfaceProvider: FeatureProvider<LoginInterface>? = null
    internal val screenEntries: MutableSet<FeatureProvider<MainScreenEntry>> = mutableSetOf()
    internal val startupPermissions: MutableSet<FeatureProvider<StartupPermissionProvider>> = mutableSetOf()
    internal var autotestsParametersProvider: FeatureProvider<AutotestsParametersProvider>? = null
        private set
    internal var dashboardScreenProvider: FeatureProvider<DashboardScreenProvider>? = null
        private set
    private var authEventsObservableProvider: FeatureProvider<AuthEventsObservableProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .require(ru.tensor.sbis.events_tracker.EventsTracker::class.java) { eventsTrackerProvider = it }
        .optional(LoginInterface::class.java) { loginInterfaceProvider = it }
        .optional(AuthEventsObservableProvider::class.java) { authEventsObservableProvider = it }
        .optional(NavigationService::class.java) { navigationServiceProvider = it }
        .optional(ToolbarTabsController::class.java) { tabsVisibilityController = it }
        .optional(AutotestsParametersProvider::class.java) { autotestsParametersProvider = it }
        .optional(DashboardScreenProvider::class.java) { dashboardScreenProvider = it }
        .optionalSet(MainScreenEntry::class.java) { if (it != null) screenEntries.addAll(it) }
        .optionalSet(StartupPermissionProvider::class.java) { if (it != null) startupPermissions.addAll(it) }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        listenAuthEvents()
    }

    @SuppressLint("CheckResult")
    private fun listenAuthEvents() {
        authEventsObservableProvider?.get()?.let { authStreamSource ->
            val storage = MainScreenStorage(application)
            authStreamSource.eventsObservable
                .subscribe(
                    { authEvent ->
                        if (authEvent.eventType == AuthEvent.EventType.LOGOUT) {
                            storage.reset()
                            // При смене пользователя данные о доступных вкладках могут стать неактуальными
                            tabsVisibilityController?.get()?.updateTabs(emptySet())
                            NavigationItemStorage.clear()
                        }
                    },
                    Timber::w
                )
        }
    }
}