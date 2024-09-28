package ru.tensor.sbis.widget_player.layout.internal

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.findViewTreeLifecycleOwner

/**
 * @author am.boldinov
 */
internal class WidgetViewLifecycleOwner : LifecycleOwner {

    private val rootLifecycleObserver = object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            onPause(owner)
            if (lifecycle.currentState == Lifecycle.State.STARTED) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            }
            if (lifecycle.currentState == Lifecycle.State.CREATED) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            }
            owner.lifecycle.removeObserver(this)
        }

        override fun onPause(owner: LifecycleOwner) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            if (lifecycle.currentState == Lifecycle.State.STARTED) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
        }
    }

    override val lifecycle = LifecycleRegistry(this)

    fun dispatchAttachedToPlayer(view: View) {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        view.findViewTreeLifecycleOwner()?.let { owner ->
            owner.lifecycle.addObserver(rootLifecycleObserver)
            if (owner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }
        }
    }

    fun dispatchDetachedFromPlayer(view: View) {
        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (lifecycle.currentState == Lifecycle.State.STARTED) {
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    fun dispatchWidgetInitialize(root: Lifecycle) {
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    fun dispatchWidgetDestroy() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }
}