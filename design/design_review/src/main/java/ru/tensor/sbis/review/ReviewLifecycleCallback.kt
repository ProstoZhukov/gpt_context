package ru.tensor.sbis.review

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Коллбек жизненного цикла для отслеживания текущей Активити
 *
 * @author ma.kolpakov
 */
internal class ReviewLifecycleCallback : Application.ActivityLifecycleCallbacks, ActivityProvider {
    private var activity: WeakReference<Activity>? = null

    override fun activity() = activity?.get()

    override fun onActivityResumed(activity: Activity) {
        this.activity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (this.activity == activity) {
            this.activity = null
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
