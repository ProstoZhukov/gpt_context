package ru.tensor.sbis.entrypoint_guard.util

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

/**
 * Внутренний [ActivityLifecycleCallbacks] для удержания ссылки на текущую активность.
 */
internal class InitActivityHolderCallback : ActivityLifecycleCallbacks {

    private var ref = WeakReference<AppCompatActivity?>(null)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ref = WeakReference(activity as? AppCompatActivity)
    }

    override fun onActivityResumed(activity: Activity) {
        ref = WeakReference(activity as? AppCompatActivity)
    }

    override fun onActivityStarted(activity: Activity) {
        ref = WeakReference(activity as? AppCompatActivity)
    }

    override fun onActivityDestroyed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    /** Получить текущую активность. */
    fun get(): AppCompatActivity? = ref.get()

    /** Очистить ресурсы. */
    fun clear() {
        ref.clear()
    }
}