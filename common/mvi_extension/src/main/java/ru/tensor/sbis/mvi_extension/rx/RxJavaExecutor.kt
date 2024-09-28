package ru.tensor.sbis.mvi_extension.rx

import androidx.annotation.CallSuper
import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by Aleksey Boldinov on 23.08.2022.
 */
abstract class RxJavaExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any> :
    Executor<Intent, Action, State, Message, Label> {

    private val callbacks = AtomicReference<Executor.Callbacks<State, Message, Label>>()
    private val disposable = CompositeDisposable()
    private val serialDisposable = SerialDisposable()

    private val getState: () -> State = { callbacks.get().state }

    final override fun init(callbacks: Executor.Callbacks<State, Message, Label>) {
        this.callbacks.set(callbacks)
    }

    final override fun executeIntent(intent: Intent) {
        executeIntent(intent, getState)
    }

    final override fun executeAction(action: Action) {
        executeAction(action, getState)
    }

    /**
     * Вызывается из [Store] для каждого поступившего намерения.
     *
     * @param intent намерение, полученное в [Store]
     * @param getState возвращает текущее состояние из [Store]
     */
    @MainThread
    protected open fun executeIntent(intent: Intent, getState: () -> State) = Unit

    /**
     * Вызывается из [Store] для каждого действия, созданного [Bootstrapper]
     *
     * @param action действие из [Bootstrapper]
     * @param getState возвращает текущее состояние из [Store]
     */
    @MainThread
    protected open fun executeAction(action: Action, getState: () -> State) = Unit

    @CallSuper
    override fun dispose() {
        disposable.dispose()
        serialDisposable.dispose()
    }

    /**
     * Отправляет сообщение в [Reducer].
     * Обновленное состояние будет сразу доступно после выхода из этого метода.
     */
    @MainThread
    protected fun dispatch(message: Message) {
        callbacks.get().onMessage(message)
    }

    /**
     * Отправляет Label в [Store] для публикации.
     *
     * @param label a `Label` to be published
     */
    @MainThread
    protected fun publish(label: Label) {
        callbacks.get().onLabel(label)
    }

    protected fun launch(operation: () -> Disposable) {
        disposable.add(operation.invoke())
    }

    protected fun serial(operation: () -> Disposable) {
        serialDisposable.set(operation.invoke())
    }
}