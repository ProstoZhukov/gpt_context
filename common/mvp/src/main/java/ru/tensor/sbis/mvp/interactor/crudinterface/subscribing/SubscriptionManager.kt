package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

import androidx.annotation.UiThread
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventManagerServiceSubscriber
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Класс для управления подписками и событиями контроллера.
 *
 * @author am.boldinov
 */
@Suppress("unused")
@UiThread
@Deprecated("Устаревший подход, переходим на mvi_extension")
class SubscriptionManager(subscriber: EventManagerServiceSubscriber? = null) {

    /**
     * Держатель подписок.
     */
    private val subscriptionHolder = SubscriptionHolder()

    /**
     * Контроллер событий.
     */
    private val eventController = if (subscriber != null) EventController(subscriber) else null

    private val disposables = CompositeDisposable()

    /**
     * Оформить синхронную подписку.
     */
    private fun batchSubscription(
        events: List<EventParams>,
        subscriptions: List<SubscriptionParams>,
        actions: List<Action>
    ) {
        val waiters = mutableListOf<Completable>()
        if (events.isNotEmpty()) {
            if (eventController == null) {
                throw NullPointerException("Attempt to subscribe on event ${events[0]} without specified subscriber.")
            }
            // Добавляем ожидателя подписки на события по имени
            val waiter = eventController.createWaiter(events)
            waiters.add(waiter)
        }

        for (subscription in subscriptions) {
            // Добавляем ожидателя подписки
            waiters.add(subscriptionHolder.createWaiter(subscription))
        }

        // Начать ожидание завершения подписки
        waitSubscriptions(waiters, actions)

        if (eventController != null) {
            // Подписываемся на события по названиям
            for (event in events) {
                eventController.subscribeOn(event)
            }
        }
    }

    /**
     * Начать ожидание завершения подписок.
     */
    private fun waitSubscriptions(waiters: List<Completable>, callbacks: List<Action>) {
        disposables.add(
            // Формируем ожидателя оформления всех подписок
            Completable.merge(waiters)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Action {
                        // Выполняем указанные действия по окончании подписки
                        callbacks.forEach { it.run() }
                    },
                    FallbackErrorConsumer.DEFAULT
                )
        )
    }

    // region Public methods

    /**
     * Подписаться на событие с указанным названием.
     *
     * @param eventName - название события
     * @param permanent - если true - подписка будет работать постоянно, не реагируя на вызовы pause
     */
    fun subscribeOn(eventName: String, permanent: Boolean) {
        eventController?.subscribeOn(eventName, permanent) ?: throw NullPointerException("Subscriber is not specified")
    }

    /**
     * Управлять указанной подпиской и предоставить доступ к ней по указанному названию.
     *
     * @param name          - название подписки
     * @param subscription  - источник асинхронной подписки
     * @param permanent     - если true - подписка будет работать постоянно, не реагируя на вызовы pause
     */
    fun manage(name: String? = null, subscription: Observable<Subscription>, permanent: Boolean = false) {
        disposables.add(
            subscriptionHolder.createWaiter(SubscriptionParams(name, subscription, permanent))
                .subscribe(Functions.EMPTY_ACTION, FallbackErrorConsumer.DEFAULT)
        )
    }

    /**
     * Добавить обработчик событий.
     */
    fun addConsumer(consumer: Consumer<EventData>) {
        eventController?.addConsumer(consumer)
    }

    /**
     * Удалить обработчик событий.
     */
    fun removeConsumer(consumer: Consumer<EventData>) {
        eventController?.removeConsumer(consumer)
    }

    /**
     * Возобновить все события и подписки.
     */
    fun resume() {
        eventController?.resumeEvents()
        subscriptionHolder.resumeSubscriptions()
    }

    /**
     * Приостановить все события и подписки.
     */
    fun pause() {
        eventController?.pauseEvents()
        subscriptionHolder.pauseSubscriptions()
    }

    /**
     * Возобновить событие.
     */
    fun resumeEvent(event: String) {
        eventController?.resumeEvent(event)
    }

    /**
     * Приостановить событие.
     */
    fun pauseEvent(event: String) {
        eventController?.pauseEvent(event)
    }

    /**
     * Возобновить подписку с указанным названием.
     */
    fun resumeSubscription(subscriptionName: String) {
        subscriptionHolder.resumeSubscription(subscriptionName)
    }

    /**
     * Приостановить подписку с указанным названием.
     */
    fun pauseSubscription(subscriptionName: String) {
        subscriptionHolder.pauseSubscription(subscriptionName)
    }

    /**
     * Высвободить ресурсы, занимаемые менеджером.
     */
    fun dispose() {
        disposables.dispose()
        eventController?.dispose()
        subscriptionHolder.dispose()
    }

    /**
     * Создать batch для оформления составной подписки.
     */
    fun batch(): Batch {
        return Batch()
    }

    // endregion

    /**
     * Вспомогательный класс для конфигурации синхронной подписки
     * на несколько событий и ожидания нескольких подписок на коллбек.
     */
    inner class Batch {

        private val namedEvents = mutableListOf<EventParams>()
        private val subscriptions = mutableListOf<SubscriptionParams>()
        private val actions = mutableListOf<Action>()

        /**
         * Подписаться на событие с указанным названием.
         */
        fun subscribeOn(eventName: String, permanent: Boolean = false): Batch {
            namedEvents.add(EventParams(eventName, permanent))
            return this
        }

        /**
         * Управлять указанной подпиской и предоставить доступ к ней по указанному названию.
         */
        fun manage(name: String? = null, subscription: Observable<Subscription>, permanent: Boolean): Batch {
            subscriptions.add(SubscriptionParams(name, subscription, permanent))
            return this
        }

        /**
         * Указать действие, которое нужно выполнить после успешной подписки.
         */
        fun doAfterSubscribing(action: Action): Batch {
            actions.add(action)
            return this
        }

        /**
         * Выполнить все подписки.
         */
        fun subscribe() {
            batchSubscription(namedEvents, subscriptions, actions)
        }

    }

}
