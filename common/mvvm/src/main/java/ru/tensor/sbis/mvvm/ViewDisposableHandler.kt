package ru.tensor.sbis.mvvm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.LinkedList

/** Обработчик подписок в привязке к жизненному циклу вью [LifecycleOwner] */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ViewDisposableHandler(private val lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    private val disposableFactories: LinkedList<() -> Disposable> = LinkedList()
    private var compositeDisposable: CompositeDisposable? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun addFactory(factory: () -> Disposable) {
        disposableFactories.add(factory)
        if (lifecycleOwner.lifecycle.currentState == RESUMED) {
            subscribe(factory)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun resume() {
        compositeDisposable = CompositeDisposable()
        for (factory in disposableFactories) {
            subscribe(factory)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pause() {
        compositeDisposable?.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        disposableFactories.clear()
        compositeDisposable = null
    }

    private fun subscribe(factory: () -> Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable!!.add(factory.invoke())
    }
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
fun LifecycleOwner.createViewDisposableHandler() = ViewDisposableHandler(this)