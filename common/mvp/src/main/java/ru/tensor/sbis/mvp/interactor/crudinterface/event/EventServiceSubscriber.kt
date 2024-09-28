package ru.tensor.sbis.mvp.interactor.crudinterface.event

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * Реализация подписчика на события от микросервиса
 *
 * @author ev.grigoreva
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class EventServiceSubscriber(
    private val factory: ServiceSubscriptionFactory
) : EventManagerServiceSubscriber {

    private val pushEventSubject = PublishSubject.create<EventData>()
    private val stateEventSubject = PublishSubject.create<StateEvent>()

    private val subscriptionStore = ConcurrentHashMap<String, Subscription>()
    private var eventStateDisposable: Disposable?

    init {
        eventStateDisposable = stateEventSubject.observeOn(Schedulers.single())
            .filter { event -> event.state == State.SUBSCRIBING || event.state == State.UNSUBSCRIBING }
            .subscribe { stateEvent ->
                if (stateEvent.state == State.SUBSCRIBING) {
                    subscribeOnEvents(*stateEvent.events)
                    stateEventSubject.onNext(StateEvent(State.SUBSCRIBED, *stateEvent.events))
                } else {
                    unsubscribeFromEvents(*stateEvent.events)
                    stateEventSubject.onNext(StateEvent(State.UNSUBSCRIBED, *stateEvent.events))
                }
                if (isDisposed) {
                    dispose()
                }
            }
    }

    override fun subscribe(vararg events: String) {
        stateEventSubject.onNext(StateEvent(State.SUBSCRIBING, *events))
    }

    override fun unsubscribe(vararg events: String) {
        stateEventSubject.onNext(StateEvent(State.UNSUBSCRIBING, *events))
    }

    override fun waitForSubscription(eventName: String): Completable {
        return stateEventSubject
            // Обрабатываем только SUBSCRIBED
            .filter { e -> e.state == State.SUBSCRIBED }
            // Проверяем наличие названия события в подписанных
            .filter { e -> e.events.contains(eventName) }
            .take(1)
            .ignoreElements()
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun dispose() {
        stateEventSubject.onComplete()
        pushEventSubject.onComplete()
        eventStateDisposable?.dispose()
        eventStateDisposable = null
        subscriptionStore.clear()
    }

    override fun isDisposed(): Boolean {
        return eventStateDisposable?.isDisposed != false
    }

    override fun getEventDataObservable(): Observable<EventData> {
        return pushEventSubject.observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Выполнить подписку на события.
     *
     * @param events - названия событий
     */
    private fun subscribeOnEvents(vararg events: String) {
        events.forEach { event -> subscriptionStore[event]?.enable() ?: subscribeOnEvent(event) }
    }

    /**
     * Выполнить отписку от событий.
     *
     * @param events - названия событий
     */
    private fun unsubscribeFromEvents(vararg events: String) {
        events.forEach { event -> subscriptionStore[event]?.disable() }
    }

    private fun subscribeOnEvent(event: String) {
        subscriptionStore[event] = factory.createSubscription(event) {
            try {
                pushEventSubject.onNext(it)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    /** Состояние подписки*/
    private enum class State {
        SUBSCRIBING,
        SUBSCRIBED,
        UNSUBSCRIBING,
        UNSUBSCRIBED
    }

    private class StateEvent(val state: State, vararg val events: String)
}