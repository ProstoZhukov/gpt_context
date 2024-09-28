package ru.tensor.sbis.common.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.AnyThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Трекер состояния приложения.
 * Позволяет получать состояния нахождения в foreground или background.
 *
 * @author ar.leschev
 */
object AppLifecycleTracker {
    val appForegroundStateFlow: StateFlow<Boolean>

    private val appForegroundMutableStateFlow = MutableStateFlow(isAppInForeground)
    private var numStarted = 0
    private var numCreated = 0

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        appForegroundStateFlow = appForegroundMutableStateFlow
            .onStart { checkAppIsForeground() }
            .stateIn(scope, SharingStarted.WhileSubscribed(), isAppInForeground)
    }

    /**
     * Подписаться на колбеки активностей у [application].
     * Стоит вызывать как можно раньше, т.к активность может уже стартовать в случаях с асинхронным стартом.
     * Без этого вызова ничего не будет работать.
     */
    @AnyThread
    fun start(application: Application) =
        application.registerActivityLifecycleCallbacks(ForegroundListener())

    /**
     * Подписаться на события выхода в foreground или background.
     */
    @Deprecated("Использовать appForegroundStateFlow напрямую")
    fun subscribeOnAppForegroundEvents(): SharedFlow<Boolean> =
        appForegroundStateFlow

    /**
     * Находится ли приложение сейчас на экране.
     */
    @JvmStatic
    val isAppInForeground: Boolean
        get() = numStarted > 0

    /**
     * Есть ли созданные активности.
     */
    fun hasCreatedTasks(): Boolean =
        numCreated > 0


    private fun checkAppIsForeground() {
        scope.launch {
            appForegroundMutableStateFlow.emit(isAppInForeground)
        }
    }

    private class ForegroundListener : AbstractActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            numCreated++
        }

        override fun onActivityStarted(activity: Activity) {
            numStarted++
            if (numStarted == 1) {
                // Приложение перешло в foreground.
                appForegroundMutableStateFlow.tryEmit(true)
            }
        }

        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0 && !activity.isChangingConfigurations) {
                // Приложение перешло в background.
                appForegroundMutableStateFlow.tryEmit(false)
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
            numCreated--
        }
    }
}