package ru.tensor.sbis.base_saby_app

import android.app.Application
import ru.tensor.sbis.core_saby_app.CoreSabyApp
import ru.tensor.sbis.login.AuthPlugin
import ru.tensor.sbis.plugin_manager.PluginManager
import androidx.annotation.CallSuper

/**
 * Базовая конфигурация приложения.
 * Регистрирует плагины, которые используются практически в каждом приложении.
 *
 * @author kv.martyshenko
 */
open class BaseSabyApp : CoreSabyApp() {

    protected val authPlugin: AuthPlugin = AuthPlugin

    @CallSuper
    override fun registerPlugins(
        app: Application,
        pluginManager: PluginManager
    ) {
        pluginManager.registerPlugins(authPlugin)
        super.registerPlugins(app, pluginManager)
    }
}