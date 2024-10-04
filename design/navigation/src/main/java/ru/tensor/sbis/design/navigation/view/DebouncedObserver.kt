package ru.tensor.sbis.design.navigation.view

import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Подписка, которая позволяет отбрасывать повторяющиеся события в рамках указанного интервала.
 *
 * @param consumer конечный потребитель событий.
 * @param errorConsumer обработчик ошибок.
 *
 * @author ma.kolpakov
 */
class DebouncedObserver<T>(
    consumer: Consumer<T?>,
    private val windowDuration: Long,
    errorConsumer: Consumer<Throwable>
) : Observer<T?>, Consumer<T?>, Disposable {

    private val eventSubject = PublishSubject.create<List<T>>()

    private val disposables = CompositeDisposable()
    private val timerDisposable = SerialDisposable()

    private var lastEvent: List<T> = emptyList()
    private var deferredEvent: List<T> = emptyList()

    private var isEventDeferred = false
    private var lastPostedEventTime = 0L

    init {
        disposables.addAll(
            eventSubject
                .subscribe(
                    { eventContainer ->
                        val event = if (eventContainer.isEmpty()) null else eventContainer.first()
                        try {
                            consumer.accept(event)
                        } catch (e: Exception) {
                            errorConsumer.accept(
                                IllegalStateException(
                                    "Unable to handle navigation event. Cause: ${e.message}. Event: $event:", e
                                )
                            )
                        }
                    },
                    errorConsumer
                ),
            timerDisposable
        )
    }

    @Deprecated("Конструктор для обратной совместимости")
    constructor(
        consumer: Consumer<T?>,
        longWindowDuration: Long,
        errorConsumer: Consumer<Throwable>,
        shortWindowDuration: Long = longWindowDuration
    ) : this(consumer, longWindowDuration, errorConsumer)

    override fun onChanged(event: T?) {
        if (!isEventDeferred && lastEvent.isNotEmpty() && event == lastEvent.first()) {
            eventSubject.onNext(lastEvent)
        } else {
            postOrDeferLastEventIfNotAlreadyDeferred(event)
        }
    }

    override fun accept(event: T?) = onChanged(event)

    override fun isDisposed(): Boolean = disposables.isDisposed

    override fun dispose() = disposables.dispose()

    private fun postOrDeferLastEventIfNotAlreadyDeferred(event: T?) {
        val currentTime = System.currentTimeMillis()
        val delta = abs(currentTime - lastPostedEventTime)

        if (isEventDeferred && delta < windowDuration) {
            deferredEvent = event.asSingletonList()
            return
        }

        if (delta >= windowDuration) {
            isEventDeferred = false
            lastPostedEventTime = currentTime
            lastEvent = event.asSingletonList()
            eventSubject.onNext(lastEvent)
        } else {
            val delay = windowDuration - delta
            isEventDeferred = true
            deferredEvent = event.asSingletonList()
            timerDisposable.set(
                Observable.timer(delay, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { isEventDeferred }
                    .subscribe {
                        isEventDeferred = false
                        lastPostedEventTime = System.currentTimeMillis()
                        lastEvent = deferredEvent
                        eventSubject.onNext(deferredEvent)
                    }
            )
        }
    }

    private fun T?.asSingletonList() = if (this == null) emptyList() else listOf(this)
}