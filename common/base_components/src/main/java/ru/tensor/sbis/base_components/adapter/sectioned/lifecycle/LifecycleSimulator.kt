package ru.tensor.sbis.base_components.adapter.sectioned.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.annotation.CallSuper

/**
 * Симулятор жизненного цикла на основе переданного экземпляра [Lifecycle].
 *
 * @author am.boldinov
 */
abstract class LifecycleSimulator(private val lifecycle: Lifecycle) : LifecycleObserver {

     init { initialize() }

    private fun initialize() { lifecycle.addObserver(this) }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected open fun onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected open fun onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected open fun onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected open fun onStop() {

    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun onDestroy() {
        lifecycle.removeObserver(this)
    }

}