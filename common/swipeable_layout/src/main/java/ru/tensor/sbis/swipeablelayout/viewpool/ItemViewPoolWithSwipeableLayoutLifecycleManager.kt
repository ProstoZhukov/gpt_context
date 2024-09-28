package ru.tensor.sbis.swipeablelayout.viewpool

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Отслеживает жизненный цикл view фрагмента, обеспечивая автоматическое заполнение в фоне и очистку [viewPool] при
 * создании и уничтожении view фрагмента соответственно
 *
 * @author us.bessonov
 */
class ItemViewPoolWithSwipeableLayoutLifecycleManager(
    private val viewPool: ItemViewPoolWithSwipeableLayout
) : LifecycleObserver {

    private val disposable = SerialDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        disposable.set(Completable.fromAction { viewPool.ensureRequiredViewCountInflated() }
            .subscribeOn(Schedulers.computation()).subscribe())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        viewPool.flush()
        disposable.dispose()
    }
}