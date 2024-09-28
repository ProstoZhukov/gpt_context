package ru.tensor.sbis.logging.screen_tracker

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.logging.screen_tracker.log_providers.ScreenTrackerLogProvider
import timber.log.Timber

/**
 * Автоматический логгер открытия всех активити и фрагментов.
 *
 * @param context см. [Context]
 * @property logProviders список продайдеров логов. Строки, полученные от всех провайдеров, конкатенируются и логируются одной строкой.
 *
 * @author av.krymov
 */
internal class ScreenTracker(context: Context, private val logProviders: List<ScreenTrackerLogProvider>) {

    private val activityLifecycleListener = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            logScreenAction(activity, "created")
            listenForFragments(activity)
        }

        override fun onActivityStarted(activity: Activity) =
            logScreenAction(activity, "started", Log.INFO)

        override fun onActivityResumed(activity: Activity) =
            logScreenAction(activity, "resumed")

        override fun onActivityPaused(activity: Activity) =
            logScreenAction(activity, "paused")

        override fun onActivityStopped(activity: Activity) =
            logScreenAction(activity, "stopped", Log.INFO)

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
            logScreenAction(activity, "saveInstanceState")

        override fun onActivityDestroyed(activity: Activity) =
            logScreenAction(activity, "destroyed")
    }

    private val fragmentLifecycleListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, fragment: Fragment, context: Context) =
            logScreenAction(fragment, "attached")

        override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) =
            logScreenAction(fragment, "created")

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            fragment: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) =
            logScreenAction(fragment, "viewCreated")

        override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "started", Log.INFO)

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "resumed")

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "paused")

        override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "stopped", Log.INFO)

        override fun onFragmentSaveInstanceState(fm: FragmentManager, fragment: Fragment, outState: Bundle) =
            logScreenAction(fragment, "saveInstanceState")

        override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "viewDestroyed")

        override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "destroyed")

        override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) =
            logScreenAction(fragment, "detached")
    }

    init {
        val application = context.applicationContext as Application
        application.registerActivityLifecycleCallbacks(activityLifecycleListener)
    }

    private fun listenForFragments(activity: Activity) {
        val compatActivity = activity as? AppCompatActivity ?: return
        compatActivity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleListener, true)
    }

    private fun logScreenAction(screen: Any, action: String, logLevel: Int = Log.DEBUG) {
        val message = logProviders.joinToString(" ") { it.getLogMessage(screen, action) }
        when (logLevel) {
            Log.INFO -> Timber.i(message)
            else -> Timber.d(message)
        }
    }
}
