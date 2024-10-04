package ru.tensor.sbis.design.folders.support.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable

/**
 * Инструмент для автоматической отписки от реуктивных потоков при уничтожении [Lifecycle]
 *
 * @author ma.kolpakov
 */
internal class SerialLifecycleDisposable private constructor(
    private val disposableContainer: SerialDisposable
) : Disposable by disposableContainer, LifecycleObserver {

    constructor() : this(SerialDisposable())

    fun set(disposable: Disposable?) =
        disposableContainer.set(disposable)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun dispose() {
        disposableContainer.set(null)
    }
}