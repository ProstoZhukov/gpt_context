package ru.tensor.sbis.communicator.common.util

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable

/**
 * Обертка над [SerialDisposable] для самовоскрешения после вызова dispose
 *
 * @author vv.chekurda
 */
class JesusSerialDisposable : Disposable {

    private var serialDisposable = SerialDisposable()

    operator fun plusAssign(disposable: Disposable) {
        serialDisposable.set(disposable)
    }

    override fun dispose() {
        serialDisposable.dispose()
        serialDisposable = SerialDisposable()
    }

    override fun isDisposed(): Boolean = false
}