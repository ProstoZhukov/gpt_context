package ru.tensor.sbis.design.list_utils.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.design.utils.checkNotNullSafe
import timber.log.Timber
import java.lang.IllegalStateException

/**
 * Позволяет отслеживать событие перехода приложения в фон. Подсчёт запущенных Activity позволяет удостовериться, что
 * выполняется переход в фон именно приложения, а не только текущей Activity
 */
class AppBackgroundStateObserverDuringFragmentLifetime : Application.ActivityLifecycleCallbacks {

    private lateinit var fragment: Fragment
    private lateinit var onAppWentToBackgroundAction: () -> Unit

    private val currentActivity: FragmentActivity?
        get() = fragment.activity

    private var startedCount = 0

    private val destroyObserver = object : LifecycleObserver {
        @Suppress("unused")
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            unregisterActivityCallbacks()
        }
    }

    /**
     * Выполняет подписку на событие перехода приложения в фон.
     * Подписываться необходимо после вызова [Fragment.onCreateView]
     */
    fun observe(fragment: Fragment, onAppWentToBackgroundAction: () -> Unit) {
        this.fragment = fragment
        this.onAppWentToBackgroundAction = onAppWentToBackgroundAction
        startedCount = getMinimumStartedCount()

        registerActivityCallbacks()
        try {
            fragment.viewLifecycleOwner.lifecycle.addObserver(destroyObserver)
        } catch (e: IllegalStateException) {
            Timber.w(e, "You should start observing only after onCreateView() is called in fragment")
            fragment.lifecycle.addObserver(destroyObserver)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        startedCount = (startedCount - 1).coerceAtLeast(getMinimumStartedCount())
        if (startedCount == 0 && !activity.isChangingConfigurations) {
            onAppWentToBackgroundAction()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        startedCount++
    }

    private fun registerActivityCallbacks() {
        checkNotNullSafe(currentActivity) {
            "Cannot register callbacks: fragment ${fragment.javaClass} not attached to an activity"
        }?.let { activity ->
            (activity.applicationContext as Application).registerActivityLifecycleCallbacks(this)
        }
    }

    private fun unregisterActivityCallbacks() {
        checkNotNullSafe(currentActivity) {
            "Cannot unregister callbacks: fragment ${fragment.javaClass} not attached to an activity"
        }?.let { activity ->
            (activity.applicationContext as Application).unregisterActivityLifecycleCallbacks(this)
        }
    }

    private fun getMinimumStartedCount(): Int = if (isCurrentActivityStarted()) 1 else 0

    private fun isCurrentActivityStarted(): Boolean {
        return currentActivity?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED)
            ?: false
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // ignore
    }

    override fun onActivityDestroyed(activity: Activity) {
        // ignore
    }

    override fun onActivityPaused(activity: Activity) {
        // ignore
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // ignore
    }

    override fun onActivityResumed(activity: Activity) {
        // ignore
    }
}