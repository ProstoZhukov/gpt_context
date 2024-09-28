package ru.tensor.sbis.design.navigation.view.filter

import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * Реализация [BackpressureEventFilter].
 *
 * @author ma.kolpakov
 */
internal class BackpressureEventFilterImpl<EventType : NavigationEvent<*>> : BackpressureEventFilter<EventType> {

    private val eventProcessor = PublishProcessor.create<EventType>()
    private val eventSubject = PublishSubject.create<EventType>()
    override val eventObservable: Observable<EventType> = eventSubject

    private val subscriber = EventSubscriber<EventType>(eventSubject)

    init {
        eventProcessor.onBackpressureLatest().distinctUntilChanged().subscribe(subscriber)
    }

    override fun requestNext() = subscriber.requestNext()

    override fun onChanged(event: EventType?) {
        requireNotNull(event) { "Nullable events are unsupported" }

        eventProcessor.onNext(event)
    }
}