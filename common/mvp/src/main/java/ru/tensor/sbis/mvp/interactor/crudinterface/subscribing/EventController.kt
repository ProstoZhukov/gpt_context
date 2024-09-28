package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber

/**
 * Контроллер событий
 * @author am.boldinov
 */
internal class EventController(private val subscriber: EventManagerServiceSubscriber) {

    /**
     * Обработчики получаемых событий.
     */
    private val eventConsumers = mutableSetOf<Consumer<EventData>>()

    /**
     * Параметры событий.
     */
    private val events = mutableSetOf<EventParams>()

    private val dataDisposable: Disposable

    init {
        dataDisposable = subscribeOnEventData()
    }

    /**
     * Подписаться на события с данными и вернуть completable подписки.
     */
    private fun subscribeOnEventData(): Disposable {
        // Подписываемся на события с данными
        return subscriber.eventDataObservable
            .subscribe(
                Consumer { e -> eventConsumers.forEach { consumer -> consumer.accept(e) } },
                FallbackErrorConsumer.DEFAULT
            )
    }

    /**
     * Создать ожидатель подписки на указанные события.
     */
    fun createWaiter(params: List<EventParams>): Completable {
        // Создаем ожидателя последней подписки по имени
        val last = params.last()
        return subscriber.waitForSubscription(last.eventName)
    }

    /**
     * Подписаться на событие с указанным названием.
     */
    fun subscribeOn(event: String, permanent: Boolean = false) {
        subscribeOn(EventParams(event, permanent))
    }

    /**
     * Подписаться на событие с указанными параметрами.
     */
    fun subscribeOn(event: EventParams) {
        events.add(event)
        subscriber.subscribe(event.eventName)
    }

    /**
     * Возобновить все события.
     */
    fun resumeEvents() {
        for (event in events) {
            subscriber.subscribe(event.eventName)
        }
    }

    /**
     * Приостановить все события.
     */
    fun pauseEvents() {
        for (event in events) {
            if (!event.permanent) {
                subscriber.unsubscribe(event.eventName)
            }
        }
    }

    /**
     * Возобновить событие с указанным названием.
     */
    fun resumeEvent(eventName: String) {
        subscriber.subscribe(eventName)
    }

    /**
     * Приостановить событие с указанным названием.
     */
    fun pauseEvent(eventName: String) {
        for (event in events) {
            if (event.eventName == eventName && !event.permanent) {
                subscriber.unsubscribe(eventName)
            }
        }
    }

    /**
     * Добавить обработчик событий.
     */
    fun addConsumer(consumer: Consumer<EventData>) {
        eventConsumers.add(consumer)
    }

    /**
     * Удалить обработчик событий.
     */
    fun removeConsumer(consumer: Consumer<EventData>) {
        eventConsumers.remove(consumer)
    }

    /**
     * Освободить ресурсы, занимаемые менеджером.
     */
    fun dispose() {
        dataDisposable.dispose()
        subscriber.dispose()
        eventConsumers.clear()
    }

}