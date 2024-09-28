package ru.tensor.sbis.core_saby_app

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper
import ru.tensor.sbis.application_tools.AppToolsPlugin
import ru.tensor.sbis.common.CommonUtilsPlugin
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.change_theme.util.createThemeAppContext
import ru.tensor.sbis.events_tracker.EventTrackerPlugin
import ru.tensor.sbis.logging.LoggingPlugin
import ru.tensor.sbis.login.common.AuthCommonPlugin
import ru.tensor.sbis.network_native.NetworkPlugin
import ru.tensor.sbis.onboarding.OnboardingPlugin
import ru.tensor.sbis.permission.PermissionPlugin
import ru.tensor.sbis.plugin_manager.PluginManager
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Базовая конфигурация приложения.
 * Регистрирует плагины, которые используются практически в каждом приложении.
 *
 * @author kv.martyshenko
 */
open class CoreSabyApp {
    protected val appToolsPlugin: AppToolsPlugin = AppToolsPlugin
    protected val commonUtilsPlugin: CommonUtilsPlugin = CommonUtilsPlugin
    protected val eventTrackerPlugin: EventTrackerPlugin = EventTrackerPlugin
    protected val loggingPlugin: LoggingPlugin = LoggingPlugin
    protected val permissionPlugin: PermissionPlugin = PermissionPlugin
    protected val onboardingPlugin: OnboardingPlugin = OnboardingPlugin
    protected val authCommonPlugin: AuthCommonPlugin = AuthCommonPlugin
    protected val networkPlugin: NetworkPlugin = NetworkPlugin

    /** Регистрировать ли плагин онбординга. */
    protected var isOnboardingEnable = true

    @CallSuper
    protected open fun registerPlugins(
        app: Application,
        pluginManager: PluginManager
    ) {
        pluginManager.registerPlugins(
            appToolsPlugin,
            commonUtilsPlugin,
            eventTrackerPlugin,
            loggingPlugin,
            permissionPlugin,
            authCommonPlugin,
            networkPlugin
        )
        if (isOnboardingEnable) {
            pluginManager.registerPlugin(onboardingPlugin)
        }
    }

    /**
     * Метод для инициализации плагинной системы полностью.
     *
     * @param themedContext - темизированный [Application.getApplicationContext]
     */
    @JvmOverloads
    fun initialize(
        app: Application,
        pluginManager: PluginManager = PluginManager(),
        themedContext: Context = app.createThemeAppContext(R.style.DefaultLightTheme, R.style.BaseAppTheme)
    ) {
        registerPlugins(app, pluginManager)
        pluginManager.configure(
            app,
            SbisThemedContext(themedContext)
        )
    }

    /**
     * Метод для инциализации плагинной системы.
     *
     * @param themedContext - темизированный [Application.getApplicationContext]
     */
    fun doInit(
        app: Application,
        pluginManager: PluginManager,
        themedContext: Context = app.createThemeAppContext(
            R.style.DefaultLightTheme,
            R.style.BaseAppTheme
        )
    ) {
        registerPlugins(app, pluginManager)
        pluginManager.doInitStage(app, SbisThemedContext(themedContext))
    }

    /**
     * Метод для нотификации плагинов, после инициализации плагинной системы.
     */
    fun doAfterInit(pluginManager: PluginManager) {
        pluginManager.doAfterInitStage()
    }
}
