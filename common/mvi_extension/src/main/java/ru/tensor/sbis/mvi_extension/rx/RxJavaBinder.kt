package ru.tensor.sbis.mvi_extension.rx

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */
class RxJavaBinder(
    private val subscribe: BinderContainer.() -> Unit
) : Binder {

    private val disposables = CompositeDisposable()
    private val container = object : BinderContainer {
        override fun bind(disposable: () -> Disposable) {
            disposables.add(disposable())
        }

        override fun <Event : Any> Observable<Event>.bind(consumer: (Event) -> Unit) {
            disposables.add(subscribe {
                consumer.invoke(it)
            })
        }

        override fun <Model : Any> Observable<Model>.bindTo(viewRenderer: ViewRenderer<Model>) {
            bind { viewRenderer.render(it) }
        }
    }

    override fun start() {
        subscribe.invoke(container)
    }

    override fun stop() {
        disposables.clear()
    }
}

interface BinderContainer {
    infix fun bind(disposable: () -> Disposable)

    infix fun <Event : Any> Observable<Event>.bind(consumer: (Event) -> Unit)

    infix fun <Model : Any> Observable<Model>.bindTo(viewRenderer: ViewRenderer<Model>)
}