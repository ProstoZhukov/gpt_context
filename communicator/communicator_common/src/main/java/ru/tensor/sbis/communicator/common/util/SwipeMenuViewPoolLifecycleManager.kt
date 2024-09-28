package ru.tensor.sbis.communicator.common.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool

/**
 * Отслеживает жизненный цикл view фрагмента, обеспечивая автоматическое заполнение в фоне и очистку [viewPool] при
 * создании и уничтожении view фрагмента соответственно
 *
 * @author rv.krohalev
 */
class SwipeMenuViewPoolLifecycleManager(
    private val viewPool: SwipeMenuViewPool
): LifecycleObserver {

    private val disposable = SerialDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        disposable.set(
            Completable.fromAction { viewPool.inflate() }
                .subscribeOn(Schedulers.computation())
                .subscribe()
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        viewPool.flush()
        disposable.dispose()
    }
}