package ru.tensor.sbis.base_components.activity.swipeback

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Если на уровне app delegate выставлена [SwipeBackContainerFactory],
 * будет учитываться стратегия активностей с данным интерфейсом.
 *
 * @author kv.martyshenko
 */
interface SwipeBackAware {
    /** Состояние свайпа на активности. */
    val strategy: Strategy

    /** @SelfDocumented */
    enum class Strategy {
        /** Включен. */
        ENABLED,

        /** Выключен. */
        DISABLED
    }
}

/** @SelfDocumented */
fun <T> T.findSwipeBackController(): SwipeBackController?
        where T : AppCompatActivity, T : SwipeBackAware {
    return ViewModelProvider(this)[SwipeBackControllerHolder::class.java].swipeBackController
}

/** @SelfDocumented */
internal fun <T> SwipeBackContainerFactory.keepUntilDestroy(
    swipeBackController: SwipeBackController,
    activity: T
) where T : ComponentActivity, T : SwipeBackAware {
    val holder = ViewModelProvider(activity)[SwipeBackControllerHolder::class.java]
    holder.swipeBackController = swipeBackController

    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            holder.swipeBackController = null
            owner.lifecycle.removeObserver(this)
            super.onDestroy(owner)
        }
    })
}

/** @SelfDocumented */
internal class SwipeBackControllerHolder : ViewModel() {
    /** @SelfDocumented */
    var swipeBackController: SwipeBackController? = null
}