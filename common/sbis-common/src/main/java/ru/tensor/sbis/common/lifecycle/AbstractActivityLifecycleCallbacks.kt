package ru.tensor.sbis.common.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Пустая реализация интерфейса, т.к. обычно нужна реализация не всех методов, а лишь пары
 */
abstract class AbstractActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {
        //ignore
    }

    override fun onActivityStarted(activity: Activity) {
        //ignore
    }

    override fun onActivityDestroyed(activity: Activity) {
        //ignore
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
        //ignore
    }

    override fun onActivityStopped(activity: Activity) {
        //ignore
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        //ignore
    }

    override fun onActivityResumed(activity: Activity) {
        //ignore
    }
}