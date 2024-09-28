package ru.tensor.sbis.user_activity_track_watcher

import android.app.Application
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.user_activity_track.service.UserActivityService
import ru.tensor.sbis.user_activity_track_watcher.activity.UserActivityTrackingWatcher
import ru.tensor.sbis.user_activity_track_watcher.service.DefaultUserActivityService

/**
 * Плагин для фиксации активности пользователя
 *
 * @author kv.martyshenko
 */
object UserActivityTrackWatcherPlugin : BasePlugin<UserActivityTrackWatcherPlugin.CustomizationOptions>() {
    private val userActivityService: UserActivityService by lazy {
        DefaultUserActivityService(application)
    }

    private var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(UserActivityService::class.java) { userActivityService }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .apply {
                if(customizationOptions.isAutoTrackEnabled) {
                    require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
                }
            }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun doAfterInitialize() {
        if(customizationOptions.isAutoTrackEnabled) {
            UserActivityTrackingWatcher.monitorAppScreensAutomatically(
                application,
                userActivityService,
                loginInterfaceProvider!!.get().loginInterface
            )
        }
    }

    class CustomizationOptions internal constructor() {
        /**
         * Включено ли автоматическое отслеживание активности пользователя
         */
        var isAutoTrackEnabled: Boolean = true
    }
}