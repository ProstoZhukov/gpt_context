package ru.tensor.sbis.design.navigation.view.filter

import io.reactivex.FlowableSubscriber
import io.reactivex.subjects.Subject
import io.reactivex.subscribers.DefaultSubscriber
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * Реализация [FlowableSubscriber], которая позволяет поочерёдно запрашивать события навигации.
 *
 * @author ma.kolpakov
 * Создан 11/11/2019
 */
internal class EventSubscriber<EventType : NavigationEvent<*>>(
    /**
     * Целевой поток для доставки событий [onNext], [onComplete], [onError]
     */
    private val eventSubject: Subject<EventType>
) : DefaultSubscriber<EventType>() {

    private var nextDelivered = true

    /**
     * Запрос следующего события для обработки подписчиками [eventSubject].
     */
    fun requestNext() {
        if (nextDelivered) {
            request(1L)
            nextDelivered = false
        }
    }

    override fun onStart() = requestNext()
    override fun onComplete() = eventSubject.onComplete()
    override fun onNext(event: EventType) {
        eventSubject.onNext(event)
        nextDelivered = true
    }

    override fun onError(error: Throwable) = eventSubject.onError(error)
}