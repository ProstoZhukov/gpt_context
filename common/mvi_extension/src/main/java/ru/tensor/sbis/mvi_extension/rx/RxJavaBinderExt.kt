package ru.tensor.sbis.mvi_extension.rx

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.view.ViewEvents
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable


/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */

fun <Event : Any> ViewEvents<Event>.observableEvents(): Observable<Event> {
    return wrapRxObservable {
        events(it)
    }
}

fun <State : Any> Store<*, State, *>.observableStates(): Observable<State> {
    return wrapRxObservable {
        states(it)
    }
}

fun <Label : Any> Store<*, *, Label>.observableLabels(): Observable<Label> {
    return wrapRxObservable {
        labels(it)
    }
}

private inline fun <T : Any> wrapRxObservable(
    crossinline subscribe: (Observer<T>) -> com.arkivanov.mvikotlin.rx.Disposable
): Observable<T> {
    return Observable.create { emitter ->
        val disposable = subscribe(observer(
            onComplete = {
                emitter.onComplete()
            },
            onNext = {
                emitter.onNext(it)
            }
        ))
        emitter.setDisposable(disposable.toRxDisposable())
    }
}

private fun com.arkivanov.mvikotlin.rx.Disposable.toRxDisposable(): Disposable {
    return object : Disposable {
        override fun dispose() {
            this@toRxDisposable.dispose()
        }

        override fun isDisposed(): Boolean {
            return this@toRxDisposable.isDisposed
        }

    }
}