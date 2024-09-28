package ru.tensor.sbis.mvi_extension.internal

import androidx.annotation.MainThread
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import ru.tensor.sbis.mvi_extension.LabelBufferStrategy
import java.util.concurrent.atomic.AtomicInteger

/**
 * Реализация [Store] для стандартного использования в Activity и Fragment.
 * Оформление подписок требуется выполнять на главном потоке.
 *
 * Created by Aleksey Boldinov on 30.05.2023.
 */
internal class AndroidStore<in Intent : Any, out State : Any, out Label : Any>(
    private val delegate: Store<Intent, State, Label>,
    labelBufferStrategy: LabelBufferStrategy
) : Store<Intent, State, Label> by delegate {

    private val labelBuffer = LabelBuffer.from<Label>(labelBufferStrategy)
    private val observerCount = AtomicInteger()

    private val labelDisposable = observer<Label>(
        onNext = {
            if (observerCount.get() == 0) {
                labelBuffer.add(it)
            }
        },
        onComplete = {
            labelBuffer.clear()
        }
    ).let {
        when (labelBufferStrategy) {
            LabelBufferStrategy.BeforeInit -> delegate.labels(it)
            is LabelBufferStrategy.Buffer -> {
                delegate.labels(it)
                null // observe forever
            }

            LabelBufferStrategy.NoBuffer -> null
        }
    }

    @MainThread
    override fun states(observer: Observer<State>): Disposable {
        assertOnMainThread()
        return delegate.states(observer)
    }

    @MainThread
    override fun labels(observer: Observer<Label>): Disposable {
        assertOnMainThread()
        val source = delegate.labels(observer).apply {
            if (observerCount.incrementAndGet() == 1) {
                labelBuffer.extractAll {
                    observer.onNext(it)
                }
                labelDisposable?.dispose()
            }
        }
        return object : Disposable by source {
            override fun dispose() {
                source.dispose()
                observerCount.decrementAndGet()
            }
        }
    }
}