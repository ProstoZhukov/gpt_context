package ru.tensor.sbis.user_activity_track_watcher.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.AnyThread
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable
import ru.tensor.sbis.user_activity_track.service.UserActivityService
import timber.log.Timber

/**
 * Класс, ответственный за настройку автоматического мониторинга активности пользователя.
 * Анализирует экраны [Activity], помеченные интерфейсом [UserActivityTrackable], на основе чего управляет мониторингом.
 *
 * @author kv.martyshenko
 */
object UserActivityTrackingWatcher {

    /**
     * Метод для настройки автоматического мониторинга активности пользователя на всех экранах [Activity],
     * помеченных интерфейсом [UserActivityTrackable].
     *
     * @param application
     * @param userActivityService сервис трекинга активности
     * @param loginInterface экземпляр [LoginInterface]
     * @param fallbackAction действие, которое будет вызвано при обнаружении экрана, не реализующего [UserActivityTrackable]
     */
    @AnyThread
    @JvmStatic
    @JvmOverloads
    fun monitorAppScreensAutomatically(application: Application,
                                       userActivityService: UserActivityService,
                                       loginInterface: LoginInterface,
                                       fallbackAction: (Activity) -> Unit = ::handleNonUserActivityTrackingScreen): Disposable {
        val screenTracker = UserActivityScreenTracker(userActivityService, fallbackAction)

        return loginInterface
            .eventsObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ event ->
                           when (requireNotNull(event.eventType)) {
                               AuthEvent.EventType.LOGIN      -> {
                                   application.registerActivityLifecycleCallbacks(screenTracker)
                               }
                               AuthEvent.EventType.LOGOUT     -> {
                                   application.unregisterActivityLifecycleCallbacks(screenTracker)
                                   userActivityService.reset()
                               }
                               AuthEvent.EventType.AUTHORIZED -> {
                                   application.registerActivityLifecycleCallbacks(screenTracker)
                               }
                           }.exhaustive
                       }, Timber::w)
    }

    private class UserActivityScreenTracker(
        private val userActivityService: UserActivityService,
        private val fallbackAction: (Activity) -> Unit = ::handleNonUserActivityTrackingScreen
    ) : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            if (activity !is UserActivityTrackable) {
                userActivityService.startPeriodicallyRegisterActivity(activity::class.java.name)
                fallbackAction(activity)
            } else {
                executeTrackingActionIfConditionSatisfied(activity) {
                    userActivityService.startPeriodicallyRegisterActivity(it.screenName)
                }
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (activity !is UserActivityTrackable) {
                userActivityService.stopPeriodicallyRegisterActivity(activity::class.java.name)
            } else {
                executeTrackingActionIfConditionSatisfied(activity) {
                    userActivityService.stopPeriodicallyRegisterActivity(it.screenName)
                }
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}

        private inline fun <T> executeTrackingActionIfConditionSatisfied(screen: T, crossinline action: (UserActivityTrackable) -> Unit)
                where T : Activity, T: UserActivityTrackable {
            if(screen.isTrackActivityEnabled) {
                action(screen)
            }
        }
    }

}

private fun handleNonUserActivityTrackingScreen(activity: Activity) {
    if(activity.isTensorScreen()) {
        Timber.w("""
            ${activity::class.java.canonicalName} should implement ${UserActivityTrackable::class.java}.
            Application can work incorrectly!
        """.trimIndent())
    }
}

private fun Activity.isTensorScreen(): Boolean {
    return this::class.java.name.contains("ru.tensor.sbis")
}

private val <T> T.exhaustive: T
    get() = this