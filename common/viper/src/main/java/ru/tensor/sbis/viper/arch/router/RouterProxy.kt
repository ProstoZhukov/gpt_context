package ru.tensor.sbis.viper.arch.router

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.plusAssign
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Класс, в котором содержится либо очередь, либо Router.
 * Если Router - null, то события записываются в очередь и при появлении роутера будут в него переданы.
 * А если Router есть, то события идут напрямую в него.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class RouterProxy<T> {
    companion object {
        // Тротлим переходы через роутеры
        private const val ACTION_TIMEOUT_MILLIS: Long = 300

    }

    private var disposable = SerialDisposable()
    private var disposer = CompositeDisposable()
    private val publishSubject = PublishSubject.create<T.() -> Unit>()

    var router by Delegates.observable<T?>(null) { _, _, newValue ->
        newValue?.apply {
            try {
                disposable.set(publishSubject
                                   .throttleFirst(ACTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                                   .subscribe { newValue.let(it) })

                val mutableIterator = actionQueue.iterator()
                for (action in mutableIterator) {
                    action()
                    mutableIterator.remove()
                }
            } catch (e: IllegalStateException) {
                Timber.e(e)
            }
        }
    }

    @VisibleForTesting
    internal val actionQueue: Queue<T.() -> Unit> = ArrayDeque<T.() -> Unit>()

    // Метод с throttleFirst. Вызывается раз в ACTION_TIMEOUT_MILLIS
    fun execute(action: T.() -> Unit) {
        router?.let { publishSubject.onNext(action) } ?: actionQueue.add(action)
    }

    // Метод без тротла. Если надо вызвать несколько execute подряд
    fun executeWithoutThrottle(action: T.() -> Unit) {
        router?.let(action) ?: actionQueue.add(action)
    }

    //Метод с отложенным выполнением
    fun executeWithTimer(delayMillis: Long, action: T.() -> Unit) {
        disposer += Observable.timer(delayMillis, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { executeWithoutThrottle(action) }
    }

    fun onDestroy() {
        disposable.dispose()
        disposer.dispose()
    }
}