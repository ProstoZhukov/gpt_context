package ru.tensor.sbis.entrypoint_guard.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import timber.log.Timber

/**
 * Оповещает о неподдержанных активностях в МП.
 *
 * @author ar.leschev
 */
internal class LegacyDetectorCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val activityNameWithPackage = activity::class.java.canonicalName ?: return
        if (!activityNameWithPackage.startsWith("ru.tensor")) return

        if (activity is EntryPointGuard.LegacyEntryPoint) {
            Timber.i("${activity.javaClass.name} реализует устаревшую обработку входных точек. Необходим переход на EntryPoint.")
            return
        }
        if (activity !is EntryPointGuard.EntryPoint) {
            Timber.e("${activity.javaClass.name} не поддерживает работу с асинхронной инициализацией. Необходим переход на EntryPoint.")
            return
        }
    }

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}